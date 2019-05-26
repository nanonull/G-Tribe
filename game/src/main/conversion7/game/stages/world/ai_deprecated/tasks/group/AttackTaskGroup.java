package conversion7.game.stages.world.ai_deprecated.tasks.group;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.FindPath;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.ai_deprecated.tasks.single.MoveToAttackTaskOld;
import conversion7.game.stages.world.ai_deprecated.tasks.single.WaitForAttackTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellAmountOfNeighborTeamObjectsComparator;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.AreaObjectTotalPowerComparator;
import conversion7.game.stages.world.objects.AreaObjectTotalPowerWithNeighborsComparator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import org.slf4j.Logger;

import java.util.Iterator;

public class AttackTaskGroup extends AbstractSquadTaskGroup {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final float NOT_ON_ATTACK_POSITION = 2.85f;
    private static final float CLOSEST_ATTACK_POSITION_DISTANCE = 2f;

    private Array<AbstractSquad> targets;
    private AbstractSquad armiesReadyToAttackTarget;

    public AttackTaskGroup(AiTeamControllerOld aiTeamControllerOld, Array<AbstractSquad> targets) {
        super(aiTeamControllerOld);
        refreshTargets(targets);
    }

    public void refreshTargets(Array<AbstractSquad> targets) {
        this.targets = targets;
    }

    /** Targets and Actors are updated from outside */
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
            for (AbstractSquad actor : actors) {
                if (actor.isNeighborOf(armiesReadyToAttackTarget)) {
                    actor.meleeAttack(armiesReadyToAttackTarget);
                    return false;
                }
            }

            //if there was no attack - continue from beginning
            armiesReadyToAttackTarget = null;
        }

        // use sorting to select target (the weakest including neighbor support)
        WorldThreadLocalSort.instance().sort(targets, AreaObjectTotalPowerWithNeighborsComparator.instance());
        AbstractSquad theWeakestTarget = targets.get(targets.size - 1);

        // mark all cells around targets with path_index = NOT_FOR_MOVE to avoid move near enemy
        for (AreaObject target : targets) {
            for (Cell neighborCell : target.getLastCell().getCellsAround()) {
                if (neighborCell.canBeSeized()) {
                    neighborCell.pathData.setObstacleFilter(true);
                }
            }
        }

        // move to target and wait for other team members on position
        Array<AbstractSquad> armiesOnAttackPosition = PoolManager.ARRAYS_POOL.obtain();
        for (AbstractSquad actor : actors) {
            float distanceTo = actor.getLastCell().distanceTo(theWeakestTarget.getLastCell());
            if (distanceTo > NOT_ON_ATTACK_POSITION) { // just move
                actor.addTaskToWipSet(new MoveToAttackTaskOld(actor, theWeakestTarget.getLastCell()));
            } else if (distanceTo > CLOSEST_ATTACK_POSITION_DISTANCE) {
                Array<PathData> path = FindPath.getPath(actor.getLastCell(), theWeakestTarget.getLastCell());
                if (path != null) {
                    Cell nextStepCell = path.get(0).cell;
                    if (nextStepCell.distanceTo(theWeakestTarget.getLastCell()) < actor.getLastCell().distanceTo(theWeakestTarget.getLastCell())) {
                        // move on distance 2 (the closest as pre-attack position)
                        actor.addTaskToWipSet(new MoveToAttackTaskOld(actor, nextStepCell));
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
        if (AbstractSquad.getObjectsPower(armiesOnAttackPosition) > theWeakestTarget.getPowerWithNeighborSupport()) {
            resetCustomObstaclesAroundTarget(theWeakestTarget);

            CellAmountOfNeighborTeamObjectsComparator.instance().sort(theWeakestTarget.getLastCell().getCellsAround(), aiTeamControllerOld.team);
            Array<WorldSquad> placedArmies = PoolManager.ARRAYS_POOL.obtain();
            for (int i = 0; i < theWeakestTarget.getLastCell().getCellsAround().size; i++) {
                if (placedArmies.size == actors.size) {
                    break;
                }
                Cell attackPlace = theWeakestTarget.getLastCell().getCellsAround().get(i);
                Array<WorldSquad> objectsOfTeam = attackPlace.getNeighborObjectsOfTeam(WorldSquad.class, aiTeamControllerOld.team);
                if (objectsOfTeam.size > 0) {
                    WorldThreadLocalSort.instance().sort(objectsOfTeam, AreaObjectTotalPowerComparator.instance());
                    for (WorldSquad army : objectsOfTeam) {
                        if (!placedArmies.contains(army, true)) {
                            army.addTaskToWipSet(new MoveToAttackTaskOld(army, attackPlace));
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
        Iterator<AbstractSquad> targetsIterator = targets.iterator();
        while (targetsIterator.hasNext()) {
            AreaObject next = targetsIterator.next();
            if (next.isRemovedFromWorld()) {
                targetsIterator.remove();
            }
        }
    }

    private void resetCustomObstaclesAroundTarget(AreaObject target) {
        // if armies will finally attack - reset all custom obstacles around  target
        for (Cell neighborCell : target.getLastCell().getCellsAround()) {
            neighborCell.pathData.setObstacleFilter(false);
        }
    }
}
