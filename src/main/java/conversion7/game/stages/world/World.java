package conversion7.game.stages.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.terrain.TerrainDataGrid;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.TablePrinter;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.ai.AnimalAiTeamController;
import conversion7.game.stages.world.creator.WorldCreator;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.events.TribesSeparationEpochCompletedEvent;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.Iterator;

import static conversion7.engine.utils.Utils.error;
import static java.lang.String.format;
import static org.fest.assertions.api.Assertions.assertThat;

public class World {

    private static final Logger LOG = Utils.getLoggerForClass();

    public final static int WIDTH_IN_AREAS =
            PropertiesLoader.getIntProperty("World.WIDTH_IN_AREAS");

    public final static int HEIGHT_IN_AREAS =
            PropertiesLoader.getIntProperty("World.HEIGHT_IN_AREAS");

    public final static int WIDTH_IN_CELLS = WIDTH_IN_AREAS * Area.WIDTH_IN_CELLS;
    public final static int HEIGHT_IN_CELLS = HEIGHT_IN_AREAS * Area.HEIGHT_IN_CELLS;
    public final static int WIDTH_IN_SEGMENTS = WIDTH_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;
    public final static int HEIGHT_IN_SEGMENTS = HEIGHT_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;

    public final static Point2s CENTRAL_AREA = new Point2s(WIDTH_IN_AREAS / 2, HEIGHT_IN_AREAS / 2);
    public final static Point2s CENTRAL_CELL = new Point2s();
    public static final int TRIBE_SEPARATION_VALUE_MAX = 10000;
    public static final int INCREASE_TRIBES_SEPARATION_PER_BATTLE = 10;
    public static final int INCREASE_TRIBES_SEPARATION_PER_NEW_CLASS = 100;
    public static final ObjectSet<Class<? extends Unit>> CLASSES_IN_WORLD = new ObjectSet<>();

    public static int humanTeamsCreated;
    public static int createdAnimalHerds;
    private static Team lastCreateHumanTeam;

    static {
        if (World.WIDTH_IN_AREAS % 2 == 0) {
            CENTRAL_CELL.x = CENTRAL_AREA.x * Area.WIDTH_IN_CELLS - 1;
        } else {
            CENTRAL_CELL.x = CENTRAL_AREA.x * Area.WIDTH_IN_CELLS + (int) Area.WIDTH_IN_CELLS_HALF - 1;
        }
        if (World.HEIGHT_IN_AREAS % 2 == 0) {
            CENTRAL_CELL.y = CENTRAL_AREA.y * Area.HEIGHT_IN_CELLS - 1;
        } else {
            CENTRAL_CELL.y = CENTRAL_AREA.y * Area.HEIGHT_IN_CELLS + (int) Area.HEIGHT_IN_CELLS_HALF - 1;
        }
    }

    public static Team ANIMAL_TEAM;
    public static TerrainDataGrid worldTerrainDataGrid;

    private static AreaViewer areaViewer;
    public final static Area[][] areas = new Area[WIDTH_IN_AREAS][HEIGHT_IN_AREAS];
    public final static Array<Area> areasList = PoolManager.ARRAYS_POOL.obtain();
    public static final Array<Team> TEAMS = PoolManager.ARRAYS_POOL.obtain();
    private static WorldTurnsQueue worldTurnsQueue;
    private static volatile Team activeTeam;

    public static boolean initialized = false;
    public static int year = -30000000;
    public static volatile int step = 0;
    private static int totalTribesSeparationValue;
    private static boolean tribesSeparationEpochCompleted;

    public static void init() {
        Assert.assertFalse(initialized);
        LOG.info("< World init");
        Timer timer = new Timer(LOG);
        assertThat(WIDTH_IN_AREAS).as("World.WIDTH_IN_AREAS should be more or equal 3!").isGreaterThanOrEqualTo(3);
        assertThat(HEIGHT_IN_AREAS).as("World.HEIGHT_IN_AREAS should be more or equal 3!").isGreaterThanOrEqualTo(3);
        FastAsserts.assertMoreThan(Cell.CELL_TERRAIN_SEGMENTATION, 1);
        Assert.assertEquals(Cell.CELL_TERRAIN_SEGMENTATION % 2, 0);

        WorldCreator.run();
        Gdxg.clientUi.getTribesSeparationBar().updateTribeSeparation(totalTribesSeparationValue);

        areaViewer = new AreaViewer(World.getArea(0, 0));

        LOG.info(format("World created: %sx%s areas, %sx%s cells",
                WIDTH_IN_AREAS, HEIGHT_IN_AREAS, WIDTH_IN_CELLS, HEIGHT_IN_CELLS));

        timer.stop("> World init");

        System.gc();
        Utils.sleepThread(500);
        initialized = true;
    }

