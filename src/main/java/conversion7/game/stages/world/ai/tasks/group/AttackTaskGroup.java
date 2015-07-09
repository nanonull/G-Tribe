package conversion7.game.stages.world.ai.tasks.group;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldPath;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.ai.tasks.single.MoveToAttackTask;
import conversion7.game.stages.world.ai.tasks.single.WaitForAttackTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellAmountOfNeighborTeamObjectsComparator;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.AreaObjectTotalPowerComparator;
import conversion7.game.stages.world.objects.AreaObjectTotalPowerWithNeighborsComparator;
import conversion7.game.stages.world.objects.HumanSquad;
import org.slf4j.Logger;

import java.util.Iterator;

public class AttackTaskGroup extends AbstractAreaObjectTaskGroup {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.AttackTaskGroup");
    private static final float NOT_ON_ATTACK_POSITION = 2.85f;
    private static final float CLOSEST_ATTACK_POSITION_DISTANCE = 2f;

    private Array<AreaObject> targets;
    private AreaObject armiesReadyToAttackTarget;

    public AttackTaskGroup(AiTeamController aiTeamController, Array<AreaObject> targets) {
        super(aiTeamController, DEFAULT_PRIORITY);
        refreshTargets(targets);
    }

    /** Targets and Actors are updated from outside */
    @Override
    public boolean execute() {
        if (LOG.isDebugEnabled()) LOG.debug("execute");
        removeDefeatedTargets();

        // if 0 targets - task will be completed on next step
        if (targets.size == 0) {
            return true;
        }

        // there could be 0 attackers
        if (actors.size == 0) {
            return false;
        }

        // so simple...
        // there is no final check about 'is target's power still so weak as on previous step?' before trigger attack
        if (armiesReadyToAttackTarget != null) {
            // check who could attack
            for (AreaObject actor : actors) {
                if (actor.isNeighborOf(armiesReadyToAttackTarget)) {
                    actor.attack(armiesReadyToAttackTarget);
                    return false;
                }
            }

            //if there was no attack - continue from beginning
            armiesReadyToAttackTarget = null;
        }

        // use sorting to select target (the weakest including neighbor support)
        WorldThreadLocalSort.instance().sort(targets, AreaObjectTotalPowerWithNeighborsComparator.instance());
        AreaObject theWeakestTarget = targets.get(targets.size - 1);

        // mark all cells around targets with path_index = NOT_FOR_MOVE to avoid move near enemy
        for (AreaObject target : targets) {
            for (Cell neighborCell : target.getCell().getNeighborCells()) {
                if (neighborCell.couldBeSeized()) {
                    neighborCell.pathData.setObstacleFilter(true);
                }
            }
        }

        // move to target and wait for other team members on position
        Array<AreaObject> armiesOnAttackPosition = PoolManager.ARRAYS_POOL.obtain();
        for (AreaObject actor : actors) {
            float distanceTo = actor.getCell().distanceTo(theWeakestTarget.getCell());
            if (distanceTo > NOT_ON_ATTACK_POSITION) { // just move
                actor.addTaskToWipSet(new MoveToAttackTask(actor, theWeakestTarget.getCell()));
            } else if (distanceTo > CLOSEST_ATTACK_POSITION_DISTANCE) {
                Array<PathData> path = WorldPath.getPath(actor.getCell(), theWeakestTarget.getCell());
                if (path != null) {
                    Cell nextStepCell = path.get(0).cell;
                    if (nextStepCell.distanceTo(theWeakestTarget.getCell()) < actor.getCell().distanceTo(theWeakestTarget.getCell())) {
                        // move on distance 2 (the closest as pre-attack position)
                        actor.addTaskToWipSet(new MoveToAttackTask(actor, nextStepCell));
                    } else {
                        // wait
                        actor.addTaskToWipSet(new WaitForAttackTask(actor));
                        armiesOnAttackPosition.add(actor);
                    }
                }
            } else {
                // wait
                actor.addTaskToWipSet(new WaitForAttackTask(actor));
                armiesOnAttackPosition.add(actor);
            }
        }

        // each step check if figures at pre-attack place (get all ON_POSITION armies) (A) could win (2.a maybe some timeout if figure moves too long)
        //  if yes - move on attack position armies which are already on attack position (F)
        //  if no - wait
        if (AreaObject.getObjectsPower(armiesOnAttackPosition) > theWeakestTarget.getPowerWithNeighborSupport()) {
            resetCustomObstaclesAroundTarget(theWeakestTarget);

            CellAmountOfNeighborTeamObjectsComparator.instance().sort(theWeakestTarget.getCell().getNeighborCells(), aiTeamController.team);
            Array<HumanSquad> placedArmies = PoolManager.ARRAYS_POOL.obtain();
            for (int i = 0; i < theWeakestTarget.getCell().getNeighborCells().size; i++) {
                if (placedArmies.size == actors.size) {
                    break;
                }
                Cell attackPlace = theWeakestTarget.getCell().getNeighborCells().get(i);
                Array<HumanSquad> objectsOfTeam = attackPlace.getNeighborObjectsOfTeam(HumanSquad.class, aiTeamController.team);
                if (objectsOfTeam.size > 0) {
                    WorldThreadLocalSort.instance().sort(objectsOfTeam, AreaObjectTotalPowerComparator.instance());
                    for (HumanSquad army : objectsOfTeam) {
                        if (!placedArmies.contains(army, true)) {
                            army.addTaskToWipSet(new MoveToAttackTask(army, attackPlace));
                            placedArmies.add(army);
                            break;
                        }
                    }
                }
                PoolManager.ARRAYS_POOL.free(objectsOfTeam);
            }
            PoolManager.ARRAYS_POOL.free(placedArmies);

            armiesReadyToAttackTarget = theWeakestTarget;
        }

        // TODO retreat if not ready to battle
        // if target moves closer - retreat neighbor to enemy 1 cell back
        // OR even check each step if smbdy has neighbor enemy
        return false;
    }

    private void removeDefeatedTargets() {
        Iterator<AreaObject> targetsIterator = targets.iterator();
        while (targetsIterator.hasNext()) {
            AreaObject next = targetsIterator.next();
            if (next.isRemovedFromWorld()) {
                targetsIterator.remove();
            }
        }
    }

    private void resetCustomObstaclesAroundTarget(AreaObject target) {
        // if armies will finally attack - reset all custom obstacles around  target
        for (Cell neighborCell : target.getCell().getNeighborCells()) {
            neighborCell.pathData.setObstacleFilter(false);
        }
    }

    public void refreshTargets(Array<AreaObject> targets) {
        this.targets = targets;
    }
}
