package conversion7.game.stages.world.area;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.geometry.terrain.TerrainChunk;
import conversion7.engine.geometry.terrain.TerrainVertexData;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.BattleConstants;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldSettings;
import conversion7.game.stages.world.adventure.WorldAdventure;
import conversion7.game.stages.world.climate.Climate;
import conversion7.game.stages.world.landscape.*;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.composite.SandWorm;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.view.AreaView;
import org.slf4j.Logger;
import org.testng.Assert;

import java.awt.*;

import static conversion7.game.stages.world.landscape.LandscapeGenerator.BIOM_WIP_AREA_ERROR;
import static org.fest.assertions.api.Assertions.assertThat;

public class Area {

    public final static int WIDTH_IN_CELLS = 8;
    public final static int HEIGHT_IN_CELLS = 8;
    public final static int WIDTH_IN_SEGMENTS = WIDTH_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;
    public final static int HEIGHT_IN_SEGMENTS = HEIGHT_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;
    public final static float HEIGHT_IN_CELLS_HALF = HEIGHT_IN_CELLS / 2f;
    public final static float WIDTH_IN_CELLS_HALF = WIDTH_IN_CELLS / 2f;
    public final static int BIOM_R = (int) Math.min(WIDTH_IN_CELLS_HALF, HEIGHT_IN_CELLS_HALF);
    public static final int TEMPERATURE_ZONE_STEP = Climate.TEMPERATURE_RADIUS / 6;
    public static final int BASE_MIN_TEMPERATURE_FOR_SPAWN = Unit.HEALTHY_TEMPERATURE_MIN - TEMPERATURE_ZONE_STEP;
    public static final int FILL_SPAWN_START_RADIUS = 3;
    public static final int FIND_SPAWN_PLACE_MAX_RADIUS = FILL_SPAWN_START_RADIUS * 2;
    public static final int MAX_SPREAD_ANIMALS_PER_AREA = 2;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int BIOM_POS_FIX = 1;
    public final Point2s worldPosInAreas;
    public final Point2s worldPosInCells;
    public int id;
    public Cell[][] cells = new Cell[WIDTH_IN_CELLS][HEIGHT_IN_CELLS];
    public Array<Biom> bioms = PoolManager.ARRAYS_POOL.obtain();
    public Array<AreaObject> squads = PoolManager.ARRAYS_POOL.obtain();
    public AreaView areaView;
    public World world;
    public boolean faunaGeneratedAroundMe;
    public boolean faunaGeneratedOn;
    public Array<Area> areasAround;
    public boolean playerStartArea;
    Array<Cell> cellsWithComfortConditionsForFauna = PoolManager.ARRAYS_POOL.obtain();
    private boolean discovered;
    private TerrainChunk terrainChunk;
    private ModelActor terrain;
    private SceneGroup3d sceneGroup = new SceneGroup3d();
    private AnimalSpawn lastSpawn;
    private ObjectMap<Class, AreaEvent> events = new ObjectMap<>();
    private Array<Cell> fillSpawnCellsWip = new Array<>();
    private SandWorm sandWorm;

