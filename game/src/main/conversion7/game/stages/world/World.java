package conversion7.game.stages.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.AudioPlayer;
import conversion7.engine.CameraController;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.GlobalStrategyComponent;
import conversion7.engine.artemis.GlobalStrategySystem;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.terrain.TerrainDataGrid;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.*;
import conversion7.game.GdxgConstants;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.world.adventure.*;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.ai_deprecated.AnimalAiTeamControllerOld;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.climate.Climate;
import conversion7.game.stages.world.climate.WinterEvent;
import conversion7.game.stages.world.gods.GodsGlobalStats;
import conversion7.game.stages.world.inventory.items.CampBuildingKit;
import conversion7.game.stages.world.inventory.items.SchemasBookItem;
import conversion7.game.stages.world.landscape.Biom;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.LandscapeGenerator;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.IronResourceObject;
import conversion7.game.stages.world.objects.UranusResourceObject;
import conversion7.game.stages.world.objects.buildings.SpaceShip;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.quest.items.BuildCampQuest;
import conversion7.game.stages.world.quest.items.FertilizeAnimalsQuest;
import conversion7.game.stages.world.quest.items.FindAndKillIlluminatiDadQuest;
import conversion7.game.stages.world.quest.items.SendSosQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect;
import conversion7.game.stages.world.unit.effects.items.ConcealmentEffect;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;
import conversion7.game.stages.world.unit.hero_classes.SpecClass;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.utils.collections.Comparators;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

import static conversion7.engine.Gdxg.core;
import static java.lang.String.format;
import static org.fest.assertions.api.Assertions.assertThat;

public class World {

    public static final int FIRST_STEP = 0;
    public static final int APPROX_UNIT_EXP_PER_STEP = 5;
    public static final int EXP_FOR_EVOLUTION = ChildbearingEffect.PREGNANCY_DURATION * APPROX_UNIT_EXP_PER_STEP;
    public static final int BASE_VIEW_RADIUS = 3;
    public static final int MAX_VIEW_RADIUS = BASE_VIEW_RADIUS + 2;
    public static final int BASE_NIGHT_VIEW_RADIUS = 1;
    public static final int HALF_DAY_LENGTH = 4;
    public static final int DAY_LENGTH = HALF_DAY_LENGTH * 2;
    public static final int SPACE_SHIP_AREA_RADIUS = (int) (Area.WIDTH_IN_CELLS * 0.5f);
    public static final Comparator<? super Biom> BIOM_DST_TO_TARGET_COMPARATOR =
            (Comparator<Biom>) (o1, o2) -> Float.compare(o1.distanceToTargetCell, o2.distanceToTargetCell);
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int CHAOS_STEPS_MAX = (int) (World.HALF_DAY_LENGTH * 3.14f);
    private static final int EPOCH_LENGTH = (int) (DAY_LENGTH * 0.75f);
    public static int FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER = 10;
    public static float WATER_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER = FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER / 2f;
    public final Array<Point2s> areaThroughWorldBoundsVariants = new Array<>();
    public final Array<Point2s> cellThroughWorldBoundsVariants = new Array<>();
    public final Array<Point2s> segmentThroughWorldBoundsVariants = new Array<>();
    public final int widthInAreas;
    public final int heightInAreas;
    public final int widthInCells;
    public final int heightInCells;
    public final int minSizeInCells;
    public final int minRadiusInCells;
    public final int widthInSegments;
    public final int heightInSegments;
    public final Point2s centralArea;
    public final Point2s centralCellCoord;
    public final ObjectSet<Class<? extends Unit>> classesInWorld = new ObjectSet<>();
    public final Area[][] areas;
    public final Array<Area> areasList = PoolManager.ARRAYS_POOL.obtain();
    public final Array<Team> teams = PoolManager.ARRAYS_POOL.obtain();
    public final WorldRelations worldRelations = new WorldRelations(this);
    @Deprecated
    public final boolean multiPlayer;
    public WorldSettings settings;
    public TerrainDataGrid worldTerrainDataGrid;
    public volatile Team activeTeam;
    public boolean initialized = false;
    public int year = -33000000;
    public volatile int step = FIRST_STEP;
    public Team lastActivePlayerTeam;
    public int worldEntityId;
    public LandscapeGenerator landscapeGenerator;
    public int humanTeamsCreated;
    public int playerTeamsCreated;
    public int aiTeamsCreated;
    public int createdAnimalHerds;
    public Team animalTeam;
    public Array<AnimalSpawn> animalSpawns = new Array<>();
    public GodsGlobalStats godsGlobalStats;
    public SoulQueue soulQueue;
    public Cell questDebrisCell;
    public ObjectSet<WorldTrend> trends = new ObjectSet<>();
    public int maxIterationRadius;
    public int humanPlayersToCreate = 1;
    public int humanPlayersCreated = 0;
    public Array<Biom> bioms = new Array<>();
    public Array<Team> humanPlayers = new Array<>();
    public Array<Biom> goodBioms;
    public Biom playerStartBiom;
    public Biom baalsCampStart;
    public Biom spaceShipStart;
    public Biom questAnimal;
    public Array<WorldBattle.PostponedBattle> postponedBattles = new Array<>();
    Array<String> eventMainUiNotificationsDelayed = new Array<String>();
    WorldBattle activeBattle;
    private Cell centralCell;
    private int totalTribesSeparationValue;
    @Deprecated
    private boolean tribesSeparationEpochCompleted;
    private ObjectMap<String, Integer> relationBalance = new ObjectMap<>();
    private ObjectMap<String, ObjectSet<TribeRelationType>> relationTypes = new ObjectMap<>();
    private boolean finishingStep;
    private boolean daytime;
    private WinterEvent winterEvent;
    private int chaosStepsLeft = 0;
    private int noChaosForSteps = 0;
    private int currentChaosStartedOnStep = -1;
    private int desiredActiveAnimalsPerStep;
    private RandomQuestsAndEvents randomQuestsAndEvents = new RandomQuestsAndEvents(this);
    private Array<AreaObject> importantObjects = new Array<>();
    private ObjectSet<WorldTrend> newTrendsWip = new ObjectSet<>();
    private int currEpoch = -1;
    private Team firstHumanPlayer;
    private boolean firstPlayerTurnRequested;
    private SpaceShip spaceShip;

