package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle_deprecated.calculation.Cell;
import org.fest.assertions.api.Fail;
import org.slf4j.Logger;

import java.awt.*;
import java.util.HashMap;

public class ArmyPlaceArea {

    private static final Logger LOG = Utils.getLoggerForClass();

    private Battle battle;

    private final HashMap<BattleSide, Array<Point>> ARMY_PLACE_AREAS = new HashMap<>();
    private Array<Point> ARMY_PLACE_LEFT = PoolManager.ARRAYS_POOL.obtain();
    private Array<Point> ARMY_PLACE_RIGHT = PoolManager.ARRAYS_POOL.obtain();
    private Array<Point> ARMY_PLACE_UP = PoolManager.ARRAYS_POOL.obtain();
    private Array<Point> ARMY_PLACE_DOWN = PoolManager.ARRAYS_POOL.obtain();

    public ArmyPlaceArea(Battle battle) {
        this.battle = battle;
        initPlaceLists();
    }

    private void initPlaceLists() {
        ARMY_PLACE_AREAS.put(BattleSide.LEFT_YELLOW, ARMY_PLACE_LEFT);
        ARMY_PLACE_AREAS.put(BattleSide.RIGHT_RED, ARMY_PLACE_RIGHT);
        ARMY_PLACE_AREAS.put(BattleSide.UP_BLUE, ARMY_PLACE_UP);
        ARMY_PLACE_AREAS.put(BattleSide.DOWN, ARMY_PLACE_DOWN);

        // ARMY_PLACE_LEFT
        for (int x = 0; x < battle.getGridRadius(); x++) {
            for (int y = battle.getGridRadius(); y < battle.getTotalHeight() - battle.getGridRadius(); y++) {
                ARMY_PLACE_LEFT.add(new Point(x, y));
            }
        }

        // ARMY_PLACE_RIGHT
        for (int x = battle.getTotalWidth() - 1; x >= battle.getTotalWidth() - battle.getGridRadius(); x--) {
            for (int y = battle.getTotalHeight() - battle.getGridRadius() - 1; y >= battle.getGridRadius(); y--) {
                ARMY_PLACE_RIGHT.add(new Point(x, y));
            }
        }
    }

    /** repeat place cycle from the beginning if end of area reached */
    public void placeFigureOnSavedOrNextAvailableCell(BattleFigure battleFigure) {
        LOG.info("placeFigureOnSavedOrNextAvailableCell " + battleFigure);
        Cell freeCell = getNextAvailableCellFromIncluding(battleFigure);
        battle.round.startStep.addFigure(battleFigure, freeCell);
        if (battleFigure.getBattleSide().equals(BattleSide.LEFT_YELLOW)) {
            battleFigure.figureVisualGroup.setRotation(90, 0, 0);
        } else if (battleFigure.getBattleSide().equals(BattleSide.RIGHT_RED)) {
            battleFigure.figureVisualGroup.setRotation(-90, 0, 0);
        } else {
            Fail.fail("not implemented yet");
        }
    }

    public Point2s getBattleFieldPositionByMirrorPosition(Point mirrorPosition, BattleSide battleSide) {
        if (battleSide.equals(BattleSide.LEFT_YELLOW)) {
            return new Point2s(mirrorPosition.x, mirrorPosition.y + battle.getGridRadius());
        } else if (battleSide.equals(BattleSide.RIGHT_RED)) {
            return new Point2s(battle.getTotalWidth() - 1 - mirrorPosition.x,
                    battle.getTotalHeight() - battle.getGridRadius() - 1 - mirrorPosition.y);
        }
        Utils.error("add all sides!");
        return null;
    }

    /**
     * Get position used for place configuration.<br>
     * Position is not related to side.
     */
    public Point2s getMirrorPositionByBattleFieldPosition(Cell battleCell, BattleSide battleSide) {
        Point battlePosition = new Point2s(battleCell.x, battleCell.y);
        if (battleSide.equals(BattleSide.LEFT_YELLOW)) {
            return new Point2s(battlePosition.x, battlePosition.y - battle.getGridRadius());
        } else if (battleSide.equals(BattleSide.RIGHT_RED)) {
            return new Point2s((battle.getTotalWidth() - 1) - battlePosition.x,
                    (battle.getTotalHeight() - battle.getGridRadius() - 1) - battlePosition.y);
        }
        Utils.error("add all sides!");
        return null;
    }

    /** throw error if place cycle comes on saved position again */
    private Cell getNextAvailableCellFromIncluding(BattleFigure battleFigure) {
        Array<Point> points = ARMY_PLACE_AREAS.get(battleFigure.getBattleSide());

        // check saved position
        if (battleFigure.savedMirrorPosition != null) {
            Cell cell = battle.round.startStep.getCell(
                    getBattleFieldPositionByMirrorPosition(battleFigure.savedMirrorPosition, battleFigure.getBattleSide()));
            if (!cell.isSeized()) {
                return cell;
            }
        }

        // check places from beginning of area
        for (Point point : points) {
            Cell cell = battle.round.startStep.getCell(point);
            if (!cell.isSeized()) {
                return cell;
            }
        }
        Utils.error("Army place area has not enough free cells!");
        return null;
    }

}
