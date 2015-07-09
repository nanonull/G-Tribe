package conversion7.game.stages.world.ai.tasks.single;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellTotalValueWithNeighborsComparator;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class FindComfortableCellTask extends MoveTask implements SearchCellTask {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.FindComfortableCellTask");

    Array<Cell> visitedKeyCells = PoolManager.ARRAYS_POOL.obtain();

    public FindComfortableCellTask(AreaObject owner) {
        super(owner, null, DEFAULT_PRIORITY);
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

        // if current cell is good - complete
        if (isCellMatchesTargetCondition(owner.getCell())) {
            if (LOG.isDebugEnabled()) LOG.debug("completed at: " + owner.getCell());
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
    public void continueSearchingTargetCell(Cell theBestCell) {
        if (theBestCell == null || hasVisitedAlready(theBestCell)) {
            initMoveInRandomDirection();
        } else {
            initMoveToTheBestCell(theBestCell);
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
            if (lastMoveTargetDiff != null) {
                repeatPreviousMovement();
            } else {
                initMoveInRandomDirection();
            }
        }
    }

}