    public World(WorldSettings settings) {
        this.settings = settings;
        LOG.info("settings: {}", settings);
        this.widthInAreas = settings.widthInAreas;
        this.heightInAreas = settings.heightInAreas;

        Assert.assertTrue(humanPlayersToCreate > 0);
        multiPlayer = true;
//        multiPlayer = humanPlayersToCreate > 1;

        widthInCells = widthInAreas * Area.WIDTH_IN_CELLS;
        heightInCells = heightInAreas * Area.HEIGHT_IN_CELLS;
        minSizeInCells = Math.min(widthInCells, heightInCells);
        minRadiusInCells = minSizeInCells / 2;
        maxIterationRadius = minRadiusInCells - 1;
        widthInSegments = widthInCells * Cell.CELL_TERRAIN_SEGMENTATION;
        heightInSegments = heightInCells * Cell.CELL_TERRAIN_SEGMENTATION;
        desiredActiveAnimalsPerStep = widthInCells * heightInCells / 75;
        if (GdxgConstants.DEVELOPER_MODE) {
            desiredActiveAnimalsPerStep /= 5;
        }
        if (desiredActiveAnimalsPerStep < 1) {
            desiredActiveAnimalsPerStep = 1;
        }
        LOG.info("desiredActiveAnimalsPerStep {}", desiredActiveAnimalsPerStep);

        worldEntityId = core.nextEntityId();

        centralArea = new Point2s(widthInAreas / 2, heightInAreas / 2);
        centralCellCoord = new Point2s();
        if (widthInAreas % 2 == 0) {
            centralCellCoord.x = centralArea.x * Area.WIDTH_IN_CELLS - 1;
        } else {
            centralCellCoord.x = centralArea.x * Area.WIDTH_IN_CELLS + (int) Area.WIDTH_IN_CELLS_HALF - 1;
        }
        if (heightInAreas % 2 == 0) {
            centralCellCoord.y = centralArea.y * Area.HEIGHT_IN_CELLS - 1;
        } else {
            centralCellCoord.y = centralArea.y * Area.HEIGHT_IN_CELLS + (int) Area.HEIGHT_IN_CELLS_HALF - 1;
        }


        areas = new Area[widthInAreas][heightInAreas];
        segmentThroughWorldBoundsVariants.add(new Point2s());
        segmentThroughWorldBoundsVariants.add(new Point2s(widthInSegments, 0));
        segmentThroughWorldBoundsVariants.add(new Point2s(0, heightInSegments));
        segmentThroughWorldBoundsVariants.add(new Point2s(widthInSegments, heightInSegments));
        segmentThroughWorldBoundsVariants.add(new Point2s(-widthInSegments, 0));
        segmentThroughWorldBoundsVariants.add(new Point2s(0, -heightInSegments));
        segmentThroughWorldBoundsVariants.add(new Point2s(-widthInSegments, -heightInSegments));
        segmentThroughWorldBoundsVariants.add(new Point2s(-widthInSegments, heightInSegments));
        segmentThroughWorldBoundsVariants.add(new Point2s(widthInSegments, -heightInSegments));

        cellThroughWorldBoundsVariants.add(new Point2s());
        cellThroughWorldBoundsVariants.add(new Point2s(widthInCells, 0));
        cellThroughWorldBoundsVariants.add(new Point2s(0, heightInCells));
        cellThroughWorldBoundsVariants.add(new Point2s(widthInCells, heightInCells));
        cellThroughWorldBoundsVariants.add(new Point2s(-widthInCells, 0));
        cellThroughWorldBoundsVariants.add(new Point2s(0, -heightInCells));
        cellThroughWorldBoundsVariants.add(new Point2s(-widthInCells, -heightInCells));
        cellThroughWorldBoundsVariants.add(new Point2s(-widthInCells, heightInCells));
        cellThroughWorldBoundsVariants.add(new Point2s(widthInCells, -heightInCells));

        areaThroughWorldBoundsVariants.add(new Point2s());
        areaThroughWorldBoundsVariants.add(new Point2s(widthInAreas, 0));
        areaThroughWorldBoundsVariants.add(new Point2s(0, heightInAreas));
        areaThroughWorldBoundsVariants.add(new Point2s(widthInAreas, heightInAreas));
        areaThroughWorldBoundsVariants.add(new Point2s(-widthInAreas, 0));
        areaThroughWorldBoundsVariants.add(new Point2s(0, -heightInAreas));
        areaThroughWorldBoundsVariants.add(new Point2s(-widthInAreas, -heightInAreas));
        areaThroughWorldBoundsVariants.add(new Point2s(-widthInAreas, heightInAreas));
        areaThroughWorldBoundsVariants.add(new Point2s(widthInAreas, -heightInAreas));

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

    public static void initPlayerTeam(Team team, World world) {
        team.setName("Player-" + world.humanPlayersCreated);
        team.setTeamOrder(0);
        if (world.lastActivePlayerTeam == null) {
            world.lastActivePlayerTeam = team;
        }

        for (String msg : world.eventMainUiNotificationsDelayed) {
            team.addEventMainUiNotification(msg);
        }

        world.loadPlayerTeamProgress(team);
        team.playerUnitProgress = world.loadPlayerUnitProgress() / 5;
        BaseQuest.startQuest(team, BuildCampQuest.class);
        BaseQuest.startQuest(team, SendSosQuest.class);
        Gdxg.clientUi.getTribeResorcesPanel().showFor(team);
        team.getInventory().addItem(new SchemasBookItem());
        team.getInventory().addItem(CampBuildingKit.class, 1);
    }

    public int getTotalTribesSeparationValue() {
        return totalTribesSeparationValue;
    }

    public Team getLastActivePlayerTeam() {
        return lastActivePlayerTeam;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        LOG.info("New step: " + (step));
        this.step = step;
        addEventMainUiNotification("Step " + step);
        int epochId = getEpochId();
        if (currEpoch < epochId) {
            currEpoch = epochId;
            addEventMainUiNotification(getEpoch().name() + " epoch");
        }

        try {
            calculateDayNight();
//            randomQuestsAndEvents.newStep();
            ArchonSuppliesCampaign.newStep(this);
            BaalsMainCampaign.newStep(this);
            BaalsScoutCampaign.newStep(this);
//            IlluminatiCampaign.newStep(this);
//            Gdxg.core.artemis.getSystem(TrackAudioSystem.class).setMaxVol();
        } catch (Throwable e) {
            Gdxg.core.addError(e);
        }
    }

    public SpaceShip getSpaceShip() {
        return spaceShip;
    }

    public Cell getCentralCell() {
        if (centralCell == null) {
            centralCell = getCell(centralCellCoord.x, centralCellCoord.y);
        }
        return centralCell;
    }

    public Team getNeutralTotemTeam() {
        return animalTeam;
    }

    public Array<AreaObject> getImportantObjects() {
        Iterator<AreaObject> iterator = importantObjects.iterator();
        while (iterator.hasNext()) {
            AreaObject object = iterator.next();
            if (object.isRemovedFromWorld()) {
                iterator.remove();
            }
        }
        return importantObjects;
    }

    public int getDesiredActiveAnimalsPerStep() {
        return desiredActiveAnimalsPerStep;
    }

    public int getCurrentChaosStartedOnStep() {
        return currentChaosStartedOnStep;
    }

    public int getChaosStepsLeft() {
        return chaosStepsLeft;
    }

    public boolean isChaosPeriod() {
        return chaosStepsLeft > 0;
    }

    public WinterEvent getWinterEvent() {
        return winterEvent;
    }

    public boolean isDaytime() {
        return daytime;
    }

    public boolean isFinishingStep() {
        return finishingStep;
    }

    public boolean isPlayerTeamAlive() {
        return lastActivePlayerTeam != null && !lastActivePlayerTeam.isDefeated();
    }

    public boolean isPlayerTeamActive() {
        return lastActivePlayerTeam != null && lastActivePlayerTeam == activeTeam;
    }

    public int getBaseViewRadius() {
        return BASE_VIEW_RADIUS;
    }

    public int getDayNightViewRadius() {
        int baseR = daytime ? getBaseViewRadius() : BASE_NIGHT_VIEW_RADIUS;
        return baseR;
    }

    public boolean isChaosStartedOnThisStep() {
        return step == currentChaosStartedOnStep;
    }

    private int getYearsInCurrentStep() {
        int yearsByWorldAge = (int) Math.ceil(Math.abs(year / 100f));
        return Math.max(yearsByWorldAge, 1);
    }

    public boolean isRaceFoundersStep() {
        return step == 0;
    }

    public Class<? extends BaseAnimalClass> getSpawnableRndAnimalClass() {
        return UnitClassConstants.getAnimalClassByLevel(getSpawnableRndAnimalLevel());
    }

    public WorldEpoch getEpoch() {
        return WorldEpoch.getByID(getEpochId());
    }

    public String getEpochName() {
        return getEpoch().toString();
    }

    /** 0 ... n */
    public int getEpochId() {
        int animalClasses = UnitClassConstants.ANIMAL_CLASSES.size;
        int currId = Math.min(step / EPOCH_LENGTH, animalClasses - 1);
        currId = Math.min(currId, WorldEpoch.getMaxId());
        if (currId < 0) {
            currId = 0;
        }
        return currId;
    }

    /** 1 .. max epoch */
    public int getSpawnableRndAnimalLevel() {
        return MathUtils.random(1, getEpochId() + 1);
    }

    public Team getBaalsTeam() {
        for (Team team : teams) {
            if (team.isBaals()) {
                return team;
            }
        }
        return null;
    }

    public Team getRndPlayerTeam() {
        Array<Team> teams = new Array<>(this.humanPlayers);
        teams.shuffle();
        for (Team team : teams) {
            if (team.isHumanPlayer()) {
                return team;
            }
        }
        return null;
    }

    public boolean isBattleActive() {
        return activeBattle != null;
    }

    public WorldBattle getActiveBattle() {
        return activeBattle;
    }

    public void setDayNight(boolean newDaytime) {
        if (newDaytime == daytime) {
            return;
        }

        this.daytime = newDaytime;
        PoolManager.UNEXPLORED_DECAL_POOL.setCurrentColor(daytime ? Color.WHITE : Color.BLACK);
        PoolManager.NOT_VISIBLE_DECAL_POOL.setCurrentColor(daytime ? UiUtils.alpha(0.4f, Color.WHITE)
                : UiUtils.alpha(0.5f, Color.BLACK));
        Gdxg.graphic.setLight(daytime);
        totalViewReload2();

        String dayTimeType = daytime ? "Day" : "Night";
        UiLogger.addImportantGameInfoLabel(dayTimeType);
        addEventMainUiNotification(dayTimeType);

        if (step > 0) {
            switchTrends();
        }

        if (!daytime) {
            spawnNightHordesOnTopAndBottomOfMap();
            migrateSpawns();
        }
    }

    public void generateFaunaOnAndAround(Area startArea) {
        if (startArea.faunaGeneratedAroundMe) {
            return;
        }
        startArea.faunaGeneratedAroundMe = true;

        ObjectSet<Area> areasToBeGenerated = new ObjectSet<>();
        areasToBeGenerated.add(startArea);
        areasToBeGenerated.addAll(startArea.areasAround);
        Assert.assertEquals(areasToBeGenerated.size, 9);

        for (Area area : areasToBeGenerated) {
            if (area.faunaGeneratedOn) {
                continue;
            }
            area.faunaGeneratedOn = true;

            int chanceForAiTeam = 100
                    - (teams.size * 10)
                    + MathUtils.random(-50, 50);
            if (area.playerStartArea) {
                chanceForAiTeam = 0;
            }

            for (Biom biom : area.bioms) {
                if (biom.faunaGeneratedOn
                        || biom.startCell.getBaseTemperature() < Area.BASE_MIN_TEMPERATURE_FOR_SPAWN) {
                    continue;
                }

                boolean noLimitAiTeamsReached = settings.aiTeamsAmount == -1
                        || aiTeamsCreated < settings.aiTeamsAmount;
                if (noLimitAiTeamsReached && MathUtils.testPercentChance(chanceForAiTeam)) {
                    Team team = WorldAdventure.placeAiTeam(biom.startCell);
//                    if (humanPlayersCreated < humanPlayersToCreate) {
//                        team.setHumanPlayer(true);
//                    }
                    if (team != null) {
                        chanceForAiTeam -= 75;
                        biom.startPlaceForAiHuman = true;
                        biom.faunaGeneratedOn = true;
                    }

                } else {
                    if (MathUtils.testPercentChance(30)) {
                        AbstractSquad animalHerd = area.tryToCreateAnimalHerd(biom.startCell);
                        if (animalHerd != null) {
                            biom.startPlaceForAnimals = true;
                            biom.faunaGeneratedOn = true;
                            if (MathUtils.testPercentChance(50)) {
                                area.createBaseSpawn(biom.startCell);
                            }
                        }
                    }
                }
            }

            area.ensureSpawnCreated();
        }
    }

    private void switchTrends() {
        Array<WorldTrend> actTrends;
        if (daytime) {
            actTrends = WorldTrend.Mappings.GOOD_TRENDS;

        } else {
            actTrends = WorldTrend.Mappings.BAD_TRENDS;

        }

        for (WorldTrend goodTrend : actTrends) {
            trends.remove(goodTrend);
        }

        newTrendsWip.clear();
        while (MathUtils.testPercentChance(50)) {
            newTrendsWip.add(actTrends.random());
        }
        trends.addAll(newTrendsWip);

        if (newTrendsWip.size > 0) {
            if (lastActivePlayerTeam != null) {
                lastActivePlayerTeam.addEventMainUiNotification("New trends");
                AudioPlayer.playRitual();
            }
            if (newTrendsWip.contains(WorldTrend.ANIMAL_HIDE)) {
                for (AbstractSquad squad : animalTeam.getSquadsIter()) {
                    squad.effectManager.getOrCreate(ConcealmentEffect.class);
                }
            }
        }
        for (WorldTrend trend : newTrendsWip) {
            if (trend.isGood()) {
                UiLogger.addImportantGameInfoLabel("Active good trend: " + trend.getDesc());
            } else {
                UiLogger.addImportantGameInfoLabel("Active bad trend: " + trend.getDesc());
            }
        }
    }

    public Team getTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        return null;
    }

