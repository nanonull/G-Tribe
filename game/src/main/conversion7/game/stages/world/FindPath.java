package conversion7.game.stages.world;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import org.slf4j.Logger;

public class FindPath {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int PATH_DISTANCE_MIN_LIMIT = 20;

    /**
     * closedList The list of Nodes not searched yet, sorted by their distance to the goal as guessed by our heuristic.
     */
    private static final Array<PathData> closedList = new Array<>();
    private static final Array<PathData> openList = new Array<>();

    /** 1st item = 1st step from startCell */
    public static Array<PathData> getPath(Cell startCell, Cell goalCell) {

        if (goalCell.pathData.isObstacle()) {
            goalCell = goalCell.pathData.findNotObstacleClosestToGoalAndStart(startCell, goalCell);
            if (LOG.isDebugEnabled()) LOG.debug("new not-obstacle goal is: " + goalCell);
            if (goalCell == null) {
                return null;
            }
        }

        float distanceToGoal = startCell.distanceTo(goalCell);
        final int PATH_DISTANCE_LIMIT =
                Math.max(Math.round(distanceToGoal * distanceToGoal), PATH_DISTANCE_MIN_LIMIT);
        startCell.pathData.distanceFromStart = 0;
        openList.add(startCell.pathData);
        Array<PathData> foundPath = null;

        //while we haven't reached the goal yet
        while (openList.size != 0) {

            //get the first Node from non-searched Node list, sorted by lowest distance from our goal as guessed by our heuristic
            openList.sort();
            PathData currentOpen = openList.get(0);

            // check if our current Node location is the goal Node. If it is, we are done.
            if (currentOpen.cell == goalCell) {
                foundPath = reconstructPathFromGoal(currentOpen);
                PathData lastStep = foundPath.get(foundPath.size - 1);
                lastStep.targetNode = true;
                if (LOG.isDebugEnabled()) LOG.debug("path found, length: " + foundPath.size);
                break;
            }

            //move current Node to the closed (already searched) list
            openList.removeValue(currentOpen, true);
            closedList.add(currentOpen);

            //go through all the current Nodes neighbors and calculate if one should be our next step
            for (Cell neighborCell : currentOpen.cell.getCellsAround()) {
                boolean updateNeighbor;

                //if we have already searched this Node, don't bother and continue to the next one
                if (closedList.contains(neighborCell.pathData, true))
                    continue;

                //also just continue if the neighbor is an obstacle
                if (!neighborCell.pathData.isObstacle()) {

                    // calculate how long the path is if we choose this neighbor as the next step in the path
                    float neighborDistanceFromStart = currentOpen.distanceFromStart + currentOpen.cell.distanceTo(neighborCell);
                    // TODO limit path search. SMA* could return the best from limited path: https://en.wikipedia.org/wiki/SMA*
                    if (neighborDistanceFromStart > PATH_DISTANCE_LIMIT) {
                        continue;
                    }

                    //add neighbor to the open list if it is not there
                    if (!openList.contains(neighborCell.pathData, true)) {
                        openList.add(neighborCell.pathData);
                        updateNeighbor = true;
                        //if neighbor is closer to start it could also be better
                    } else if (neighborDistanceFromStart < currentOpen.distanceFromStart) {
                        updateNeighbor = true;
                    } else {
                        updateNeighbor = false;
                    }
                    // set neighbors parameters if it is better
                    if (updateNeighbor) {
                        neighborCell.pathData.previousNode = currentOpen;
                        neighborCell.pathData.distanceFromStart = neighborDistanceFromStart;
                        neighborCell.pathData.heuristicDistanceFromGoal = neighborCell.distanceTo(goalCell);
                    }
                }
            }
        }

        for (PathData pathData : closedList) {
            pathData.reset();
        }
        closedList.clear();

        for (PathData pathData : openList) {
            pathData.reset();
        }
        openList.clear();

        if (foundPath != null && LOG.isDebugEnabled()) {
            for (PathData pathData : foundPath) {
                LOG.debug(pathData.toString());
            }
        }

        return foundPath;
    }

    private static Array<PathData> reconstructPathFromGoal(PathData pathData) {
        Array<PathData> path = PoolManager.ARRAYS_POOL.obtain();
        while (pathData.previousNode != null) {
            path.insert(0, pathData.copy());
            pathData = pathData.previousNode;
        }
        return path;
    }

    public static boolean wasPathFound(Array<PathData> path) {
        if (path == null) {
            return false;
        }
        PathData lastNode = path.get(path.size - 1);
        return lastNode.targetNode;
    }

    public static boolean isAccesible(Cell from, Cell to) {
        Array<PathData> path = getPath(from, to);
        return wasPathFound(path);
    }
}
