package conversion7.game.stages.battle.calculation;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle.contollers.MoveController;
import org.slf4j.Logger;

import java.awt.*;

public class Cell extends Point {

    private static final Logger LOG = Utils.getLoggerForClass();

    public FigureStepParams seizedBy;
    Step step;
    public MoveController registeredMovementOnMe;


    public Cell(int x, int y, Step g) {
        this.x = x;
        this.y = y;
        step = g;
    }


    public void seize(FigureStepParams figureParams) {
        this.seizedBy = figureParams;
        figureParams.cell = this;
    }

    public boolean isSeized() {
        return seizedBy != null;
    }

    public void free() {
        seizedBy.cell = null;
        this.seizedBy = null;
    }


    public int _timesCalculated = 0;
    public int weight = 0;

    public void calculateWeightsAround(Cell target) {

        if (_timesCalculated < 3) {
            if (LOG.isDebugEnabled()) LOG.debug("calculateWeightsAround on " + this + ". weight = " + weight);

            if (target == null) {
                Utils.error("calculateWeightsAround > target = " + target);
                return;
            }

            Array<Cell> toBeCalculated = PoolManager.ARRAYS_POOL.obtain();

            for (Point point : GdxgConstants.CELLS_AROUND) {
                Cell cell = step.getCell(point.x + x, point.y + y);
                if (cell != null) {
                    if (cell.equals(target)) {
                        if (LOG.isDebugEnabled())
                            LOG.debug(" path target found - " + cell + " at weight = " + this.weight);
                        step.targetFound = true;
                    } else {
                        if (cell.isAvailableForMove() && weight < Path.PATH_WEIGHT_LIMIT) {
                            int nextWeight = weight + (this.isStraightTo(cell) ? 10 : 14);
                            if (cell.weight == 0 || nextWeight < cell.weight) {
                                cell.weight = nextWeight;
                                toBeCalculated.add(cell);
                            }
                        }
                    }
                }
            }

            for (Cell cell : toBeCalculated) {
                cell.calculateWeightsAround(target);
            }
        }

    }


    public boolean isStraightTo(Cell to) {
        return this.x == to.x || this.y == to.y;
    }


    @Override
    public String toString() {
        return "CELL: " + x + "," + y;
    }


    public Cell findTheSmallestWeightCell() {
        Cell smallest = null;

        for (Point point : GdxgConstants.CELLS_AROUND) {
            Cell cell = step.getCell(point.x + x, point.y + y);
            if (cell != null) {
                if (cell.weight == 10) { // start point
                    return null; // END BUILDING PATH I'VE FOUND START CELL
                } else if (cell.weight != 0) { // seized cell
                    if (smallest == null) {
                        smallest = cell;
                    } else {
                        // TODO [AI] implement additional Cell comparing by parameters
                        if (cell.weight < smallest.weight) {
                            smallest = cell;
                        }
                    }
                }
            }
        }

        return smallest;
    }


    public boolean isAvailableForMove() {
        if ((this.registeredMovementOnMe == null) &&
                ((this.seizedBy == null)
                        || (this.seizedBy != null
                        && this.seizedBy.action != null
                        && this.seizedBy.action.getClass().equals(MoveController.class))
                )) {
            return true;
        }
        return false;
    }

}
