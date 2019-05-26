package conversion7.game;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldSettings;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.Locale;


public abstract class GdxgConstants {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static boolean DEVELOPER_MODE = false;

    public static boolean AREA_OBJECT_AI = true;
    public static boolean BATTLE_AI = true;

    public static Locale locale = new Locale(PropertiesLoader.getProperty("LOCALE"));
    public static int SCREEN_WIDTH_IN_PX = PropertiesLoader.getIntProperty("SCREEN_WIDTH_IN_PX");
    public static int SCREEN_HEIGHT_IN_PX = PropertiesLoader.getIntProperty("SCREEN_HEIGHT_IN_PX");
    public static final String HINT_SPLITTER = " ## ";

    public static boolean CAMERA_3D_HANDLE_CHANGE_AREA_FOCUS =
            PropertiesLoader.getIntProperty("DEBUG.CAMERA_3D_HANDLE_CHANGE_AREA_FOCUS") == 1;

    private static boolean alwaysDontStealthOnCheck;
    private static boolean alwaysStealthOnCheck;
    private static boolean resurrectUnitInBattleIfResistFailed;
    private static boolean alwaysDontResurrectUnitsInBattle;

    public final static int WIDTH_IN_AREAS =
            PropertiesLoader.getIntProperty("World.WIDTH_IN_AREAS");
    public final static int HEIGHT_IN_AREAS =
            PropertiesLoader.getIntProperty("World.HEIGHT_IN_AREAS");
    public static final int TRIBE_SEPARATION_VALUE_MAX = 10000;
    public static final int INCREASE_TRIBES_SEPARATION_PER_BATTLE = 10;
    public static final int INCREASE_TRIBES_SEPARATION_PER_NEW_CLASS = 100;

    public static final WorldSettings WORLD_SETTINGS_GAME = new WorldSettings(
            GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS
            , false
            , true, 2, -1, -1);
    public static final WorldSettings WORLD_SETTINGS_DEV_MODE = new WorldSettings(
            GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS
            , false
            , false, 1, -1, -1,99999999);
    public static final WorldSettings WORLD_SETTINGS_EMPTY_WORLD = new WorldSettings(
            GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS
            , false
            , false, 0, 0, 0);
    public static final WorldSettings WORLD_SETTINGS_TEST = new WorldSettings(
            GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS
            , true
            , false, 1, 0, 0, 9999);

//    public static final WorldSettings WORLD_SETTINGS_TEST_PRETTY = new WorldSettings(
//            GdxgConstants.WIDTH_IN_AREAS, GdxgConstants.HEIGHT_IN_AREAS
//            , true
//            , true, false, 0, 0, 9999)
//
//            ;

    public static boolean isAlwaysDontResurrectUnitsInBattle() {
        return alwaysDontResurrectUnitsInBattle;
    }

    public static void setAlwaysDontResurrectUnitsInBattle(boolean alwaysDontResurrectUnitsInBattle) {
        if (alwaysDontResurrectUnitsInBattle) {
            Assert.assertFalse(resurrectUnitInBattleIfResistFailed, "Could not be active at the same time!");
        }
        GdxgConstants.alwaysDontResurrectUnitsInBattle = alwaysDontResurrectUnitsInBattle;
    }

    @Deprecated
    public static boolean isAlwaysDontStealthOnCheck() {
        return alwaysDontStealthOnCheck;
    }

    public static void setAlwaysDontStealthOnCheck(boolean alwaysDontStealthOnCheck) {
        LOG.info("alwaysDontStealthOnCheck {}", alwaysDontStealthOnCheck);
        if (alwaysDontStealthOnCheck) {
            setAlwaysStealthOnCheck(false);
        }
        GdxgConstants.alwaysDontStealthOnCheck = alwaysDontStealthOnCheck;
    }

    @Deprecated
    public static boolean isAlwaysStealthOnCheck() {
        return alwaysStealthOnCheck;
    }

    public static void setAlwaysStealthOnCheck(boolean alwaysStealthOnCheck) {
        LOG.info("alwaysStealthOnCheck {}", alwaysStealthOnCheck);
        if (alwaysStealthOnCheck) {
            setAlwaysDontStealthOnCheck(false);
        }
        GdxgConstants.alwaysStealthOnCheck = alwaysStealthOnCheck;
    }

    public static boolean isResurrectUnitInBattleIfResistFailed() {
        return resurrectUnitInBattleIfResistFailed;
    }

    public static void setResurrectUnitInBattleIfResistFailed(boolean resurrectUnitInBattleIfResistFailed) {
        if (resurrectUnitInBattleIfResistFailed) {
            Assert.assertFalse(alwaysDontResurrectUnitsInBattle, "Could not be active at the same time!");
        }
        GdxgConstants.resurrectUnitInBattleIfResistFailed = resurrectUnitInBattleIfResistFailed;
    }

    public static void resetFakeResurrectionInBattleFlags() {
        resurrectUnitInBattleIfResistFailed = false;
        alwaysDontResurrectUnitsInBattle = false;
        // for check
        GdxgConstants.setAlwaysDontResurrectUnitsInBattle(false);
        GdxgConstants.setResurrectUnitInBattleIfResistFailed(false);
    }

    public static void resetStealthHackFlags() {
        setAlwaysDontStealthOnCheck(false);
        setAlwaysStealthOnCheck(false);
    }
}