    public static Team getLastCreatedHumanTeam() {
        return lastCreateHumanTeam;
    }

    public static WorldTurnsQueue getWorldTurnsQueue() {
        return worldTurnsQueue;
    }

    public static void createWorldTurnsQueue() {
        worldTurnsQueue = new WorldTurnsQueue();
    }

    public static AreaViewer getAreaViewer() {
        return areaViewer;
    }

    public static Area getArea(int sceneOrWorldCoordX, int sceneOrWorldCoordY) {
        sceneOrWorldCoordX = getLoopedCoord(sceneOrWorldCoordX, WIDTH_IN_AREAS);
        sceneOrWorldCoordY = getLoopedCoord(sceneOrWorldCoordY, HEIGHT_IN_AREAS);

        Area area;
        try {
            area = areas[sceneOrWorldCoordX][sceneOrWorldCoordY];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(
                    format("Wrong area world coords [%s][%s]. Most probably bug in getLoopedCoord()",
                            sceneOrWorldCoordX, sceneOrWorldCoordY),
                    e);
        }
        return area;
    }

    public static Cell getCellByGameCoordinate(Vector3 gameCoord) {
        int cellInWorldX = getLoopedCoord(gameCoord.x, WIDTH_IN_CELLS);
        int cellInWorldY = getLoopedCoord(gameCoord.y, HEIGHT_IN_CELLS);
        Area area = getArea(cellInWorldX / Area.WIDTH_IN_CELLS, cellInWorldY / Area.HEIGHT_IN_CELLS);
        return area.getCell(cellInWorldX % Area.WIDTH_IN_CELLS, cellInWorldY % Area.HEIGHT_IN_CELLS);
    }

    /** rawCoord could be for example sceneCoord */
    public static int getLoopedCoord(float rawCoord, int sideSize) {
        int sceneCoordInt = (int) rawCoord;
        if (rawCoord >= 0) {
            return Math.abs(sceneCoordInt -
                    Math.abs(sceneCoordInt / sideSize) // shifts by size
                            * sideSize);
        } else {
            if (sceneCoordInt > rawCoord) {
                sceneCoordInt--;
            }
            return (Math.abs((sceneCoordInt + 1) / sideSize) + 1) // shifts by size
                    * sideSize
                    + sceneCoordInt;
        }
    }

    public static Team createHumanTeam(boolean isHumanPlayer) {
        Team team = new Team(isHumanPlayer, "Team" + (TEAMS.size));
        if (!isHumanPlayer) {
            team.setAiController(new AiTeamController(team));
        }
        addTeam(team);
        lastCreateHumanTeam = team;
        humanTeamsCreated++;
        return team;
    }

    public static Team createAnimalTeam() {
        Assert.assertNull(ANIMAL_TEAM);
        Team team = new Team(false, "Animals");
        team.setAiController(new AnimalAiTeamController(team));
        addTeam(team);
        ANIMAL_TEAM = team;
        return team;
    }

    private static void addTeam(Team team) {
        TEAMS.add(team);
        if (ANIMAL_TEAM != null) {
            TEAMS.swap(TEAMS.indexOf(ANIMAL_TEAM, true), TEAMS.size - 1);
        }
    }

    public static Team getAnimalTeam() {
        return ANIMAL_TEAM;
    }

    public static Team getPlayerTeam() {
        for (Team team : TEAMS) {
            if (team.isHumanPlayer()) {
                LOG.info("Human player: " + team.toString());
                return team;
            }
        }
        return null;
    }

    public static void nextTeamTurn() {
        LOG.info("nextTeamTurn");
//        worldTurnsQueue.askNextTeamTurn(true);
        worldTurnsQueue.askNextTeamTurn();
    }