    public void addEventMainUiNotification(String type) {
        if (humanPlayers.size == 0) {
            eventMainUiNotificationsDelayed.add(type);
        } else {
            for (Team player : humanPlayers) {
                player.addEventMainUiNotification(type);
            }
        }
    }

    public void addImportantObj(AreaObject object) {
        importantObjects.add(object);
    }

    private void migrateSpawns() {
        for (AnimalSpawn spawn : animalSpawns) {
            spawn.migrate();
        }

    }

    private void spawnNightHordesOnTopAndBottomOfMap() {
        if (GdxgConstants.DEVELOPER_MODE) {
            return;
        }
        int spawnAtX = MathUtils.random(0, widthInCells - 1);

        int coldStartsFromY;
        for (coldStartsFromY = centralCellCoord.y; coldStartsFromY > 0; coldStartsFromY--) {
            Cell cell = getCell(spawnAtX, coldStartsFromY);
            if (cell.getBaseTemperature() < Unit.HEALTHY_TEMPERATURE_MIN) {
                break;
            }
        }

        int maxAnimalAmountForHorde = heightInCells / 10;
        int animalsToSpawn = MathUtils.random(maxAnimalAmountForHorde / 2, maxAnimalAmountForHorde);
        boolean spawnFromSouth = MathUtils.random();
        int spawnOffsetMax = maxAnimalAmountForHorde;
        for (int spawnAtYAbs = (coldStartsFromY + MathUtils.random(0, spawnOffsetMax));
             spawnAtYAbs > 0 && animalsToSpawn > 0;
             spawnAtYAbs--) {
            boolean willTrySpawnOnThisCell = MathUtils.random();
            if (willTrySpawnOnThisCell) {
                int spawnAtY = spawnFromSouth ? spawnAtYAbs : (heightInCells - spawnAtYAbs);
                Cell cell = getCell(spawnAtX, spawnAtY);
                if (cell.hasFreeMainSlot()
                        && !cell.hasSquad()
                        && !cell.isVisibleForHumanPlayer()) {
                    int idx = MathUtils.random(0, PackageReflectedConstants.WORLD_ANIMAL_CLASSES.size - 1);
                    Class<? extends Unit> clazz = PackageReflectedConstants.WORLD_ANIMAL_CLASSES.get(idx);

                    WorldSquad squad = WorldSquad.create(clazz, animalTeam, cell);
                    animalsToSpawn--;
                    LOG.info("spawnNightHordesOnTopAndBottomOfMap: " + squad);
                }
            }
        }
    }

