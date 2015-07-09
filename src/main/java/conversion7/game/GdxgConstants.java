package conversion7.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.BattleSide;
import conversion7.game.stages.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;


public abstract class GdxgConstants {

    public static boolean NO_ATTACK_MISS = false;
    public static boolean DEVELOPER_MODE =
            PropertiesLoader.getIntProperty("DEVELOPER.MODE") == 1;

    public static boolean AUTO_BATTLE_FOR_PLAYER =
            PropertiesLoader.getIntProperty("DEVELOPER.AUTO_BATTLE_FOR_PLAYER") == 1;

    public static boolean AI_AREA_OBJECT_ENABLED =
            PropertiesLoader.getIntProperty("AI_AREA_OBJECT_ENABLED") == 1;

    public static Locale locale = new Locale(PropertiesLoader.getProperty("LOCALE"));
    public static int SCREEN_WIDTH_IN_PX = PropertiesLoader.getIntProperty("SCREEN_WIDTH_IN_PX");
    public static int SCREEN_HEIGHT_IN_PX = PropertiesLoader.getIntProperty("SCREEN_HEIGHT_IN_PX");
    public static final String HINT_SPLITTER = "#";

    public static boolean AREA_VIEWER_FOG_OF_WAR_ENABLED =
            PropertiesLoader.getIntProperty("AreaViewer.FOG_OF_WAR") == 1;

    public static boolean CAMERA_3D_HANDLE_CHANGE_AREA_FOCUS =
            PropertiesLoader.getIntProperty("DEBUG.CAMERA_3D_HANDLE_CHANGE_AREA_FOCUS") == 1;

    public static final Array<Point> CELLS_AROUND = new Array<>();

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

    public static final HashMap<BattleSide, Point> NEXT_FORWARD_CELL = new HashMap<>();

    static {
        NEXT_FORWARD_CELL.put(BattleSide.LEFT, new Point(1, 0));
        NEXT_FORWARD_CELL.put(BattleSide.RIGHT, new Point(-1, 0));
        NEXT_FORWARD_CELL.put(BattleSide.UP, new Point(0, -1));
        NEXT_FORWARD_CELL.put(BattleSide.DOWN, new Point(0, 1));
    }


    public static final HashMap<BattleSide, Array<Point>> SEARCH_FAR_TARGET_CELLS = new HashMap<>();
    private static Array<Point> SEARCH_FAR_TARGET_CELLS_LEFT = new Array<>();
    private static Array<Point> SEARCH_FAR_TARGET_CELLS_RIGHT = new Array<>();
    private static Array<Point> SEARCH_FAR_TARGET_CELLS_UP = new Array<>();
    private static Array<Point> SEARCH_FAR_TARGET_CELLS_DOWN = new Array<>();

    static {
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.LEFT, SEARCH_FAR_TARGET_CELLS_LEFT);
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.RIGHT, SEARCH_FAR_TARGET_CELLS_RIGHT);
        SEARCH_FAR_TARGET_CELLS.put(BattleSide.UP, SEARCH_FAR_TARGET_CELLS_UP);
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


    public static final HashMap<BattleSide, BattleSide> INVERT_TEAM = new HashMap<>();

    static {
        INVERT_TEAM.put(BattleSide.LEFT, BattleSide.RIGHT);
        INVERT_TEAM.put(BattleSide.RIGHT, BattleSide.LEFT);
        INVERT_TEAM.put(BattleSide.UP, BattleSide.DOWN);
        INVERT_TEAM.put(BattleSide.DOWN, BattleSide.UP);
    }

    public static final ObjectMap<Point2s, Integer> YAW_BY_DIRECTION = new ObjectMap<>();

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
        Utils.error("Unknown direction: " + direction);
        return 0;
    }

    public static final Array<Point2s> SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS = new Array<>();

    static {
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s());
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_SEGMENTS, 0));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, World.HEIGHT_IN_SEGMENTS));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_SEGMENTS, World.HEIGHT_IN_SEGMENTS));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_SEGMENTS, 0));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, -World.HEIGHT_IN_SEGMENTS));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_SEGMENTS, -World.HEIGHT_IN_SEGMENTS));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_SEGMENTS, World.HEIGHT_IN_SEGMENTS));
        SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_SEGMENTS, -World.HEIGHT_IN_SEGMENTS));
    }

    public static final Array<Point2s> CELL_THROUGH_WORLD_BOUNDS_VARIANTS = new Array<>();

    static {
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s());
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_CELLS, 0));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, World.HEIGHT_IN_CELLS));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_CELLS, World.HEIGHT_IN_CELLS));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_CELLS, 0));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, -World.HEIGHT_IN_CELLS));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_CELLS, -World.HEIGHT_IN_CELLS));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_CELLS, World.HEIGHT_IN_CELLS));
        CELL_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_CELLS, -World.HEIGHT_IN_CELLS));
    }

    public static final Array<Point2s> AREA_THROUGH_WORLD_BOUNDS_VARIANTS = new Array<>();

    static {
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s());
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_AREAS, 0));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, World.HEIGHT_IN_AREAS));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_AREAS, World.HEIGHT_IN_AREAS));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_AREAS, 0));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(0, -World.HEIGHT_IN_AREAS));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_AREAS, -World.HEIGHT_IN_AREAS));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(-World.WIDTH_IN_AREAS, World.HEIGHT_IN_AREAS));
        AREA_THROUGH_WORLD_BOUNDS_VARIANTS.add(new Point2s(World.WIDTH_IN_AREAS, -World.HEIGHT_IN_AREAS));
    }

}
