package conversion7.game.stages.world.objects;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai.tasks.single.ChaseTask;
import conversion7.game.stages.world.ai.tasks.single.EscapeTask;
import conversion7.game.stages.world.ai.tasks.single.FindComfortableCellTask;
import conversion7.game.stages.world.ai.tasks.single.MergeTask;
import conversion7.game.stages.world.ai.tasks.single.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.effects.ScareBeastEffect;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

import static conversion7.game.stages.battle.BattleFigure.ACTOR_Z;

public class AnimalHerd extends AbstractSquad {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static int SEARCH_ATTACK_TARGETS_IN_RADIUS = 2;

    public AnimalHerd(Cell cell, Team animalTeam) {
        super(cell, animalTeam);
        initActions();
    }

    @Override
    public String toString() {
        return super.toString() + " id=" + getId();
    }

    @Override
    public void buildModelActor() {
        ModelActor modelActor = PoolManager.ARMY_MODEL_POOL.obtain();
        sceneGroup.addNode(modelActor);
        this.setModelActor(modelActor);
        float boxHeight = 0.5f * ACTOR_Z;
        sceneGroup.createBoundingBox(0.7f * ACTOR_Z, 0.7f * ACTOR_Z, boxHeight);
        sceneGroup.boundBoxActor.translate(MathUtils.toEngineCoords(0, 0, boxHeight / 2));
    }

    @Override
    public void ai() {
        customObjectAi();
        applyNewTaskFromWipSet();
    }

    @Override
    public void customObjectAi() {
        if (LOG.isDebugEnabled()) LOG.debug("customObjectAi " + this);
        Array<AreaObject> objectsAround = getCell().getObjectsAroundFromToRadiusInclusively(1, SEARCH_ATTACK_TARGETS_IN_RADIUS);
        WorldThreadLocalSort.instance().sort(objectsAround, AreaObjectTotalPowerComparator.instance());

        if (needToEscapeOrChaseOrMerge(objectsAround)) {
            if (LOG.isDebugEnabled()) LOG.debug("needToEscapeOrChaseOrMerge " + this);
        } else if (needToFindComfortCell()) {
            if (LOG.isDebugEnabled()) LOG.debug("needToFindComfortCell " + this);
        } else {
            if (LOG.isDebugEnabled()) LOG.debug("nothing to do for " + this);
            if (Utils.RANDOM.nextInt(10) < 2) {
                if (LOG.isDebugEnabled()) LOG.debug("...want change position a little bit");
                Cell newCell = getCell().getArea().getCell(getCell(), Utils.RANDOM.nextInt(3) - 1, Utils.RANDOM.nextInt(3) - 1);
                addTaskToWipSet(new MoveTask(this, newCell));
            }
        }

        PoolManager.ARRAYS_POOL.free(objectsAround);
    }


    private boolean needToFindComfortCell() {
        if (!getCell().isComfortableFor(this)) {
            addTaskToWipSet(new FindComfortableCellTask(this));
            return true;
        }
        return false;
    }

    private boolean needToEscapeOrChaseOrMerge(Array<AreaObject> objectsAround) {
        boolean hasSuccessfulTask = false;
        for (AreaObject otherObject : objectsAround) {
            if (otherObject.isAnimalHerd() && this.hasEqualAnimalClassWith((AnimalHerd) otherObject)) {
                addTaskToWipSet(new MergeTask(this, otherObject));
                hasSuccessfulTask = true;
                continue;
            }

            boolean thisHasMorePower = hasMorePowerThan(otherObject);
            if (thisHasMorePower) {
                // there is no stronger squad around
                if (isChasingCancelled()) {
                    // chasing would be possible after squad takes 1 turn pause now
                    setChasingCancelled(false);
                    break;
                } else {
                    addTaskToWipSet(new ChaseTask(this, otherObject));
                    hasSuccessfulTask = true;
                    break;
                }
            }

            if (otherObject.isSquad()) {
                addTaskToWipSet(new EscapeTask(this, otherObject));
                hasSuccessfulTask = true;
                break;
            }
        }
        return hasSuccessfulTask;
    }

    public boolean hasMorePowerThan(AreaObject otherObject) {
        if (otherObject.hasEffect(ScareBeastEffect.class)) {
            return getPower() > otherObject.getPower() * 2;
        } else {
            return super.hasMorePowerThan(otherObject);
        }
    }

    @Override
    public boolean couldJoinToTeam(AreaObject targetToBeJoined) {
        return false;
    }

    private boolean hasEqualAnimalClassWith(AnimalHerd animalHerd) {
        return getUnits().get(0).getClass().equals(animalHerd.getUnits().get(0).getClass());
    }


}
