package conversion7.game.stages.battle_deprecated.calculation;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class Path {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int PATH_WEIGHT_LIMIT = 99;

    /**
     * Recursively calculate weights on map and search for target cell.<br>
     * 1st path node - will be target cell.<br>
     * Returns null if no path found.
     */
    static public Array<Cell> getPath(Cell from, Cell target, Step step) {
        Array<Cell> path = PoolManager.ARRAYS_POOL.obtain();

        for (int x = 0; x < step.battle.getTotalWidth(); x++) {
            for (int y = 0; y < step.battle.getTotalHeight(); y++) {
                step.cells[x][y].weight = 0;
                step.cells[x][y]._timesCalculated = 0;
            }
        }
        step.targetFound = false;

        from.weight = 10;
        from.calculateWeightsAround(target);

        if (step.targetFound) {
            // BUILD PATH
            if (LOG.isDebugEnabled()) LOG.debug("target found!");

            Cell pathPoint = target;
            while (pathPoint != null) {
                path.add(pathPoint);
                pathPoint = pathPoint.findTheSmallestWeightCell();
            }

            if (LOG.isDebugEnabled()) LOG.debug("path length = " + path.size);
            return path;
        }
        PoolManager.ARRAYS_POOL.free(path);
        if (LOG.isDebugEnabled()) LOG.debug("path target NOT found - not available!");
        return null;
    }

}
