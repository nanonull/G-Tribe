package conversion7.game.stages.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.terrain.TerrainChunk;
import conversion7.engine.geometry.terrain.TerrainVertexData;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.creator.WorldCreator;
import conversion7.game.stages.world.landscape.Biom;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.landscape.Soil;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.view.AreaView;
import org.slf4j.Logger;
import org.testng.Assert;

import java.awt.*;

import static org.fest.assertions.api.Assertions.assertThat;

public class Area {

    private static final Logger LOG = Utils.getLoggerForClass();

    public final static int WIDTH_IN_CELLS = PropertiesLoader.getIntProperty("Area.WIDTH_IN_CELLS");
    public final static int HEIGHT_IN_CELLS = PropertiesLoader.getIntProperty("Area.HEIGHT_IN_CELLS");
    public final static int WIDTH_IN_SEGMENTS = WIDTH_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;
    public final static int HEIGHT_IN_SEGMENTS = HEIGHT_IN_CELLS * Cell.CELL_TERRAIN_SEGMENTATION;
    public final static float HEIGHT_IN_CELLS_HALF = HEIGHT_IN_CELLS / 2f;
    public final static float WIDTH_IN_CELLS_HALF = WIDTH_IN_CELLS / 2f;
    public static final float TEMPERATURE_PER_CELL = (float) Climate.TEMPERATURE_PER_AREA / (float) HEIGHT_IN_CELLS;

    public int id;

    private boolean discovered;
    public Cell[][] cells = new Cell[WIDTH_IN_CELLS][HEIGHT_IN_CELLS];
    public Array<Biom> bioms = PoolManager.ARRAYS_POOL.obtain();
    public Array<AreaObject> objects = PoolManager.ARRAYS_POOL.obtain();
    public AreaView areaView;

    public final Point2s worldPosInAreas;
    public final Point2s worldPosInCells;
    private ModelActor terrain;
    private SceneGroup3d sceneGroup = new SceneGroup3d();

