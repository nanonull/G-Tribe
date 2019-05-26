package conversion7.game.stages.world.ai_deprecated.tasks.single;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellTotalValueWithNeighborsComparator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class FindComfortableCellTaskOldOld extends MoveTaskOld implements SearchCellTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    Array<Cell> visitedKeyCells = PoolManager.ARRAYS_POOL.obtain();

    public FindComfortableCellTaskOldOld(AbstractSquad owner) {
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

        // if current cell is good - complete
        if (isCellMatchesTargetCondition(owner.getLastCell())) {
            if (LOG.isDebugEnabled()) LOG.debug("completed at: " + owner.getLastCell());
            complete();
            return true;
        }

        Cell nextCell = getTheBestVisibleCell();
        if (nextCell == null) {
            nextCell = getCellForMoveInRandomDirection();
        }
        continueSearchingTargetCell(nextCell);

        return false;
    }

    @Override
    public boolean isCellMatchesTargetCondition(Cell cell) {
        return cell.isComfortableFor(owner);
    }

    @Override
    public void complete() {
        super.complete();
        PoolManager.ARRAYS_POOL.free(visitedKeyCells);
    }

    @Override
    public void continueSearchingTargetCell(Cell theBestCell) {
        if (theBestCell == null || hasVisitedAlready(theBestCell)) {
            initMoveInRandomDirection();
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
            if (lastMoveTargetDiff != null) {
                repeatPreviousMovement();
            } else {
                initMoveInRandomDirection();
            }
        }
    }

}