    private void createClimate() {
        winterEvent = new WinterEvent(this);
    }


    public void totalViewReload2() {
        // reset cells
        for (Area area : areasList) {
            for (Cell[] cells : area.cells) {
                for (Cell cell : cells) {
                    cell.setDiscovered(null);
                }
            }
        }

        for (Team team : teams) {
            team.recalculateVisibleCellsFull();
        }
        if (Gdxg.core.areaViewer != null) {
            Gdxg.core.areaViewer.requestCellsRefresh();
        } else {
            LOG.debug("skip totalViewReload2");
        }
    }

    public void calculateDayNight() {
        setDayNight((step / HALF_DAY_LENGTH) % 2 == 0);
    }

    public void addRelationType(TribeRelationType type, Team team1, Team team2) {
        WorldRelations.RelationData relationData = worldRelations.getRelationData(team1, team2);
        relationData.add(type);
    }

    protected String getCompositeId(Team team1, Team team2) {
        if (team1 == team2) {
            // unit under control
            return String.valueOf(team1.getTeamId());
        }
        Assert.assertFalse(team1.getTeamId() == team2.getTeamId());
        String compositeId;
        if (team1.getTeamId() < team2.getTeamId()) {
            compositeId = team1.getTeamId() + "-" + team2.getTeamId();
        } else {
            compositeId = team2.getTeamId() + "-" + team1.getTeamId();
        }
        return compositeId;
    }

