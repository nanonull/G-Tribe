package conversion7.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.geometry.Point2s;
import conversion7.game.stages.battle_deprecated.BattleSide;

import java.awt.*;
import java.util.HashMap;

public class BattleConstants {
    public static final Array<Point> CELLS_AROUND = new Array<>();
    public static final HashMap<BattleSide, Point> NEXT_FORWARD_CELL = new HashMap<>();
    public static final HashMap<BattleSide, Array<Point>> SEARCH_FAR_TARGET_CELLS = new HashMap<>();
    static Array<Point> SEARCH_FAR_TARGET_CELLS_LEFT = new Array<>();
    static Array<Point> SEARCH_FAR_TARGET_CELLS_RIGHT = new Array<>();
    static Array<Point> SEARCH_FAR_TARGET_CELLS_UP = new Array<>();
    static Array<Point> SEARCH_FAR_TARGET_CELLS_DOWN = new Array<>();
    public static final HashMap<BattleSide, BattleSide> INVERT_TEAM = new HashMap<>();
    public static final ObjectMap<Point2s, Integer> YAW_BY_DIRECTION = new ObjectMap<>();

    static {
        CELLS_AROUND.add(new Point(-1, 1));
        CELLS_AROUND.add(new Point(-1, 0));
        CELLS_AROUND.add(new Point(-1, -1));

        CELLS_AROUND.add(new Point(0, 1));
        CELLS_AROUND.add(new Point(0, -1));

        CELLS_AROUND.add(new Point(1, 1));
        CELLS_AROUND.add(new Point(1, 0));
        CELLS_AROUND.add(new Point(1, -1));
    }

    static {
        NEXT_FORWARD_CELL.put(BattleSide.LEFT_YELLOW, new Point(1, 0));
        NEXT_FORWARD_CELL.put(BattleSide.RIGHT_RED, new Point(-1, 0));
        NEXT_FORWARD_CELL.put(BattleSide.UP_BLUE, new Point(0, -1));
        NEXT_FORWARD_CELL.put(BattleSide.DOWN, new Point(0, 1));
    }

    static {
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.LEFT_YELLOW, SEARCH_FAR_TARGET_CELLS_LEFT);
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.RIGHT_RED, SEARCH_FAR_TARGET_CELLS_RIGHT);
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.UP_BLUE, SEARCH_FAR_TARGET_CELLS_UP);
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.DOWN, SEARCH_FAR_TARGET_CELLS_DOWN);


        //SEARCH_FAR_TARGET_CELLS_LEFT
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(-1, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(-1, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(-1, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(-1, -3));

        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(0, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(0, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(0, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(0, -3));

        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(1, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(1, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(1, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(1, -3));

        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, 1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, 0));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, -1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(2, -3));

        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, 1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, 0));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, -1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(3, -3));

        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, 3));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, 2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, 1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, 0));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, -1));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, -2));
        SEARCH_FAR_TARGET_CELLS_LEFT.add(new Point(4, -3));


        //SEARCH_FAR_TARGET_CELLS_RIGHT
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(1, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(1, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(1, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(1, -3));

        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(0, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(0, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(0, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(0, -3));

        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-1, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-1, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-1, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-1, -3));

        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, 1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, 0));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, -1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-2, -3));

        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, 1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, 0));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, -1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-3, -3));

        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, 3));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, 2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, 1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, 0));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, -1));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, -2));
        SEARCH_FAR_TARGET_CELLS_RIGHT.add(new Point(-4, -3));


        //SEARCH_FAR_TARGET_CELLS_UP
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, 1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, 1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, 1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, 1));

        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, 0));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, 0));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, 0));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, 0));

        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, -1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, -1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, -1));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, -1));

        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(1, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(0, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-1, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, -2));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, -2));

        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(1, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(0, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-1, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, -3));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, -3));

        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(3, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(2, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(1, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(0, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-1, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-2, -4));
        SEARCH_FAR_TARGET_CELLS_UP.add(new Point(-3, -4));


        //SEARCH_FAR_TARGET_CELLS_DOWN
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, -1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, -1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, -1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, -1));

        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, 0));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, 0));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, 0));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, 0));

        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, 1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, 1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, 1));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, 1));

        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(1, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(0, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-1, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, 2));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, 2));

        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(1, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(0, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-1, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, 3));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, 3));

        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(3, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(2, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(1, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(0, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-1, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-2, 4));
        SEARCH_FAR_TARGET_CELLS_DOWN.add(new Point(-3, 4));
    }

    static {
        INVERT_TEAM.put(BattleSide.LEFT_YELLOW, BattleSide.RIGHT_RED);
        INVERT_TEAM.put(BattleSide.RIGHT_RED, BattleSide.LEFT_YELLOW);
        INVERT_TEAM.put(BattleSide.UP_BLUE, BattleSide.DOWN);
        INVERT_TEAM.put(BattleSide.DOWN, BattleSide.UP_BLUE);
    }

    static {
        YAW_BY_DIRECTION.put(new Point2s(0, -1), 0);
        YAW_BY_DIRECTION.put(new Point2s(1, -1), 45);
        YAW_BY_DIRECTION.put(new Point2s(1, 0), 90);
        YAW_BY_DIRECTION.put(new Point2s(1, 1), 135);
        YAW_BY_DIRECTION.put(new Point2s(0, 1), 180);
        YAW_BY_DIRECTION.put(new Point2s(-1, 1), 225);
        YAW_BY_DIRECTION.put(new Point2s(-1, 0), 270);
        YAW_BY_DIRECTION.put(new Point2s(-1, -1), 315);
    }

    public static int getYawByDirection(Point direction) {
        for (ObjectMap.Entry<Point2s, Integer> entry : YAW_BY_DIRECTION.entries()) {
            if (entry.key.equals(direction)) {
                return entry.value;
            }
        }
        throw new GdxRuntimeException("Unknown direction: " + direction);
    }
}