    public Area(int worldX, int worldY, World world) {
        this.world = world;
        id = Utils.getNextId();
        worldPosInAreas = new Point2s(worldX, worldY);
        worldPosInCells = new Point2s(worldPosInAreas.x * WIDTH_IN_CELLS, worldPosInAreas.y * HEIGHT_IN_CELLS);
        for (int x = 0; x < WIDTH_IN_CELLS; x++) {
            for (int y = 0; y < HEIGHT_IN_CELLS; y++) {
                Cell cell = cells[x][y] = new Cell(this, x, y);
            }
        }
        sceneGroup.setName(this.toString());
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public SandWorm getSandWorm() {
        if (sandWorm == null) {
            sandWorm = SandWorm.create(world.animalTeam);
        } else {
            if (!sandWorm.alive) {
                return null;
            }
        }
        return sandWorm;
    }

    public AnimalSpawn getLastSpawn() {
        return lastSpawn;
    }

    public SceneGroup3d getSceneGroup() {
        return sceneGroup;
    }

    public Cell getCentralCell() {
        return cells[(int) WIDTH_IN_CELLS_HALF][(int) HEIGHT_IN_CELLS_HALF];
    }

    public Area getArea(int x, int y) {
        return world.getArea(this.worldPosInAreas.x + x, this.worldPosInAreas.y + y);
    }

    public Array<Area> getAdjAreas(Array<Area> areas) {
        for (Point adjPoint : BattleConstants.CELLS_AROUND) {
            Area area = world.getArea(this.worldPosInAreas.x + adjPoint.x, this.worldPosInAreas.y + adjPoint.y);
            areas.add(area);
        }
        return areas;
    }

    public TerrainVertexData getAverageInterpolatedVertex(Cell curCell, Point2s curSegmentWorldPos, float distanceToMainOrigin) {
        TerrainChunk._importantNeighbors = 0;
        TerrainVertexData averageNeighborVertexData = new TerrainVertexData();
        for (Cell cellAround : curCell.getCellsAround()) {
            Point2s cellAroundPosInSegments = cellAround.getOriginWorldPosInSegments();
            float distanceToNeighborInSegm = getDistanceBtwSegments(curSegmentWorldPos, cellAroundPosInSegments);
            if (distanceToNeighborInSegm < TerrainChunk.IGNORE_NEIGHBOR_CELL_ON_SEGMENT_DISTANCE) {
                averageNeighborVertexData.append(cellAround.getLandscape().getTerrainVertexData());
                TerrainChunk._importantNeighbors++;
            }
        }

        // TODO clear on iteration end
        if (TerrainChunk._importantNeighbors < 3) {
            throw new GdxRuntimeException("_importantNeighbors: " + TerrainChunk._importantNeighbors);
        }

        // origin will be affected by zero, farthest cell will be affected almost fully by average neighbors
        averageNeighborVertexData.divide(TerrainChunk._importantNeighbors);
        TerrainVertexData copyOriginVertexData = new TerrainVertexData(curCell.getLandscape().getTerrainVertexData());
        copyOriginVertexData.interpolateWithNeighbors(averageNeighborVertexData,
                distanceToMainOrigin, Cell.DISTANCE_FROM_CORNER_TO_ORIGIN_IN_SEGMENTS);
        return copyOriginVertexData;
    }

    public float getDistanceBtwSegments(Point2s segment1, Point2s segment2) {
        return getDiffBtwSegments(segment1, segment2).len();
    }

    public Point2s getDiffBtwSegments(Point2s segment1, Point2s segment2) {
        int toMinusFromX = segment1.x - segment2.x;
        int toMinusFromY = segment1.y - segment2.y;
        Point2s theShortestDiffPoint = new Point2s();
        float theShortestDiffLen = Float.MAX_VALUE;

        float curDiffLen;
        int curDiffX;
        int curDiffY;
        for (Point2s curMoveVariant : world.segmentThroughWorldBoundsVariants) {
            curDiffX = toMinusFromX + curMoveVariant.x;
            curDiffY = toMinusFromY + curMoveVariant.y;
            curDiffLen = Vector2.len2(curDiffX, curDiffY);
            if (curDiffLen < theShortestDiffLen) {
                theShortestDiffLen = curDiffLen;
                theShortestDiffPoint.x = curDiffX;
                theShortestDiffPoint.y = curDiffY;
            }
        }

        return theShortestDiffPoint;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName().toUpperCase()).append(": ")
                .append(worldPosInAreas).append(GdxgConstants.HINT_SPLITTER)
                .append("id = ").append(id).append(GdxgConstants.HINT_SPLITTER).toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public void addSquad(AreaObject object) {
        this.squads.add(object);
    }

    public void removeSquad(AreaObject object) {
        this.squads.removeValue(object, false);
    }

    public Cell getCell(Cell relativeTo, Point2s withDiff) {
        return getCell(relativeTo, withDiff.x, withDiff.y);
    }

    public Cell getCell(Cell relativeTo, int diffX, int diffY) {
        return getCell(relativeTo.x + diffX, relativeTo.y + diffY);
    }

    public Cell getCell(final Point point) {
        return getCell(point.x, point.y);
    }

    /** Returns cell from neighbor areas if x/y outs of bounds */
    public Cell getCell(int x, int y) {

        int cellX = x;
        int cellY = y;
        int areaX = worldPosInAreas.x;
        int areaY = worldPosInAreas.y;

        if (x < 0) {
            int areaShiftX = -(Math.abs(x / Area.WIDTH_IN_CELLS) + 1);
            areaX += areaShiftX;
            cellX = x + Math.abs(areaShiftX) * Area.WIDTH_IN_CELLS;
        } else if (x >= Area.WIDTH_IN_CELLS) {
            int areaShiftX = x / Area.WIDTH_IN_CELLS;
            areaX += areaShiftX;
            cellX = x - areaShiftX * Area.WIDTH_IN_CELLS;
        }

        if (y < 0) {
            int areaShiftY = -(Math.abs(y / Area.HEIGHT_IN_CELLS) + 1);
            areaY += areaShiftY;
            cellY = y + Math.abs(areaShiftY) * Area.HEIGHT_IN_CELLS;
        } else if (y >= Area.HEIGHT_IN_CELLS) {
            int areaShiftY = y / Area.HEIGHT_IN_CELLS;
            areaY += areaShiftY;
            cellY = y - areaShiftY * Area.HEIGHT_IN_CELLS;
        }

        if (areaX == worldPosInAreas.x && areaY == worldPosInAreas.y) {
            return cells[x][y];
        } else {
            return world.getArea(areaX, areaY).getCell(cellX, cellY);
        }
    }

    /** Diff through world bounds */
    public Point2s diffWithArea(Area areaTo) {
        if (LOG.isDebugEnabled()) LOG.debug("diffWithArea");
        Point2s fromPos = this.worldPosInAreas;
        Point2s toPos = areaTo.worldPosInAreas;
        if (LOG.isDebugEnabled()) LOG.debug("    fromPos: " + fromPos);
        if (LOG.isDebugEnabled()) LOG.debug("    toPos: " + toPos);

        int toMinusFromX = toPos.x - fromPos.x;
        int toMinusFromY = toPos.y - fromPos.y;
        Point2s theShortestDiffPoint = new Point2s();
        float theShortestDiffLen = java.lang.Float.MAX_VALUE;

        float curDiffLen;
        int curDiffX;
        int curDiffY;
        for (Point2s curMoveVariant : world.areaThroughWorldBoundsVariants) {
            curDiffX = toMinusFromX + curMoveVariant.x;
            curDiffY = toMinusFromY + curMoveVariant.y;
            curDiffLen = Vector2.len2(curDiffX, curDiffY);
            if (curDiffLen < theShortestDiffLen) {
                theShortestDiffLen = curDiffLen;
                theShortestDiffPoint.x = curDiffX;
                theShortestDiffPoint.y = curDiffY;
            }
        }

        if (LOG.isDebugEnabled()) LOG.debug("    theShortestDiffLen: " + theShortestDiffLen);
        if (LOG.isDebugEnabled()) LOG.debug("    theShortestDiffPoint: " + theShortestDiffPoint);

        return theShortestDiffPoint;
    }

    public void endStepSimulation() {
        if (LOG.isDebugEnabled()) LOG.debug("< area endStepSimulation " + this);
        if (LOG.isDebugEnabled()) LOG.debug("squads.size " + squads.size);
        ObjectMap<Class, AreaEvent> activeEvents = new ObjectMap<>(events);
        for (ObjectMap.Entry<Class, AreaEvent> activeEvent : activeEvents) {
            AreaEvent areaEvent = activeEvent.value;
            areaEvent.act();
        }


        for (Cell[] cellsRow : cells) {
            for (Cell cell : cellsRow) {
                cell.endStepSimulation();
            }
        }
    }

    public ModelActor buildTerrainModel() {
        if (terrain == null) {
            terrainChunk = new TerrainChunk(this);
            terrain = Modeler.createModelActorFromMesh(terrainChunk.getMesh(), new Material(new TerrainAttribute()),
                    terrainChunk.getVertexUnitsAmount(), Gdxg.terrainBatch);

            terrain.setPosition(-WIDTH_IN_CELLS_HALF, 0, HEIGHT_IN_CELLS_HALF);
            sceneGroup.addNode(terrain);
        }
        return terrain;
    }

    public void calculateShapeAndNature(boolean dummyFauna) {
        addBiomDetailsToArea();

        int ax = worldPosInAreas.x;
        int ay = worldPosInAreas.y;
        cellsWithComfortConditionsForFauna.clear();
        for (int cx = 0; cx < Area.WIDTH_IN_CELLS; cx++) {
            for (int cy = 0; cy < Area.HEIGHT_IN_CELLS; cy++) {
                Cell cell = cells[cx][cy];

                // TODO split on sub-methods
                // METHOD terrain map
                // main body vertices
                for (int segmX = 0; segmX < Cell.CELL_TERRAIN_SEGMENTATION; segmX++) {
                    for (int segmY = 0; segmY < Cell.CELL_TERRAIN_SEGMENTATION; segmY++) {
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData vertexData = getAverageInterpolatedVertex(cell, segmWorldPos,
                                (float) cell.getSegmentDistanceToOrigin(segmX, segmY));
                        Assert.assertNull(world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = vertexData;
                    }
                }

                // the last right vertices column
                if (ax == GdxgConstants.WIDTH_IN_AREAS - 1 && cx == Area.WIDTH_IN_CELLS - 1) {
                    for (int segmY = 0; segmY < Cell.CELL_TERRAIN_SEGMENTATION; segmY++) {
                        int segmX = Cell.CELL_TERRAIN_SEGMENTATION;
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData copyFrom = world.worldTerrainDataGrid.
                                vertices[0][segmWorldPos.y];
                        Assert.assertNull(world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
                    }
                }

                // the last top vertices row
                if (ay == GdxgConstants.HEIGHT_IN_AREAS - 1 && cy == Area.HEIGHT_IN_CELLS - 1) {
                    for (int segmX = 0; segmX < Cell.CELL_TERRAIN_SEGMENTATION; segmX++) {
                        int segmY = Cell.CELL_TERRAIN_SEGMENTATION;
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData copyFrom = world.worldTerrainDataGrid.
                                vertices[segmWorldPos.x][0];
                        Assert.assertNull(world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
                    }
                }

                // the last vertex in the top-right corner
                if (ax == GdxgConstants.WIDTH_IN_AREAS - 1 && cx == Area.WIDTH_IN_CELLS - 1
                        && ay == GdxgConstants.HEIGHT_IN_AREAS - 1 && cy == Area.HEIGHT_IN_CELLS - 1) {
                    int segmX = Cell.CELL_TERRAIN_SEGMENTATION;
                    int segmY = Cell.CELL_TERRAIN_SEGMENTATION;
                    Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                    TerrainVertexData copyFrom = world.worldTerrainDataGrid.
                            vertices[0][0];
                    Assert.assertNull(world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                    world.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
                }

                // METHOD food-water-fauna
                // modificators
                int dirt = cell.getLandscape().soil.getSoilTypeValue(Soil.TypeId.DIRT);
                boolean hasForest = cell.getLandscape().hasForest();
                /**could be from 0.05 (the lowest temperature: 1 - TEMPERATURE_AFFECT_FOOD_MAXIMAL_MULTIPLIER)
                 * to 1 (normal temperature)*/
                float temperatureMultiplier = 1;
                if (cell.getTemperature() < Climate.AFFECT_FOOD_FROM_TEMPERATURE) {
                    int temperatureDiff = Climate.AFFECT_FOOD_FROM_TEMPERATURE - cell.getTemperature();
                    // temperatureAffectPercent == 1 on the minimal temperate
                    float temperatureAffectPercent = (float) temperatureDiff /
                            (float) (Climate.AFFECT_FOOD_FROM_TEMPERATURE - Climate.TEMPERATURE_MIN);
                    assertThat(temperatureAffectPercent).isLessThanOrEqualTo(1);
                    // food and water multiplier should be = 0.05 on cell with the minimal temperate
                    temperatureMultiplier -= Climate.TEMPERATURE_AFFECT_FOOD_MAXIMAL_MULTIPLIER * temperatureAffectPercent;
                }

                // food calculation
                int foodBonusesFromNeighbors = 0;
                for (Cell neighborCell : cell.getCellsAround()) {
                    if ((neighborCell.getLandscape().type.equals(Landscape.Type.COMMON) && hasForest)
                            || neighborCell.getLandscape().type.equals(Landscape.Type.WATER)) {
                        foodBonusesFromNeighbors += World.FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER;
                    }
                }

                cell.setFood(dirt * (hasForest ? 2 : 1) + foodBonusesFromNeighbors);
                cell.setFood(Math.round(cell.getFood() * temperatureMultiplier));

                // water calculation
                float waterBonusesFromNeighbors = 0;
                Array<Cell> cellsAroundToRadiusInclusively = cell.getCellsAroundToRadiusInclusively(3);
                for (Cell neighborCell : cellsAroundToRadiusInclusively) {
                    if ((neighborCell.getLandscape().type.equals(Landscape.Type.COMMON) && hasForest)
                            ||
                            (neighborCell.getLandscape().type.equals(Landscape.Type.WATER))) {
                        waterBonusesFromNeighbors += World.WATER_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER
                                / cell.distanceTo(neighborCell);
                    }
                }
                PoolManager.ARRAYS_POOL.free(cellsAroundToRadiusInclusively);

                cell.setWater(Math.round(
                        (dirt / 3 * (hasForest ? 1.5f : 1)
                                + waterBonusesFromNeighbors)
                                * temperatureMultiplier));

                cell.updateTotalValue();
                cell.resourcesGenerator.refresh();

                if (cell.canBeSeized() && (cell.getFood() + cell.getWater() > 60)) {
                    cellsWithComfortConditionsForFauna.add(cell);
                }
            }
        }

        if (!world.settings.dummyFauna && world.animalTeam != null) {
            generateSpreadAnimals(cellsWithComfortConditionsForFauna);
        }
    }

    public void findBiomForPlayerTeam() {
        if (world.lastActivePlayerTeam == null || world.lastActivePlayerTeam.toBePlacedInArea != null) {
            return;
        }

        bioms.shuffle();
        for (Biom biom : bioms) {
            if (!biom.isGoodPlaceForTeam()) {
                continue;
            }

            boolean goodPlace = world.settings.isTemperatureOkForPlayerSpawn(biom.startCell)
                    && WorldAdventure.canPlacePlayerTeam(biom.startCell);
            if (goodPlace || GdxgConstants.DEVELOPER_MODE) {
                playerStartArea = true;
                world.lastActivePlayerTeam.toBePlacedInArea = this;
                world.lastActivePlayerTeam.toBePlacedOnBiom = biom;
                return;
            }
        }
    }

    public void ensureSpawnCreated() {
        if (lastSpawn == null) {
            int radius = FILL_SPAWN_START_RADIUS;
            Biom biomTarget = null;
            for (Biom biom : bioms) {
                if (!biom.startPlaceForPlayer && !biom.startPlaceForAiHuman) {
                    biomTarget = biom;
                    break;
                }
            }

            if (biomTarget == null) {
                return;
            }

            while (lastSpawn == null && radius < FIND_SPAWN_PLACE_MAX_RADIUS) {
                fillSpawnCellsWip.clear();
                biomTarget.startCell.getCellsAroundOnRadius(radius, fillSpawnCellsWip);
                for (Cell cell : fillSpawnCellsWip) {
                    if (cell.canBeSeized()) {
                        createBaseSpawn(cell);
                        break;
                    }
                }
                radius++;
            }
        }
    }

    private void generateSpreadAnimals(Array<Cell> cellsWithComfortConditionsForFauna) {
        cellsWithComfortConditionsForFauna.shuffle();
        int unitsCreated = 0;
        Class<? extends Unit> spawnableAnimalClass = world.getSpawnableRndAnimalClass();
        while (cellsWithComfortConditionsForFauna.size > 0 && unitsCreated < MAX_SPREAD_ANIMALS_PER_AREA) {
            Cell cell = cellsWithComfortConditionsForFauna.removeIndex(0);
            if (cell.canBeSeized()) {
                WorldSquad.create(spawnableAnimalClass, world.animalTeam, cell);
                unitsCreated++;
            }
        }
    }

    private void addBiomDetailsToArea() {
        for (Biom biom : bioms) {
            biom.generateDetails();
        }

    }

    public void createBaseSpawn(Cell cell) {
        AnimalSpawn spawn = new AnimalSpawn(cell, cell.getArea().world.animalTeam);
        lastSpawn = spawn;
        spawn.setSpawnEvery(world.settings.getAnimalsBalanceSpawnEvery());
        for (int i = 0; i < 1; i++) {
            if (MathUtils.random()) {
                spawn.trySpawnSingleUnit();
            }
        }
    }

    public AbstractSquad tryToCreateAnimalHerd(Cell cell) {
        if ((world.settings.animalHerdsAmount == -1
                || world.createdAnimalHerds < world.settings.animalHerdsAmount)) {
            int animalHerdChance = Math.min(WorldSettings.ANIMAL_HERD_CHANCE_MAX, cell.getFood());
            animalHerdChance = Math.max(100, animalHerdChance);
            if (cell.canBeSeized() && MathUtils.testPercentChance(animalHerdChance)) {
                return WorldSquad.create(world.getSpawnableRndAnimalClass(), world.animalTeam, cell);
            }
        }
        return null;
    }

    public void dispose() {
        if (terrain != null) {
            terrain.dispose();
        }
    }

    public void addEvent(AreaEvent areaEvent) {
        if (areaEvent.area != null) {
            areaEvent.area.events.remove(areaEvent.getClass());
        }
        events.put(areaEvent.getClass(), areaEvent);
        areaEvent.area = this;
    }

    public Cell randomCell() {
        return cells[MathUtils.random(0, WIDTH_IN_CELLS - 1)][MathUtils.random(0, HEIGHT_IN_CELLS - 1)];
    }

    public boolean canHaveWorm() {
        for (Biom biom : bioms) {
            if (biom.type == Biom.Type.SAND) {
                return true;
            }
        }
        return false;
    }

    public void placeBioms() {
        loadBioms();
        LandscapeBiomsMapLevel1.setStoryObjectsByBiom(this);
    }

    private void loadBioms() {
        int fromBiomX = this.worldPosInAreas.x * 2;
        int fromBiomY = this.worldPosInAreas.y * 2;

        int rndX;
        int rndY;
        Cell biomOrigin;
        LandscapeBiomsMapLevel1.BiomMapData biomMapData;

        rndX = MathUtils.random(BIOM_POS_FIX, BIOM_R - BIOM_POS_FIX);
        rndY = MathUtils.random(BIOM_POS_FIX, BIOM_R - BIOM_POS_FIX);
        biomOrigin = getCell(rndX, rndY);
        assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(this);
        biomMapData = LandscapeBiomsMapLevel1.getBiom(fromBiomX, fromBiomY);
        bioms.add(new Biom(biomOrigin, biomMapData));

        rndX = MathUtils.random(BIOM_POS_FIX, BIOM_R - BIOM_POS_FIX);
        rndY = MathUtils.random(BIOM_R + BIOM_POS_FIX, BIOM_R * 2 - BIOM_POS_FIX);
        biomOrigin = getCell(rndX, rndY);
        assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(this);
        biomMapData = LandscapeBiomsMapLevel1.getBiom(fromBiomX, fromBiomY + 1);
        bioms.add(new Biom(biomOrigin, biomMapData));

        rndX = MathUtils.random(BIOM_R + BIOM_POS_FIX, BIOM_R * 2 - BIOM_POS_FIX);
        rndY = MathUtils.random(BIOM_POS_FIX, BIOM_R - BIOM_POS_FIX);
        biomOrigin = getCell(rndX, rndY);
        assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(this);
        biomMapData = LandscapeBiomsMapLevel1.getBiom(fromBiomX + 1, fromBiomY);
        bioms.add(new Biom(biomOrigin, biomMapData));

        rndX = MathUtils.random(BIOM_R + BIOM_POS_FIX, BIOM_R * 2 - BIOM_POS_FIX);
        rndY = MathUtils.random(BIOM_R + BIOM_POS_FIX, BIOM_R * 2 - BIOM_POS_FIX);
        biomOrigin = getCell(rndX, rndY);
        assertThat(biomOrigin.getArea()).as(BIOM_WIP_AREA_ERROR).isEqualTo(this);
        biomMapData = LandscapeBiomsMapLevel1.getBiom(fromBiomX + 1, fromBiomY + 1);
        bioms.add(new Biom(biomOrigin, biomMapData));
    }

    public Cell getRandomCell(int startPlusX, int endMinusX, int startPlusY, int endMinusY) {
        int rndX;
        int rndY;

        int maxX = WIDTH_IN_CELLS - endMinusX;
        if (startPlusX >= maxX) {
            rndX = startPlusX;
        } else {
            rndX = MathUtils.random(startPlusX, maxX);
        }

        int maxY = HEIGHT_IN_CELLS - endMinusY;
        if (startPlusY >= maxY) {
            rndY = startPlusY;
        } else {
            rndY = MathUtils.random(startPlusY, maxY);
        }

        return cells[rndX][rndY];
    }
}
