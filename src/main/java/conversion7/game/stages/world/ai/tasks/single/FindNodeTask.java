package conversion7.game.stages.world.ai.tasks.single;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.ai.events.team.NewNodeFoundEvent;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellTotalValueWithNeighborsComparator;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class FindNodeTask extends MoveTask implements SearchCellTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.FindNodeAiTask");

    Array<Cell> visitedKeyCells = PoolManager.ARRAYS_POOL.obtain();
    private Point2s previousTargetDiff;

    public FindNodeTask(AreaObject owner) {
        super(owner, null, DEFAULT_PRIORITY);
    }

    @Override
    protected void initRadius() {
        RADIUS = Math.round(AiTeamController.MINIMUM_DISTANCE_BETWEEN_NODES);
        RANDOM_MOVE_AMPLITUDE = RADIUS * 2 + 1;
    }

    @Override
    public void cancel() {
        super.cancel();
        PoolManager.ARRAYS_POOL.free(visitedKeyCells);
    }

    @Override
    public void complete() {
        super.complete();
        PoolManager.ARRAYS_POOL.free(visitedKeyCells);
    }

    @Override
    public boolean execute() {
        if (hasMoveTarget()) {
            if (!super.moveStep()) {
                return false;
            }
        }

        Cell theBestCell = getTheBestVisibleCell();
        if (theBestCell == null) {
            initMoveFarFromTeamNodes();
            return false;
        }

        if (isCellMatchesTargetCondition(theBestCell)) {
            if (LOG.isDebugEnabled()) LOG.debug("new node found at: " + theBestCell);
            owner.getTeam().getAiTeamController().addEvent(new NewNodeFoundEvent(theBestCell));
            complete();
            return true;
        } else {
            continueSearchingTargetCell(theBestCell);
        }
        return false;
    }

    @Override
    public boolean isCellMatchesTargetCondition(Cell theBestCell) {
        return theBestCell.isGoodToBeNode();
    }

    @Override
    public void continueSearchingTargetCell(Cell theBestCell) {
        if (hasVisitedAlready(theBestCell)) {
            initMoveFarFromTeamNodes();
        } else {
            initMoveToTheBestCell(theBestCell);
        }
    }

    @Override
    public void setMoveTarget(Cell target) {
        if (target != null) {
            super.setMoveTarget(target);
            this.previousTargetDiff = owner.getCell().diffWithCell(target);
        }
    }

    private Cell getTheBestVisibleCell() {
        Cell theBestCell = null;
        Array<Cell> visibleCells = owner.getVisibleCellsAroundOnly();
        WorldThreadLocalSort.instance().sort(visibleCells, CellTotalValueWithNeighborsComparator.instance());
        for (Cell visibleCell : visibleCells) {
            if (visibleCell.hasLandscapeAvailableForMove()
                    && visibleCell.isFarEnoughFromTeamNodes(owner.getTeam().getAiTeamController())) {
                theBestCell = visibleCell;
                break;
            }
        }
        PoolManager.ARRAYS_POOL.free(visibleCells);
        return theBestCell;
    }

    private boolean hasVisitedAlready(Cell theBestCell) {
        boolean visited = visitedKeyCells.contains(theBestCell, true);
        visitedKeyCells.add(theBestCell);
        if (visitedKeyCells.size == 5) {
            visitedKeyCells.removeIndex(0);
        }
        return visited;
    }

    private void initMoveToTheBestCell(Cell theBestCell) {
        if (!theBestCell.isNeighborOf(owner.getCell()) || !theBestCell.isSeized()) {
            if (LOG.isDebugEnabled()) LOG.debug("move to theBestCell: " + theBestCell);
            setMoveTarget(theBestCell);
        } else {
            if (previousTargetDiff == null) {
                initMoveInRandomDirection();
            } else {
                repeatPreviousMovement();
            }
        }
    }

    /** move in contra-direction from the closest team Node */
    private void initMoveFarFromTeamNodes() {
        if (LOG.isDebugEnabled()) LOG.debug("initMoveFarFromTeamNodes");
        AiNode theClosestNodeTo = owner.getCell().getTheClosestNodeFrom(owner.getTeam().getAiTeamController());

        if (theClosestNodeTo != null) {
            Point2s diffWithNode = owner.getCell().diffWithCell(theClosestNodeTo.origin);
            diffWithNode.trimAndFill(RADIUS).multiply(-1);
            setMoveTarget(owner.getCell().getArea().getCell(owner.getCell(), diffWithNode));
        } else {
            initMoveInRandomDirection();
        }
    }

}