    public static void finishStep() {
        UiLogger.addInfoLabel("finishStep: " + step);
        // clear defeated teams
        Iterator<Team> iterator = TEAMS.iterator();
        while (iterator.hasNext()) {
            Team next = iterator.next();
            if (next.isDefeated() && next.isAiPlayer()) {
                iterator.remove();
                continue;
            }
            next.clearDefeatedObjects();
        }
        World.runEndStepSimulations();
        LOG.info("WorldStep finished: " + step);
        LOG.info("Teams in world: " + TEAMS.size + "\n");
        step++;
        year++;
    }

    public static void runEndStepSimulations() {
        LOG.info("endStepSimulation started by " + WorldCreator.class);
        for (Area area : areasList) {
            area.endStepSimulation();
        }
    }

    public static void startBattle(AreaObject attacker, AreaObject defender, boolean autoCalculated) {
        UiLogger.addInfoLabel("startBattle");
        if (attacker.isHumanSquad() && defender.isHumanSquad()) {
            updateTotalTribesSeparationValue(World.getTotalTribesSeparationValue() + INCREASE_TRIBES_SEPARATION_PER_BATTLE);
        }

        World.getAreaViewer().deselect();
        Gdxg.clientUi.getHighlightedCellBar().hide();

        Battle battle = new Battle(attacker, defender, autoCalculated);
        ClientCore.core.battle = battle;
        if (autoCalculated) {
            ClientCore.core.battle.startAuto();
        } else {
            ClientCore.core.activateStage(battle);
            Gdxg.clientUi.hideTeamUi();
            Gdxg.clientUi.getBattleBar().show();
            if (battle.getAttackerTeamSide().getTeam().isHumanPlayer()) {
                battle.setHumanPlayerArmyLink(attacker);
            } else if (battle.getDefenderTeamSide().getTeam().isHumanPlayer()) {
                battle.setHumanPlayerArmyLink(defender);
            } else {
                error("Battle must contain playerTeam to be shown!");
            }
        }
    }

    public static void drawMap(Cell someCell, Array<Cell> cells) {
        String[][] worldMap = new String[World.WIDTH_IN_CELLS][World.HEIGHT_IN_CELLS];
        for (int ax = 0; ax < World.WIDTH_IN_AREAS; ax++) {
            for (int ay = 0; ay < World.HEIGHT_IN_AREAS; ay++) {
                Area area1 = World.getArea(ax, ay);
                for (int cx = 0; cx < Area.WIDTH_IN_CELLS; cx++) {
                    for (int cy = 0; cy < Area.HEIGHT_IN_CELLS; cy++) {
                        Cell cell = area1.getCell(cx, cy);
                        String cellText;
                        if (cells.contains(cell, true)) {
                            cellText = "C";
                        } else if (someCell == cell) {
                            cellText = "T";
                        } else {
                            cellText = ".";
                        }
                        worldMap[ax * Area.WIDTH_IN_CELLS + cx][ay * Area.HEIGHT_IN_CELLS + cy] = cellText;
                    }
                }
            }
        }

        LOG.info("worldMap\n" + TablePrinter.getTableToString(worldMap));
    }

    public static Team getActiveTeam() {
        return World.activeTeam;
    }

    public static void setActiveTeam(Team activeTeam) {
        World.activeTeam = activeTeam;
    }

    public static int getTotalTribesSeparationValue() {
        return totalTribesSeparationValue;
    }

    public static void updateTotalTribesSeparationValue(int value) {
        if (tribesSeparationEpochCompleted) {
            return;
        }

        World.totalTribesSeparationValue += value;
        if (totalTribesSeparationValue >= TRIBE_SEPARATION_VALUE_MAX) {
            tribesSeparationEpochCompleted = true;
            Team playerTeam = getPlayerTeam();
            playerTeam.getNextStepEvents().add(new TribesSeparationEpochCompletedEvent(playerTeam));
            Gdxg.clientUi.getTribesSeparationBar().hide();
        } else {
            Gdxg.clientUi.getTribesSeparationBar().updateTribeSeparation(value);
        }
    }

    public static void validateNewUnitBirth(Unit unit) {
        int preSize = CLASSES_IN_WORLD.size;
        CLASSES_IN_WORLD.add(unit.getClass());
        if (CLASSES_IN_WORLD.size > preSize) {
            updateTotalTribesSeparationValue(World.getTotalTribesSeparationValue() + INCREASE_TRIBES_SEPARATION_PER_NEW_CLASS);
        }
    }
}