    public boolean areEnemies(Team team1, Team team2) {
        if (team1 == team2) {
            return false;
        }
        return worldRelations.getRelationData(team1, team2).getBalance() <= WorldRelations.ENEMIES_RELATION_TOP;
    }

    public boolean areAllies(Team team1, Team team2) {
        if (team1 == team2) {
            return true;
        }
        return worldRelations.getRelationData(team1, team2).getBalance() >= WorldRelations.ALLY_RELATION_BOTTOM;
    }

    public boolean areEnemies(AbstractSquad squad1, AbstractSquad squad2) {
        return areEnemies(squad1.team, squad2.team);
    }

    @Deprecated
    private void createDummyLandscape() {
//        for (Area area : areasList) {
//            for (Cell[] cellRows : area.cells) {
//                for (Cell cell : cellRows) {
//                    cell.getLandscapeController().setRandomLandscape();
//                }
//            }
//            area.placeRndBioms();
//        }
    }

    public Team createAnimalTeam() {
        Assert.assertNull(animalTeam);
        Team team = new Team(false, "Animals", this);
        team.setAiController(new AnimalAiTeamControllerOld(team));
        team.setAnimals(true);
        animalTeam = team;
        team.setTeamOrder(10);
        addTeam(team);

        return animalTeam;
    }

    private void addTeam(Team newTeam) {
        teams.add(newTeam);

        if (!firstPlayerTurnRequested && newTeam.isHumanPlayer()) {
            firstPlayerTurnRequested = true;
            requestNextTeamTurn();
        }

        validateTeamsOrder();
    }

    private void validateTeamsOrder() {
        WorldThreadLocalSort.instance().sort(teams, Comparators.TEAMS_ORDER);
    }

    public void addTeam2(Team team) {
        team.world = this;
        addTeam(team);
    }

    public Team createHumanTeam(boolean isHumanPlayer) {
        Team team = new Team(isHumanPlayer, "Tribe-" + (teams.size), this);
        team.setHumanRace(true);
        if (isHumanPlayer) {
            if (firstHumanPlayer == null) {
                firstHumanPlayer = team;
            }
            team.setHumanPlayer(true);
        } else {
            Assert.assertNotNull(lastActivePlayerTeam);
            team.setTeamOrder(5);
            team.setAiController(new AiTeamControllerOld(team));
            aiTeamsCreated++;
        }
        addTeam(team);
        team.getAvaiableHeroClasses().add(HeroClass.HODOR);
        team.getAvaiableUnitSpecs().add(SpecClass.AGILE);
        team.getAvaiableUnitSpecs().add(SpecClass.BIG);
        team.getAvaiableUnitSpecs().add(SpecClass.FAST);
        humanTeamsCreated++;
        return team;
    }

    public Area getArea(int sceneOrWorldCoordX, int sceneOrWorldCoordY) {
        sceneOrWorldCoordX = getLoopedCoord(sceneOrWorldCoordX, widthInAreas);
        sceneOrWorldCoordY = getLoopedCoord(sceneOrWorldCoordY, heightInAreas);

        Area area;
        try {
            area = areas[sceneOrWorldCoordX][sceneOrWorldCoordY];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GdxRuntimeException(
                    format("Wrong area world coords [%s][%s]. Most probably bug in getLoopedCoord()",
                            sceneOrWorldCoordX, sceneOrWorldCoordY),
                    e);
        }
        return area;
    }

    /**
     * Safe decimal reminder, because [-0.1,-0.1] should be a cell 9,9 of area 9,9 <br>
     * (and not cell 0,0 of area 0,0)<br>
     * Refer to WorldTest class.
     */
    public Cell getCellByGameCoordinate(float worldX, float worldY) {
        int cellInWorldX = getLoopedCoord(worldX, widthInCells);
        int cellInWorldY = getLoopedCoord(worldY, heightInCells);
        Area area = getArea(cellInWorldX / Area.WIDTH_IN_CELLS, cellInWorldY / Area.HEIGHT_IN_CELLS);
        return area.getCell(cellInWorldX % Area.WIDTH_IN_CELLS, cellInWorldY % Area.HEIGHT_IN_CELLS);
    }