    public Area(int worldX, int worldY) {
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

    public void addObject(AreaObject object) {
        this.objects.add(object);
    }

    public void removeObject(AreaObject object) {
        this.objects.removeValue(object, false);
    }

    public Cell getCell(Cell cell, Point2s withDiff) {
        return getCell(cell, withDiff.x, withDiff.y);
    }

    public Cell getCell(Cell cell, int diffX, int diffY) {
        return getCell(cell.x + diffX, cell.y + diffY);
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
            return World.getArea(areaX, areaY).getCell(cellX, cellY);
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
        for (Point2s curMoveVariant : GdxgConstants.AREA_THROUGH_WORLD_BOUNDS_VARIANTS) {
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
        if (LOG.isDebugEnabled()) LOG.debug("objects.size " + objects.size);
        for (int i = objects.size - 1; i >= 0; i--) {
            objects.get(i).getStepEndController().execute();
        }
    }

    public ModelActor buildTerrainModel() {
        if (terrain == null) {
            terrain = Modeler.createTerrain(this);
            terrain.setPosition(-WIDTH_IN_CELLS_HALF, 0, HEIGHT_IN_CELLS_HALF);
            sceneGroup.addNode(terrain);
        }
        return terrain;
    }

    public SceneGroup3d getSceneGroup() {
        return sceneGroup;
    }

    public void calculateCellDetails() {
        int ax = worldPosInAreas.x;
        int ay = worldPosInAreas.y;

        Array<Cell> cellsWithComfortConditionsForFauna = PoolManager.ARRAYS_POOL.obtain();
        for (int cx = 0; cx < Area.WIDTH_IN_CELLS; cx++) {
            for (int cy = 0; cy < Area.HEIGHT_IN_CELLS; cy++) {
                Cell cell = cells[cx][cy];

                // TODO split on sub-methods
                // METHOD terrain map
                // main body vertices
                for (int segmX = 0; segmX < Cell.CELL_TERRAIN_SEGMENTATION; segmX++) {
                    for (int segmY = 0; segmY < Cell.CELL_TERRAIN_SEGMENTATION; segmY++) {
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData vertexData = TerrainChunk.getAverageInterpolatedVertex(cell, segmWorldPos,
                                (float) cell.getSegmentDistanceToOrigin(segmX, segmY));
                        Assert.assertNull(World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = vertexData;
                    }
                }

                // the last right vertices column
                if (ax == World.WIDTH_IN_AREAS - 1 && cx == Area.WIDTH_IN_CELLS - 1) {
                    for (int segmY = 0; segmY < Cell.CELL_TERRAIN_SEGMENTATION; segmY++) {
                        int segmX = Cell.CELL_TERRAIN_SEGMENTATION;
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData copyFrom = World.worldTerrainDataGrid.
                                vertices[0][segmWorldPos.y];
                        Assert.assertNull(World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
                    }
                }

                // the last top vertices row
                if (ay == World.HEIGHT_IN_AREAS - 1 && cy == Area.HEIGHT_IN_CELLS - 1) {
                    for (int segmX = 0; segmX < Cell.CELL_TERRAIN_SEGMENTATION; segmX++) {
                        int segmY = Cell.CELL_TERRAIN_SEGMENTATION;
                        Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                        TerrainVertexData copyFrom = World.worldTerrainDataGrid.
                                vertices[segmWorldPos.x][0];
                        Assert.assertNull(World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                        World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
                    }
                }

                // the last vertex in the top-right corner
                if (ax == World.WIDTH_IN_AREAS - 1 && cx == Area.WIDTH_IN_CELLS - 1
                        && ay == World.HEIGHT_IN_AREAS - 1 && cy == Area.HEIGHT_IN_CELLS - 1) {
                    int segmX = Cell.CELL_TERRAIN_SEGMENTATION;
                    int segmY = Cell.CELL_TERRAIN_SEGMENTATION;
                    Point2s segmWorldPos = cell.getSegmentWorldPos(segmX, segmY);
                    TerrainVertexData copyFrom = World.worldTerrainDataGrid.
                            vertices[0][0];
                    Assert.assertNull(World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y]);
                    World.worldTerrainDataGrid.vertices[segmWorldPos.x][segmWorldPos.y] = copyFrom;
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
                for (Cell neighborCell : cell.getNeighborCells()) {
                    if ((neighborCell.getLandscape().type.equals(Landscape.TYPE.COMMON) && hasForest)
                            ||
                            (neighborCell.getLandscape().type.equals(Landscape.TYPE.WATER))) {
                        foodBonusesFromNeighbors += WorldCreator.FOOD_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER;
                    }
                }


                cell.setFood(dirt * (hasForest ? 2 : 1) + foodBonusesFromNeighbors);
                cell.setFood(Math.round(cell.getFood() * temperatureMultiplier));

                // water calculation
                float waterBonusesFromNeighbors = 0;
                Array<Cell> cellsAroundToRadiusInclusively = cell.getCellsAroundToRadiusInclusively(3);
                for (Cell neighborCell : cellsAroundToRadiusInclusively) {
                    if ((neighborCell.getLandscape().type.equals(Landscape.TYPE.COMMON) && hasForest)
                            ||
                            (neighborCell.getLandscape().type.equals(Landscape.TYPE.WATER))) {
                        waterBonusesFromNeighbors += WorldCreator.WATER_BONUS_FROM_NEIGHBOR_FOREST_OR_WATER
                                / cell.distanceTo(neighborCell);
                    }
                }
                PoolManager.ARRAYS_POOL.free(cellsAroundToRadiusInclusively);

                cell.setWater(Math.round(
                        (dirt / 3 * (hasForest ? 1.5f : 1)
                                + waterBonusesFromNeighbors)
                                * temperatureMultiplier));

                //
                cell.updateTotalValue();

                if (cell.couldBeSeized() && Utils.RANDOM.nextInt(cell.getFood() + cell.getWater() + 1) > 60) {
                    cellsWithComfortConditionsForFauna.add(cell);
                }
            }
        }

        randomizeFaunaOn(cellsWithComfortConditionsForFauna);
        PoolManager.ARRAYS_POOL.free(cellsWithComfortConditionsForFauna);
    }

    private void randomizeFaunaOn(Array<Cell> cells) {
        if (cells.size == 0) {
            return;
        }

        cells.shuffle();
        boolean isAreaComfortableForPlayer = cells.get(0).getTemperature() >= Unit.HEALTHY_TEMPERATURE_MIN;
        WorldCreator.AREA_FAUNA_CREATION_RESULTS.reset();
        for (Cell cell : cells) {

            if (Utils.RANDOM.nextBoolean()) {
                // fauna: human
                if (WorldCreator.needHumanPlayer && isAreaComfortableForPlayer) {
                    // create at least 1 squad for player team
                    WorldCreator.needHumanPlayer = false;
                    WorldServices.createHumanSquadWithInitialUnits(World.getPlayerTeam(), cell);

                } else {
                    if (WorldCreator.TEAMS_COUNT_LIMIT > 0 && World.humanTeamsCreated == WorldCreator.TEAMS_COUNT_LIMIT) {
                        continue;
                    }
                    int humanSquadChance = cell.getFood() > WorldCreator.MAX_HUMAN_SQUAD_CHANCE ?
                            WorldCreator.MAX_HUMAN_SQUAD_CHANCE : cell.getFood();
                    if (Utils.RANDOM.nextInt(100) < humanSquadChance) {
                        Team team;
                        if (WorldCreator.AREA_FAUNA_CREATION_RESULTS.shouldCreateNewTeam()) {
                            team = World.createHumanTeam(false);
                        } else {
                            team = World.getLastCreatedHumanTeam();
                        }
                        WorldServices.createHumanSquadWithInitialUnits(team, cell);
                    }
                }

            } else {
                // fauna: animal
                if ((WorldCreator.MAX_ANIMAL_HERDS_AMOUNT_LIMIT == -1 || World.createdAnimalHerds < WorldCreator.MAX_ANIMAL_HERDS_AMOUNT_LIMIT)
                        && cell.couldBeSeized()) {
                    int animalHerdChance = cell.getFood() > WorldCreator.MAX_ANIMAL_HERD_CHANCE ?
                            WorldCreator.MAX_ANIMAL_HERD_CHANCE : cell.getFood();
                    if (Utils.RANDOM.nextInt(100) < animalHerdChance) {
                        WorldServices.createAnimalHerdWithRandomUnits(cell);
                    }
                }
            }
        }
    }
}
