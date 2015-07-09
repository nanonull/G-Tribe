package conversion7.game.stages.world.creator;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.terrain.TerrainDataGrid;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.classes.theOldest.Ardipithecus;
import conversion7.game.classes.theOldest.ArdipithecusKadabba;
import conversion7.game.classes.theOldest.ArdipithecusRamidus;
import conversion7.game.classes.theOldest.Chororapithecus;
import conversion7.game.classes.theOldest.Dryopithecus;
import conversion7.game.classes.theOldest.Gorilla;
import conversion7.game.classes.theOldest.Orrorin;
import conversion7.game.classes.theOldest.OrrorinTugenensis;
import conversion7.game.classes.theOldest.Pan;
import conversion7.game.classes.theOldest.Pliopithecus;
import conversion7.game.classes.theOldest.Propliopithecus;
import conversion7.game.classes.theOldest.SahelanthropusTchadensis;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.LandscapeGenerator;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import org.slf4j.Logger;
import org.testng.Assert;

import static conversion7.game.WaitLibrary.waitTillLandscapeGeneratorFinishes;

public class WorldCreator {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static LandscapeGenerator landscapeGenerator;

    public static int FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER = 10;
    public static float WATER_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER = FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER / 2f;
    public static int TEAMS_COUNT_LIMIT = PropertiesLoader.getIntProperty("WorldDirector.TEAMS_COUNT_LIMIT");
    public static int MAX_HUMAN_SQUAD_CHANCE = PropertiesLoader.getIntProperty("WorldDirector.MAX_HUMAN_SQUAD_CHANCE");
    public static int MAX_ANIMAL_HERD_CHANCE = PropertiesLoader.getIntProperty("WorldDirector.MAX_ANIMAL_HERD_CHANCE");
    public static int MAX_ANIMAL_HERDS_AMOUNT_LIMIT =
            PropertiesLoader.getIntProperty("WorldDirector.MAX_ANIMAL_HERDS_AMOUNT_LIMIT");
    public static final AreaFaunaCreationResults AREA_FAUNA_CREATION_RESULTS = new AreaFaunaCreationResults();

    public static final Array<Unit> START_UNITS_WIP = PoolManager.ARRAYS_POOL.obtain();

    public static boolean needHumanPlayer = true;

    public static void run() {
        createAreas();
        startLandscapeGenerator();
        waitTillLandscapeGeneratorFinishes(landscapeGenerator);

        World.createAnimalTeam();
        World.createHumanTeam(true);
        calculateWorldCellDetails();
        Assert.assertTrue(World.getPlayerTeam().getArmies().size > 0,
                "There must be at least 1 squad created for player team!");
        World.getPlayerTeam().setTribeSeparationValue(-1);

        World.createWorldTurnsQueue();

        LOG.info("humanTeamsCreated: " + World.humanTeamsCreated);
        LOG.info("createdAnimalHerds: " + World.createdAnimalHerds);
    }

    /** Create UTest for this */
    @Deprecated
    private static void test_addMoreClassesToArmy(HumanSquad army) {
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Ardipithecus.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(ArdipithecusKadabba.class, false));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(ArdipithecusRamidus.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Chororapithecus.class, false));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Dryopithecus.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Gorilla.class, false));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Orrorin.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(OrrorinTugenensis.class, false));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Pan.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Pliopithecus.class, false));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(Propliopithecus.class, true));
        army.getUnitsController().addUnit(UnitFertilizer.createStandardUnit(SahelanthropusTchadensis.class, false));
        army.validate();
    }

    public static void calculateWorldCellDetails() {
        LOG.info("< calculateWorldCellDetails");
        Timer timer = new Timer(LOG);

        World.worldTerrainDataGrid = new TerrainDataGrid(World.WIDTH_IN_SEGMENTS, World.HEIGHT_IN_SEGMENTS);

        for (int ax = 0; ax < World.WIDTH_IN_AREAS; ax++) {
            for (int ay = 0; ay < World.HEIGHT_IN_AREAS; ay++) {
                Area area = World.areas[ax][ay];
                area.calculateCellDetails();
            }
        }

        timer.stop("> calculateWorldCellDetails completed.");
    }

    static void createAreas() {
        Assert.assertEquals(World.WIDTH_IN_AREAS, World.HEIGHT_IN_AREAS);
        for (int x = 0; x < World.WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < World.HEIGHT_IN_AREAS; y++) {
                Area area = new Area(x, y);
                World.areas[x][y] = area;
                World.areasList.add(area);
            }
        }
        fillCellsAroundForAllCells();
    }

    private static void fillCellsAroundForAllCells() {
        for (Area area : World.areasList) {
            for (int ax = 0; ax < Area.WIDTH_IN_CELLS; ax++) {
                for (int ay = 0; ay < Area.HEIGHT_IN_CELLS; ay++) {
                    area.getCell(ax, ay).initCellsAround();
                }
            }
        }
    }

    static void startLandscapeGenerator() {
        landscapeGenerator = new LandscapeGenerator();
        Thread threadLandscapeGenerator = new Thread(landscapeGenerator);
        threadLandscapeGenerator.setDaemon(true);
        threadLandscapeGenerator.setName("LandscapeGenerator-main");
        threadLandscapeGenerator.start();
    }
}
