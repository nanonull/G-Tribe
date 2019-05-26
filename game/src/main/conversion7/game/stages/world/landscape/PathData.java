package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;

/** Stores info about filter for current path */
public class PathData implements Comparable<PathData> {

    private static final Array<PathData> OBSTACLE_FILTER_LIST = new Array<>();

    public float distanceFromStart;
    public float heuristicDistanceFromGoal;
    /** Used in custom path filters */
    public boolean obstacleAsFiltered;
    public Cell cell;
    public PathData previousNode;
    public boolean targetNode;

    public PathData(Cell cell) {
        this.cell = cell;
    }

    public boolean isObstacle() {
        return !cell.hasFreeMainSlot() || cell.hasSquad() || obstacleAsFiltered;
    }

    public void setObstacleFilter(boolean valueForNextPathSearch) {
        obstacleAsFiltered = valueForNextPathSearch;
        PathData.OBSTACLE_FILTER_LIST.add(this);
    }

    public static void resetObstacleFilters() {
        for (PathData pathData : PathData.OBSTACLE_FILTER_LIST) {
            pathData.obstacleAsFiltered = false;
        }
        PathData.OBSTACLE_FILTER_LIST.clear();
    }

    @Override
    public String toString() {
        return "PathData on " + cell.toString();
    }

    public void reset() {
        distanceFromStart = 0;
        heuristicDistanceFromGoal = 0;
        previousNode = null;
    }

    @Override
    public int compareTo(PathData otherNode) {
        return Float.compare(heuristicDistanceFromGoal + distanceFromStart,
                otherNode.heuristicDistanceFromGoal + otherNode.distanceFromStart);
    }

    public Cell findNotObstacleCellClosestToStart(Cell startCell) {
        Cell theClosestCell = CellDistanceToComparator.instance().sort(cell.getCellsAround(), startCell).get(0);
        if (theClosestCell.pathData.isObstacle()) {
            return theClosestCell.pathData.findNotObstacleCellClosestToStart(startCell);
        } else {
            return theClosestCell;
        }
    }

    public Cell findNotObstacleClosestToGoalAndStart(Cell startCell, Cell goalCell) {
        if (startCell.isNeighborOf(goalCell)) {
            return null;
        }
        Array<Cell> foundNotObstacle = PoolManager.ARRAYS_POOL.obtain();
        // find not obstacle cells around me
        for (int radius = 1; foundNotObstacle.size == 0; radius++) {
            Array<Cell> cellsAroundOnRadius = this.cell.getCellsAroundOnRadius(radius);
            for (Cell cellAround : cellsAroundOnRadius) {
                if (!cellAround.pathData.isObstacle()) {
                    foundNotObstacle.add(cellAround);
                }
            }
            PoolManager.ARRAYS_POOL.free(cellsAroundOnRadius);
        }

        Cell theClosestCell = CellDistanceToComparator.instance().sort(foundNotObstacle, startCell).get(0);
        PoolManager.ARRAYS_POOL.free(foundNotObstacle);
        return theClosestCell;
    }

    public PathData copy() {
        PathData pathData = new PathData(cell);
        pathData.distanceFromStart = this.distanceFromStart;
        pathData.heuristicDistanceFromGoal = this.heuristicDistanceFromGoal;
        return pathData;
    }
}
