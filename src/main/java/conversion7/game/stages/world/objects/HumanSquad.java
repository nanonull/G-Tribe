package conversion7.game.stages.world.objects;

import conversion7.engine.customscene.ModelActor;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.BattleFigure;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.ai.tasks.single.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AttackAction;
import conversion7.game.stages.world.objects.actions.CreateTownAction;
import conversion7.game.stages.world.objects.actions.ExploreAction;
import conversion7.game.stages.world.objects.actions.FollowAction;
import conversion7.game.stages.world.objects.actions.PatrolAction;
import conversion7.game.stages.world.objects.actions.RitualAction;
import conversion7.game.stages.world.objects.actions.ShareFoodAction;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class HumanSquad extends AbstractSquad {

    private static final Logger LOG = Utils.getLoggerForClass();

    /** If true - army will help neighbor allies in battles */
    public boolean helpForAllies = false;

    public HumanSquad(Cell cell, Team team) {
        super(cell, team);
        if (LOG.isDebugEnabled()) LOG.debug("< create " + getName());
        initActions();
    }

    @Override
    public void initActions() {
        super.initActions();
        actions.add(new CreateTownAction(this));
        actions.add(new PatrolAction(this));
        actions.add(new ExploreAction(this));
        actions.add(new FollowAction(this));
        actions.add(new AttackAction(this));
    }

    @Override
    public void validateActions() {
        super.validateActions();
        if (getFoodStorage().getFood() > 0) {
            addActionIfAbsent(RitualAction.class);
            addActionIfAbsent(ShareFoodAction.class);
        } else {
            removeActionIfExist(RitualAction.class);
            removeActionIfExist(ShareFoodAction.class);
        }
    }

    @Override
    public void buildModelActor() {
        ModelActor modelActor = PoolManager.ARMY_MODEL_POOL.obtain();
        sceneGroup.addNode(modelActor);
        this.setModelActor(modelActor);
        sceneGroup.createBoundingBox(0.7f * BattleFigure.ACTOR_Z,
                0.7f * BattleFigure.ACTOR_Z,
                BattleFigure.ACTOR_BOX_HEIGHT);
        sceneGroup.boundBoxActor.translate(
                MathUtils.toEngineCoords(0, 0, BattleFigure.ACTOR_BOX_HEIGHT / 2));
    }

    @Override
    public void customObjectAi() {
        if (LOG.isDebugEnabled()) LOG.debug("customObjectAi " + this);
        for (Cell cell : getCell().getNeighborCells()) {
            if (cell.isSeized()
                    && couldJoinToTeam(cell.getSeizedBy())) {
                getTeam().joinSquad((AbstractSquad) cell.getSeizedBy());
                break;
            }
        }
        if (getActiveTask() == null) {
            moveToTheClosestAiNode();
        }
    }

    private void moveToTheClosestAiNode() {
        AiNode closestNode = getCell().getTheClosestNodeFrom(getTeam().getAiTeamController());
        if (closestNode == null) {
            return;
        }

        if (closestNode.origin.distanceTo(this.getCell()) > AiNode.DEFAULT_RADIUS) {
            if (LOG.isDebugEnabled()) LOG.debug("move to the closest node: " + this);
            Cell freeCellInNodeArea = closestNode.getRandomFreeCellInNodeArea();
            if (freeCellInNodeArea != null) {
                setCurrentObjectTask(new MoveTask(this, freeCellInNodeArea));
            } else {
                LOG.warn(String.format("there is no free cells in %s for %s", closestNode, this));
            }
        }
    }

    @Override
    public boolean couldJoinToTeam(AreaObject targetToBeJoined) {
        if (targetToBeJoined instanceof HumanSquad
                && !this.getTeam().equals(targetToBeJoined.getTeam())) {
            int totalTribesSeparationValue = World.getTotalTribesSeparationValue();
            int tribeSeparationValue = targetToBeJoined.getTeam().getTribeSeparationValue();
            return tribeSeparationValue > totalTribesSeparationValue;
        }
        return false;
    }

}
