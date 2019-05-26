package conversion7.game.stages.world.ai_deprecated.tasks.single;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai_deprecated.AiNode;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.ai_deprecated.events.team.NewNodeFoundEvent;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellTotalValueWithNeighborsComparator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class FindNodeTaskOld extends MoveTaskOld implements SearchCellTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    Array<Cell> visitedKeyCells = PoolManager.ARRAYS_POOL.obtain();
    private Point2s previousTargetDiff;

    public FindNodeTaskOld(AbstractSquad owner) {
        super(owner, null);
    }

    private Cell getTheBestVisibleCell() {
        Cell theBestCell = null;
        Array<Cell> visibleCellsWithoutMyCell = owner.getVisibleCellsAround();
        WorldThreadLocalSort.instance().sort(visibleCellsWithoutMyCell, CellTotalValueWithNeighborsComparator.instance());
        for (Cell visibleCell : visibleCellsWithoutMyCell) {
            if (visibleCell.hasFreeMainSlot()
                    && visibleCell.isFarEnoughFromTeamNodes(owner.getTeam().getAiTeamControllerOld())) {
                theBestCell = visibleCell;
                break;
            }
        }
        return theBestCell;
    }

    @Override
    public void setMoveTarget(Cell target) {
        if (target != null) {
            super.setMoveTarget(target);
            this.previousTargetDiff = owner.getLastCell().getDiffWithCell(target);
        }
    }

    @Override
    protected void initRadius() {
        radius = Math.round(AiTeamControllerOld.MINIMUM_DISTANCE_BETWEEN_NODES);
        randomMoveAmplitude = radius * 2 + 1;
    }

    @Override
    public void cancel() {
        super.cancel();
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
            owner.getTeam().getAiTeamControllerOld().addEvent(new NewNodeFoundEvent(theBestCell));
            complete();
            return true;
        } else {
            continueSearchingTargetCell(theBestCell);
        }
        return false;
    }

    /** move in contra-direction from the closest team Node */
    private void initMoveFarFromTeamNodes() {
        if (LOG.isDebugEnabled()) LOG.debug("initMoveFarFromTeamNodes");
        AiNode theClosestNodeTo = owner.getLastCell().getTheClosestNodeFrom(owner.getTeam().getAiTeamControllerOld());

        if (theClosestNodeTo != null) {
            Point2s diffWithNode = owner.getLastCell().getDiffWithCell(theClosestNodeTo.origin);
            diffWithNode.trimAndFill(radius).multiply(-1);
            setMoveTarget(owner.getLastCell().getArea().getCell(owner.getLastCell(), diffWithNode));
        } else {
            initMoveInRandomDirection();
        }
    }

    @Override
    public boolean isCellMatchesTargetCondition(Cell theBestCell) {
        return theBestCell.isGoodToBeNode();
    }

    @Override
    public void complete() {
        super.complete();
        PoolManager.ARRAYS_POOL.free(visitedKeyCells);
    }

    @Override
    public void continueSearchingTargetCell(Cell theBestCell) {
        if (hasVisitedAlready(theBestCell)) {
            initMoveFarFromTeamNodes();
        } else {
            initMoveToTheBestCell(theBestCell);
        }
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
        if (!theBestCell.isNeighborOf(owner.getLastCell()) || !theBestCell.hasSquad()) {
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

}