    public Cell getCellByGameCoordinate(Vector3 gameCoord) {
        return getCellByGameCoordinate(gameCoord.x, gameCoord.y);
    }

    public Cell getCell(int worldX, int worldY) {
        return getCellByGameCoordinate(worldX, worldY);
    }

    public void requestNextTeamTurn() {
        World world = Gdxg.core.world;
        requestNextTeamTurn(world != null && world.isPlayerTeamAlive());
    }

    public void requestNextTeamTurn(boolean completeAiTeams) {
        LOG.info("requestNextTeamTurn, completeAiTeams={}", completeAiTeams);
        GlobalStrategyComponent nextTeamTurnComponent = GlobalStrategySystem.components.getSafe(worldEntityId);
        if (nextTeamTurnComponent == null) {
            long frameId = Gdx.graphics.getFrameId();
            nextTeamTurnComponent = GlobalStrategySystem.components.create(worldEntityId);
            nextTeamTurnComponent.onActiveFrame = frameId;
            nextTeamTurnComponent.onActiveTeam = activeTeam;
            nextTeamTurnComponent.completeAiTeams = completeAiTeams;
        }
    }

    public void finishStep() {
        LOG.info("finishStep: " + step);
        finishingStep = true;
        eventMainUiNotificationsDelayed.clear();
        runEndStepSimulations();
        winterEvent.moveRight();
        clearDefeatedTeams();
        LOG.info("WorldStep finished: " + step);
        LOG.info("Teams in world: " + teams.size + "\n");
        setStep(step + 1);
        year += getYearsInCurrentStep();
        finishingStep = false;
    }

    public void runEndStepSimulations() {
        LOG.info("runEndStepSimulations {}", step);
        for (Area area : areasList) {
            area.endStepSimulation();
        }
//        for (AnimalSpawn spawn : animalSpawns) {
//            spawn.trySpawnSingleUnit();
//        }
    }

    private void clearDefeatedTeams() {
        Iterator<Team> iterator = teams.iterator();
        while (iterator.hasNext()) {
            Team next = iterator.next();
            if (next.isDefeated() && !next.isAnimalTeam()) {
                iterator.remove();
            }
            next.clearDefeatedObjects();
        }
        validateTeamsOrder();
    }

