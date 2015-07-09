package conversion7.game.stages.battle.calculation;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle.BattleThreadLocalSort;
import conversion7.game.stages.battle.contollers.AttackController;
import conversion7.game.stages.battle.contollers.IdleController;
import conversion7.game.stages.battle.contollers.MoveController;
import conversion7.game.utils.collections.Comparators;
import org.slf4j.Logger;

import java.awt.*;

public class BattleAi {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static int calculateFigureAction(FigureStepParams figureStepParams) {
        if (LOG.isDebugEnabled()) LOG.debug("< calculateFigureAction for " + figureStepParams);

        if (getNeighborEnemy(figureStepParams)) {
            return 1;
        }

        if (getFarEnemy(figureStepParams)) {
            return 1;
        }

        if (getMoveForward(figureStepParams)) {
            return 1;
        }

        figureStepParams.action = new IdleController(figureStepParams, null);

        if (LOG.isDebugEnabled()) LOG.debug("> calculateFigureAction - nothing to do...");
        return 0;
    }

    private static boolean getNeighborEnemy(FigureStepParams figureStepParams) {

        if (LOG.isDebugEnabled()) LOG.debug("< getNeighborEnemy");

        Array<FigureStepParams> neighborEnemies = PoolManager.ARRAYS_POOL.obtain();

        // find all neighbor enemies
        for (int i = 0; i < GdxgConstants.CELLS_AROUND.size; i++) {
            Point p = GdxgConstants.CELLS_AROUND.get(i);
            Cell nearCell = figureStepParams.step.getCell(p.x + figureStepParams.cell.x, p.y + figureStepParams.cell.y);

            if (nearCell != null && nearCell.seizedBy != null &&
                    !nearCell.seizedBy.battleFigure.getBattleSide().equals(figureStepParams.battleFigure.getBattleSide())) {
                // set distance to 0 to skip it in comparison
                nearCell.seizedBy.distanceToMeFromLastAttacker = 0f;
                neighborEnemies.add(nearCell.seizedBy);
            }
        }

        // sorting enemies, the most valuable will be 1st in list
        BattleThreadLocalSort.instance().sort(neighborEnemies, Comparators.FIGURE_VALUE_COMPARATOR);

        // get 1st enemy and attack it
        if (neighborEnemies.size > 0) {
            figureStepParams.action = new AttackController(neighborEnemies.get(0), figureStepParams);
            if (LOG.isDebugEnabled()) LOG.debug("> getNeighborEnemy END - true");
            return true;
        }

        if (LOG.isDebugEnabled()) LOG.debug("> getNeighborEnemy END - false");
        return false;
    }

    private static boolean getFarEnemy(FigureStepParams figureStepParams) {
        if (LOG.isDebugEnabled()) LOG.debug("< getFarEnemy");

        Array<Point> farScope = GdxgConstants.SEARCH_FAR_TARGET_CELLS.get(figureStepParams.battleFigure.getBattleSide());
        FigureStepParams chosenEnemy = null;
        Cell nextStepCellToEnemy = null;

        // find all far enemies
        Array<FigureStepParams> farEnemies = PoolManager.ARRAYS_POOL.obtain();
        for (Point p : farScope) {
            Cell farCell = figureStepParams.step.getCell(p.x + figureStepParams.cell.x, p.y + figureStepParams.cell.y);
            if (LOG.isDebugEnabled()) LOG.debug(" check farScope on farCell = " + farCell);

            if (farCell != null && farCell.seizedBy != null
                    && !farCell.seizedBy.battleFigure.getBattleSide().equals(figureStepParams.battleFigure.getBattleSide())) {
                farEnemies.add(farCell.seizedBy);
                // calculate distance to include it in comparison
                // distance = sqrt((х2— х1)* + (y2— y1)*)
                int diffX = farCell.seizedBy.cell.x - figureStepParams.cell.x;
                int diffY = farCell.seizedBy.cell.y - figureStepParams.cell.y;
                farCell.seizedBy.distanceToMeFromLastAttacker = Math.sqrt(diffX * diffX + diffY * diffY);
            }
        }

        // sorting enemies, the most valuable will be 1st in list
        BattleThreadLocalSort.instance().sort(farEnemies, Comparators.FIGURE_VALUE_COMPARATOR);

        Array<Cell> path = null;
        // search path from 1st enemy
        // break searching when path to enemy found
        for (FigureStepParams enemy : farEnemies) {
            if (enemy.action != null && enemy.action.getClass().equals(MoveController.class)) {
                // calculate path in perspective with enemy movement:
                path = Path.getPath(figureStepParams.cell, ((MoveController) enemy.action).target, figureStepParams.step);
            } else {
                path = Path.getPath(figureStepParams.cell, enemy.cell, figureStepParams.step);
            }

            if (path != null) {
                chosenEnemy = enemy;
                if (path.size > 1) {
                    nextStepCellToEnemy = path.get(path.size - 1);
                } else {
                    // will stay and wait for enemy
                    // TODO TASK if wait for enemy then collect some additional power
                    nextStepCellToEnemy = figureStepParams.cell;
                }
                break;
            }
        }

        // the best enemy should be 1st in the farScope. I'll move to him or wait.
        if (chosenEnemy != null) {
            if (nextStepCellToEnemy.equals(figureStepParams.cell)) {
                if (LOG.isDebugEnabled()) LOG.debug(" will wait on this position for enemy: " + nextStepCellToEnemy);
                figureStepParams.action = new IdleController(figureStepParams, path.get(0));
                if (LOG.isDebugEnabled()) LOG.debug("> getFarEnemy END - true");
            } else {
                if (LOG.isDebugEnabled()) LOG.debug(" will move to get enemy: " + nextStepCellToEnemy);
                figureStepParams.action = new MoveController(figureStepParams, nextStepCellToEnemy);
                if (LOG.isDebugEnabled()) LOG.debug("> getFarEnemy END - true");
            }
            PoolManager.ARRAYS_POOL.free(path);
            return true;
        }

        if (LOG.isDebugEnabled()) LOG.debug("> getFarEnemy END - false");
        return false;
    }


    private static boolean getMoveForward(FigureStepParams figureStepParams) {
        if (LOG.isDebugEnabled()) LOG.debug("< getMoveForward. Me: " + figureStepParams);

        Point p = GdxgConstants.NEXT_FORWARD_CELL.get(figureStepParams.battleFigure.getBattleSide());
        Cell c = figureStepParams.step.getCell(p.x + figureStepParams.cell.x, p.y + figureStepParams.cell.y);
        if (c != null && c.isAvailableForMove()) {
            figureStepParams.action = new MoveController(figureStepParams, c);
            if (LOG.isDebugEnabled()) LOG.debug("> getMoveForward - true");
            return true;
        }
        if (LOG.isDebugEnabled()) LOG.debug("> getMoveForward - false");
        return false;
    }
}