    public void drawMap(Cell someCell, Array<Cell> cells) {
        String[][] worldMap = new String[widthInCells][heightInCells];
        for (int ax = 0; ax < widthInAreas; ax++) {
            for (int ay = 0; ay < heightInAreas; ay++) {
                Area area1 = getArea(ax, ay);
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

        LOG.info("worldMap\n" + TablePrinter.getStringTable(worldMap));
    }

    public void validateNewUnitBirth(Unit unit) {
        int preSize = classesInWorld.size;
        classesInWorld.add(unit.getClass());
        if (classesInWorld.size > preSize) {
            LOG.info("validateNewUnitBirth > new CLASSES_IN_WORLD size {}", classesInWorld.size);
        }
        FertilizeAnimalsQuest.onNewUnitBorn(unit);
    }

    private void generateWorldDetails() {
        LOG.info("< generateWorldDetails at EID " + worldEntityId);

        if (settings.dummyLandscape) {
            createDummyLandscape();
            throw new GdxRuntimeException("Broken by biom map!");
        } else {
            landscapeGenerator = new LandscapeGenerator(this);
            landscapeGenerator.run();
        }

//        transformLandscapeForStory();

        if (settings.createPlayerTeam) {
            createHumanTeam(true);
        }
        if (settings.createAnimalTeam) {
            createAnimalTeam();
        } else {
            LOG.warn("skip createAnimalTeam");
        }

        calculateWorldCellsDetails();

        if (multiPlayer) {
            placeSpaceShip(spaceShipStart.startCell.getCouldBeSeizedNeighborCell());

            goodBioms = new Array<>();
            for (Biom biom : bioms) {
                if (biom.isGoodPlaceForTeam()) {
                    goodBioms.add(biom);
                }
            }

            while (humanPlayers.size < humanPlayersToCreate) {
                createHumanTeam(true);
            }

            placePlayer();
            BaalsMainCampaign.createTeam(this);
            BaalsMainCampaign.placeBaalCamp1(this);

            placeResourceObjects();
            WorldAdventure.runAround(questAnimal.startCell, 0,
                    Cell.Filters.CAN_SET_SQUAD, WorldAdventure.Events.PLACE_Q_MAMMOTH);
        } else {
            // single player
            for (int ay = 0; ay < heightInAreas; ay++) {
                for (int ax = 0; ax < widthInAreas; ax++) {
                    Area area = areas[ax][ay];
//                    area.findBiomForPlayerTeam();
                }
            }
            if (!settings.dummyLandscape) {
//                placeSinglePlayer();
            }
        }

        LOG.info("humanTeamsCreated: " + humanTeamsCreated);
        LOG.info("createdAnimalHerds: " + createdAnimalHerds);
        LOG.info("> generateWorldDetails");
    }

    private void placePlayer() {
        Biom biom = playerStartBiom;
        goodBioms.removeValue(biom, true);
        Team team = humanPlayers.get(0);
        biom.startPlaceForPlayer = true;
        biom.faunaGeneratedOn = true;
        biom.startCell.getArea().playerStartArea = true;
        team.toBePlacedInArea = biom.startCell.getArea();
        team.toBePlacedOnBiom = biom;

        WorldAdventure.placePlayerUnits(team, biom.startCell);
        generateFaunaOnAndAround(biom.startCell.getArea());
    }

    public void placeSpaceShip(Cell cell) {
        spaceShip = new SpaceShip(cell, lastActivePlayerTeam);
    }

    private void placeResourceObjects() {
        int xDiff = (int) (Area.WIDTH_IN_CELLS_HALF * 0.33f);
        int yDiff = (int) (Area.HEIGHT_IN_CELLS_HALF * 0.33f);
        for (Area area : areasList) {
            Cell centralCell = area.getRandomCell(xDiff, xDiff, yDiff, yDiff);
            Class resClass;
            if (area.worldPosInAreas.x % 2 == 0 || MathUtils.testPercentChance(33)) {
                resClass = IronResourceObject.class;
            } else {
                resClass = UranusResourceObject.class;
            }
            WorldAdventure.runAround(centralCell, 0, (int) Area.WIDTH_IN_CELLS_HALF,
                    Cell.Filters.CAN_SET_RESOURCE_OBJ, cell -> {
                        AreaObject.create(cell, animalTeam, resClass);
                    });
        }
    }

    private void createGods() {
        godsGlobalStats = new GodsGlobalStats(this);
    }

    void createAreas() {
        Assert.assertEquals(widthInAreas, heightInAreas);
        for (int x = 0; x < widthInAreas; x++) {
            for (int y = 0; y < heightInAreas; y++) {
                Area area = new Area(x, y, this);
                areas[x][y] = area;
                areasList.add(area);
            }
        }
        fillCellsAroundForAllCells();
        fillAreasAround();
        Climate.calcExistingTemperatures(this);
    }

    private void fillAreasAround() {
        for (Area area : areasList) {
            area.areasAround = area.getAdjAreas(new Array<>());
        }
    }

    private void fillCellsAroundForAllCells() {
        for (Area area : areasList) {
            for (int ax = 0; ax < Area.WIDTH_IN_CELLS; ax++) {
                for (int ay = 0; ay < Area.HEIGHT_IN_CELLS; ay++) {
                    area.getCell(ax, ay).initCellsAround();
                }
            }
        }
    }

    public void calculateWorldCellsDetails() {
        LOG.info("< calculateWorldCellsDetails");
        Timer timer = new Timer(LOG);

        worldTerrainDataGrid = new TerrainDataGrid(widthInSegments, heightInSegments);

        for (int ay = 0; ay < heightInAreas; ay++) {
            for (int ax = 0; ax < widthInAreas; ax++) {
                Area area = areas[ax][ay];
                area.calculateShapeAndNature(settings.dummyFauna);
            }
        }

        timer.stop("> calculateWorldCellsDetails completed.");
    }

    public void defeat(Team team) {
        if (team.isAnimalTeam()) {
            // animals team is regenerating armies
            return;
        }

        LOG.info("Defeated: " + team);
        Assert.assertFalse(team.defeated);
        team.defeated = true;
        if (team.isHumanPlayer()) {
            if (Gdxg.core.isAreaViewerActiveStage()) {
                SchedulingSystem.schedule("refresh ui for defeated team", 0, () -> {
                    team.showDefeatedWindow();
                    Gdxg.clientUi.getTeamBar().refreshContent(team);
                    Gdxg.clientUi.getEventsBar().hide();
                    team.addEventMainUiNotification("Player defeated");
                });
            }
            AudioPlayer.playEnd();
            lastActivePlayerTeam = null;
        } else {
            if (lastActivePlayerTeam != null) {
                if (lastActivePlayerTeam.metTeams.contains(team)) {
                    UiLogger.addImportantGameInfoLabel("Team defeated: " + team.getName());
                } else {
                    UiLogger.addImportantGameInfoLabel("Unknown team defeated");
                }
                AudioPlayer.playRitual();
            }
        }

        if (team.isIlluminati()) {
            FindAndKillIlluminatiDadQuest illuminatiDadQuest = lastActivePlayerTeam.journal
                    .getOrCreate(FindAndKillIlluminatiDadQuest.class);
            illuminatiDadQuest.complete(FindAndKillIlluminatiDadQuest.State.S2);
            lastActivePlayerTeam.updateEvolutionPointsOn(1, FindAndKillIlluminatiDadQuest.class.getSimpleName());
        }
    }

    private void loadPlayerTeamProgress(Team team) {
        team.updateEvolutionPointsOn((int) Math.sqrt(WorldPlayerSave.getPlayerTeamProgress()), "Player save");
    }

    public void savePlayerTeamProgress(Team team) {
        try {
            FileUtils.writeStringToFile(new File("save/ep.txt"), String.valueOf(team.getEvolutionPointsTotal()));
        } catch (IOException e) {
            Gdxg.core.addError(e);
        }
    }

    public void savePlayerUnitProgress(int exp) {
        Integer savedExp = loadPlayerUnitProgress();
        if (exp > savedExp) {
            try {
                FileUtils.writeStringToFile(new File("save/unit_lvl.txt"), String.valueOf(exp));
            } catch (IOException e) {
                Gdxg.core.addError(e);
            }
        }
    }

    private Integer loadPlayerUnitProgress() {
        try {
            return Integer.valueOf(FileUtils.readFileToString(new File("save/unit_lvl.txt")));
        } catch (IOException e) {
        }
        return 0;
    }

    public boolean doesPlayerSeeCell(Cell cell) {
        return lastActivePlayerTeam != null && lastActivePlayerTeam.canSeeCell(cell);
    }

    public void beforeNewStepStarts() {
        UiLogger.addGameInfoLabel("Turn " + step);
        UiLogger.addGameInfoLabel("Epoch " + getEpochName());
        core.getClientUi().getWorldMainWindow().refreshWorldStepInfo(this);
        if (isChaosPeriod()) {
            chaosStepsLeft--;
            if (!isChaosPeriod()) {
                stopChaos();
            }
        } else {
            if (MathUtils.testPercentChance(noChaosForSteps)) {
                startChaos();
            }
            noChaosForSteps++;
        }
    }

    private void stopChaos() {
        noChaosForSteps = 0;
        UiLogger.addImportantGameInfoLabel("Chaos period end");
    }

    private void startChaos() {
        currentChaosStartedOnStep = step;
        chaosStepsLeft += MathUtils.random(1, CHAOS_STEPS_MAX);
        UiLogger.addImportantGameInfoLabel("Chaos period start");
    }

    public void init() {
        LOG.info("< World init");
        Assert.assertFalse(initialized);
        Timer timer = new Timer(LOG);
        assertThat(widthInAreas).as("World.WIDTH_IN_AREAS should be more or equal 3!").isGreaterThanOrEqualTo(3);
        assertThat(heightInAreas).as("World.HEIGHT_IN_AREAS should be more or equal 3!").isGreaterThanOrEqualTo(3);
        FastAsserts.assertMoreThan(Cell.CELL_TERRAIN_SEGMENTATION, 1);
        Assert.assertEquals(Cell.CELL_TERRAIN_SEGMENTATION % 2, 0);

        soulQueue = new SoulQueue();
        UnitFertilizer2.soulQueue = soulQueue;
        setStep(0);
        createGods();
        createAreas();
        UnitClassConstants.init();
        generateWorldDetails();
        createClimate();

        LOG.info(format("World created: %sx%s areas, %sx%s cells",
                widthInAreas, heightInAreas, widthInCells, heightInCells));
        timer.stop("> World init");

        initialized = true;
    }

    public boolean hasPlayerTribe() {
        return lastActivePlayerTeam != null;
    }

    public Array<Team> getEnemies(Team team) {
        Array<Team> teams = new Array<>();
        for (Team team2 : this.teams) {
            if (areEnemies(team, team2)) {
                teams.add(team2);
            }
        }
        return teams;
    }

    public void initRelations(Team team1, Team team2) {
        worldRelations.getRelationData(team1, team2);
    }

//    private void placeSinglePlayer() {
//        Team playerTeam = this.lastActivePlayerTeam;
//        if (playerTeam.toBePlacedOnBiom == null) {
//            throw new GdxRuntimeException("");
//        }
//        if (playerTeam.toBePlacedInArea == null) {
//            throw new GdxRuntimeException("");
//        }
//
//        // create player first
//        placePlayerUnits(playerTeam, playerTeam.toBePlacedOnBiom.startCell);
//        playerTeam.toBePlacedOnBiom.startPlaceForPlayer = true;
//        playerTeam.toBePlacedOnBiom.faunaGeneratedOn = true;
//
//        // fill world details
//        AbstractSquad rndSquad = playerTeam.getSquads().get(0);
//        Cell playerSquadCell = rndSquad.getLastCell();
//        DestroyAnimalsSpawnQuest.placeObjects(playerSquadCell);
//
//        WorldAdventure.placeSecondarySquad(this);
//
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 0.5f),
//                Cell.Filters.CAN_SET_JEWEL_EXP, WorldAdventure.Events.PLACE_JEWEL_EXP);
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 1.2f),
//                Cell.Filters.CAN_SET_JEWEL_EXP, WorldAdventure.Events.PLACE_JEWEL_EXP);
//
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 0.8f),
//                Cell.Filters.CAN_SET_TOTEM, WorldAdventure.Events.PLACE_EXP_TOTEM);
//
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 1.2f),
//                Cell.Filters.CAN_SET_SQUAD, WorldAdventure.Events.PLACE_Q_MAMMOTH);
//
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 1.5f),
//                Cell.Filters.CAN_SET_RESOURCE_OBJ, WorldAdventure.Events.PLACE_IRON);
//        WorldAdventure.runAround(playerTeam, (int) (Area.WIDTH_IN_CELLS * 1.75f),
//                Cell.Filters.CAN_SET_RESOURCE_OBJ, WorldAdventure.Events.PLACE_IRON);
//        WorldAdventure.runAround(playerTeam, Area.WIDTH_IN_CELLS * 2,
//                Cell.Filters.CAN_SET_RESOURCE_OBJ, WorldAdventure.Events.PLACE_URANUS);
//        WorldAdventure.runAround(playerTeam, 1,
//                Cell.Filters.CAN_SET_MAIN_SLOT_OBJ, cell -> {
//                    new SpaceShip(cell, playerTeam);
//                });
//
//        generateFaunaOnAndAround(playerTeam.toBePlacedInArea);
//    }

    public void startBattle(Team attacker, AbstractSquad target) {
        activeBattle = new WorldBattle(this, attacker, target);
        activeBattle.prepareBattleField();
        activeBattle.startNewTurn();
        core.areaViewer.hideSelection();
        CameraController.scheduleCameraFocusOn(0, target.cell);
    }

    public void autoBattle(Team attacker, AbstractSquad targetSquad) {
        LOG.info("autoBattle\nattacker: " + attacker + "\ntarget: " + targetSquad);
        activeBattle = new WorldBattle(attacker.world, attacker, targetSquad);
        activeBattle.autoCalc();
        LOG.info("autoBattle end");
    }

    public void postponeBattleWithPlayer(Team attacker, AbstractSquad humanPlayerAsTarget) {
        attacker.addRelation(TribeRelationType.ATTACK, humanPlayerAsTarget.team);
        postponedBattles.add(new WorldBattle.PostponedBattle(attacker, humanPlayerAsTarget));
    }

}
