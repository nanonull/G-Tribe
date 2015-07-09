package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.Climate;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.ai.AiNodeDistanceComparator;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.utils.UiUtils;
import org.slf4j.Logger;

import java.awt.*;

public class Cell extends Point2s implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final int NOT_HEALTHY_TEMPERATURE_VALUE_PENALTY = 100;
    private static final int NOT_INITIALIZED_VALUE = java.lang.Integer.MIN_VALUE;
    public static final AiNodeDistanceComparator AI_NODE_DISTANCE_COMPARATOR = new AiNodeDistanceComparator();
    /** Should be even (2,4...) */
    public static final int CELL_TERRAIN_SEGMENTATION = 4;
    public static final float CELL_SEGMENT_SIZE = 1f / Cell.CELL_TERRAIN_SEGMENTATION;
    public static final int CELL_ORIGIN_IN_SEGMENTS = CELL_TERRAIN_SEGMENTATION / 2;
    public static final Point2s CELL_SEGMENTS_ORIGIN = new Point2s(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS);
    public static final float DISTANCE_FROM_CORNER_TO_ORIGIN_IN_SEGMENTS =
            Vector2.dst(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS, 0, 0);

    int id;
    private Discovered discovered;
    private final Area area;
    private AreaObject seizedBy;
    private Landscape landscape;
    private LandscapeController landscapeController;
    private int temperature;
    private int food;
    private int water;
    private int totalValue;
    private int totalValueCellsAround = NOT_INITIALIZED_VALUE;
    private int totalValueWithCellsAround = NOT_INITIALIZED_VALUE;

    private final Point2s worldPosInCells;
    private final Point2s worldPosInSegments;
    private final Point2s originWorldPosInSegments;
    private final int distanceToEquator;
    private final Array<Cell> neighborCells = new Array<>();
    public final PathData pathData;
    private BasicInventory inventory = new BasicInventory();
    private ResourcesGenerator resourcesGenerator = new ResourcesGenerator(this);
    private boolean refreshedInView;
    private DecalActor explorationDecal;
    private ModelGroup forestGroup;
    private ModelActor stoneGroup;

    public Cell(Area a, int x, int y) {
        super(x, y);
        this.area = a;
        this.id = Utils.getNextId();
        worldPosInCells = new Point2s(area.worldPosInCells).plus(x, y);
        worldPosInSegments = new Point2s(worldPosInCells.x * CELL_TERRAIN_SEGMENTATION,
                worldPosInCells.y * CELL_TERRAIN_SEGMENTATION);
        originWorldPosInSegments = new Point2s(worldPosInSegments).plus(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS);
        distanceToEquator = World.CENTRAL_CELL.y - worldPosInCells.y;
        landscapeController = new LandscapeController(this);
        pathData = new PathData(this);

        temperature = Climate.TEMPERATURE_CENTER + Climate.TEMPERATURE_RADIUS
                - Math.round(Math.abs(distanceToEquator) * Area.TEMPERATURE_PER_CELL);

        if (!GdxgConstants.AREA_VIEWER_FOG_OF_WAR_ENABLED) {
            setDiscovered(Discovered.VISIBLE);
        }
    }

    public DecalActor getExplorationDecal() {
        return explorationDecal;
    }

    public ModelGroup getForestGroup() {
        return forestGroup;
    }

    public ModelActor getStoneGroup() {
        return stoneGroup;
    }

    public void setForestGroup(ModelGroup forestGroup) {
        this.forestGroup = forestGroup;
    }

    public void setStoneGroup(ModelActor stoneGroup) {
        this.stoneGroup = stoneGroup;
    }

    public void setExplorationDecal(DecalActor explorationDecal) {
        this.explorationDecal = explorationDecal;
    }

    public void setRefreshedInView(boolean refreshedInView) {
        this.refreshedInView = refreshedInView;
    }

    public boolean isRefreshedInView() {
        return refreshedInView;
    }

    public Point2s getOriginWorldPosInSegments() {
        return originWorldPosInSegments;
    }

    public Discovered getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Discovered discovered) {
        this.discovered = discovered;
    }

    public BasicInventory getInventory() {
        resourcesGenerator.validate();
        return inventory;
    }

    public Area getArea() {
        return area;
    }

    public AreaObject getSeizedBy() {
        return seizedBy;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public LandscapeController getLandscapeController() {
        return landscapeController;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getFood() {
        return food;
    }

    public int getWater() {
        return water;
    }

    /** (Food + water) - howMuchCold * temperature-penalty */
    public int getTotalValue() {
        return totalValue;
    }

    public Point2s getWorldPosInCells() {
        return worldPosInCells;
    }

    public int getDistanceToEquator() {
        return distanceToEquator;
    }

    public Array<Cell> getNeighborCells() {
        return neighborCells;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public void setWater(int water) {
        this.water = water;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName().toUpperCase()).append(": ")
                .append(super.toString())
                .append(" ").append(area).toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public String getHint() {
        StringBuilder sb = new StringBuilder()
                .append("position: ").append(super.toString()).append(GdxgConstants.HINT_SPLITTER)
                .append(area).append(GdxgConstants.HINT_SPLITTER)
                .append("temperature: ").append(getTemperatureString()).append(GdxgConstants.HINT_SPLITTER)
                .append("food: ").append(food).append(GdxgConstants.HINT_SPLITTER)
                .append("water: ").append(water).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode() + area.worldPosInAreas.x + area.worldPosInAreas.y;
    }

    public String getTemperatureString() {
        return UiUtils.getTemperatureString(temperature);
    }

    /** Save links on neighbor cells (radius 1) */
    public void initCellsAround() {
        for (int i = 0; i < GdxgConstants.CELLS_AROUND.size; i++) {
            Point p = GdxgConstants.CELLS_AROUND.get(i);
            Point2s pCell = new Point2s(this.x + p.x, this.y + p.y);
            Cell c = area.getCell(pCell);
            if (c == null) {
                Utils.error(" c == null on getCellsAround!");
            }
            neighborCells.add(c);
        }
    }

    public void updateTotalValue() {
        this.totalValue = food + water;
        int howMuchCold = Unit.HEALTHY_TEMPERATURE_MIN - temperature;
        if (howMuchCold > 0) {
            this.totalValue -= howMuchCold * NOT_HEALTHY_TEMPERATURE_VALUE_PENALTY;
        }
    }

    public int getTotalValueCellsAround() {
        if (totalValueCellsAround == NOT_INITIALIZED_VALUE) {
            totalValueCellsAround = 0;
            for (Cell cell : neighborCells) {
                totalValueCellsAround += cell.totalValue;
            }
        }
        return totalValueCellsAround;
    }

    public int getTotalValueWithCellsAround() {
        if (totalValueWithCellsAround == NOT_INITIALIZED_VALUE) {
            totalValueWithCellsAround = totalValue + getTotalValueCellsAround();
        }
        return totalValueWithCellsAround;
    }


    public void seizeBy(AreaObject areaObject) {
        seizedBy = areaObject;
        seizedBy.setCell(this);
    }

    public void free() {
        seizedBy.setCell(null);
        seizedBy = null;
    }

    public boolean couldBeSeized() {
        return !isSeized() && hasLandscapeAvailableForMove();
    }

    public boolean isSeized() {
        return seizedBy != null;
    }

    public boolean isSeizedBy(AreaObject doesSeize) {
        return seizedBy.equals(doesSeize);
    }

    public boolean isSeizedByTeam(Team teamOwner) {
        return seizedBy.getTeam().equals(teamOwner);
    }

    public boolean isFarEnoughFromTeamNodes(AiTeamController controller) {
        for (AiNode node : controller.nodes) {
            if (node.origin.distanceTo(this) < AiTeamController.MINIMUM_DISTANCE_BETWEEN_NODES) {
                return false;
            }
        }
        return true;
    }

    public Array<AiNode> sortNodesByDistance(AiTeamController controller) {
        Cell.AI_NODE_DISTANCE_COMPARATOR.sort(controller.nodes, this);
        return controller.nodes;
    }

    public AiNode getTheClosestNodeFrom(AiTeamController controller) {
        if (controller.nodes.size <= 0) {
            return null;
        }
        return sortNodesByDistance(controller).get(0);
    }

    public boolean isVisibleOnView() {
        return area.areaView != null;
    }

    public Point2s getPositionOnViewInCells() {
        return new Point2s(this.area.areaView.posInViews.x * Area.WIDTH_IN_CELLS + this.x,
                this.area.areaView.posInViews.y * Area.WIDTH_IN_CELLS + this.y);
    }

    public float distanceTo(Cell cell) {
        return diffWithCell(cell).len();
    }

    public boolean isFromSouthHemisphere() {
        return distanceToEquator > 0;
    }

    /** Diff through world bounds */
    public Point2s diffWithCell(Cell cellTo) {
        Point2s fromPos = this.worldPosInCells;
        Point2s toPos = cellTo.worldPosInCells;

        int toMinusFromX = toPos.x - fromPos.x;
        int toMinusFromY = toPos.y - fromPos.y;
        Point2s theShortestDiffPoint = new Point2s();
        float theShortestDiffLen = java.lang.Float.MAX_VALUE;

        float curDiffLen;
        int curDiffX;
        int curDiffY;
        for (Point2s curMoveVariant : GdxgConstants.CELL_THROUGH_WORLD_BOUNDS_VARIANTS) {
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

    public boolean isNeighborOf(Cell cellForCheck) {
        return neighborCells.contains(cellForCheck, false);
    }

    public Cell getCouldBeSeizedNeighborCell() {
        for (Cell cell : neighborCells) {
            if (cell.couldBeSeized()) {
                return cell;
            }
        }
        return null;
    }

    public <T extends AreaObject> Array<T> getNeighborObjectsOfTeam(Class<T> objectClass, Team team) {
        Array<T> objects = PoolManager.ARRAYS_POOL.obtain();
        for (Cell cell : neighborCells) {
            if (cell.isSeized() && cell.isSeizedByTeam(team)
                    && objectClass.isInstance(cell.seizedBy)) {
                objects.add((T) cell.seizedBy);
            }
        }
        return objects;
    }

    /** 1 - get neighbor cells (circle on radius 1), 2 - cells from circle after neighbors, 3...n */
    public Array<Cell> getCellsAroundOnRadius(int radius) {
        if (radius <= 0) {
            Utils.error("Radius must be more 0!");
        }

        Array<Cell> cellsAroundOnRadius = PoolManager.ARRAYS_POOL.obtain();
        if (1 == radius) {
            cellsAroundOnRadius.addAll(neighborCells);
            return cellsAroundOnRadius;
        } else {
            int x;
            int y;
            y = radius;
            for (x = -radius; x <= radius; x++) {
                cellsAroundOnRadius.add(area.getCell(this.x + x, this.y + y));
            }
            x = radius;
            for (y = radius - 1; y >= -radius; y--) {
                cellsAroundOnRadius.add(area.getCell(this.x + x, this.y + y));
            }
            y = -radius;
            for (x = radius - 1; x >= -radius; x--) {
                cellsAroundOnRadius.add(area.getCell(this.x + x, this.y + y));
            }
            x = -radius;
            for (y = -radius + 1; y <= radius - 1; y++) {
                cellsAroundOnRadius.add(area.getCell(this.x + x, this.y + y));
            }
        }

        return cellsAroundOnRadius;
    }

    /** Get all cells around on all circles from radius 1 to #radius (inclusively) */
    public Array<Cell> getCellsAroundToRadiusInclusively(int radius) {
        return getCellsAroundFromToRadiusInclusively(1, radius);
    }

    /** Get all cells around on all circles #fromRadius #toRadius (inclusively) */
    public Array<Cell> getCellsAroundFromToRadiusInclusively(int fromRadius, int toRadius) {
        int curRadius = fromRadius;
        Array<Cell> cellsAroundCollected = PoolManager.ARRAYS_POOL.obtain();
        while (curRadius <= toRadius) {
            Array<Cell> cellsOnRadius = getCellsAroundOnRadius(curRadius);
            cellsAroundCollected.addAll(cellsOnRadius);
            PoolManager.ARRAYS_POOL.free(cellsOnRadius);
            curRadius++;
        }
        return cellsAroundCollected;
    }

    /** Returns objects from getCellsAroundFromToRadiusInclusively list */
    public Array<AreaObject> getObjectsAroundFromToRadiusInclusively(int fromRadius, int toRadius) {
        Array<Cell> cellsAround = getCellsAroundFromToRadiusInclusively(fromRadius, toRadius);
        Array<AreaObject> objectsAroundCollected = PoolManager.ARRAYS_POOL.obtain();
        for (Cell cell : cellsAround) {
            if (cell.isSeized()) {
                objectsAroundCollected.add(cell.seizedBy);
            }
        }

        PoolManager.ARRAYS_POOL.free(cellsAround);
        return objectsAroundCollected;
    }

    public boolean hasLandscapeAvailableForMove() {
        return landscape.type.equals(Landscape.TYPE.COMMON);
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
        this.landscape.setCell(this);
    }

    /** No equipment affect */
    public boolean hasGenerallyHealthyTemperature() {
        return temperature >= Unit.HEALTHY_TEMPERATURE_MIN;
    }

    /** At least 1 cell from HEALTHY_TEMPERATURE_MIN limit */
    public boolean isGoodToBeNode() {
        return temperature > Unit.HEALTHY_TEMPERATURE_MIN && getTotalValueWithCellsAround() >= AiTeamController.GOOD_CELL_FOR_NODE_WITH_AROUND_VALUES_LIMIT;
    }

    public boolean isComfortableFor(AreaObject object) {
        return hasGenerallyHealthyTemperature() && hasEnoughResourcesFor(object);
    }

    public boolean hasEnoughResourcesFor(AreaObject object) {
        return hasEnoughFoodFor(object) && hasEnoughWaterFor(object);
    }

    public boolean hasEnoughFoodFor(AreaObject object) {
        return this.food + object.getFoodStorage().getFood() >= object.getUnits().size;
    }

    public boolean hasEnoughWaterFor(AreaObject object) {
        return this.water >= object.getUnits().size;
    }

    /** Segment coord should be from 0 to {@link Cell#CELL_ORIGIN_IN_SEGMENTS} */
    public Point2s getSegmentWorldPos(int segmentX, int segmentY) {
        return new Point2s(worldPosInSegments).plus(segmentX, segmentY);
    }

    public static Point2s getDiffBtwSegments(Point2s segment1, Point2s segment2) {
        int toMinusFromX = segment1.x - segment2.x;
        int toMinusFromY = segment1.y - segment2.y;
        Point2s theShortestDiffPoint = new Point2s();
        float theShortestDiffLen = java.lang.Float.MAX_VALUE;

        float curDiffLen;
        int curDiffX;
        int curDiffY;
        for (Point2s curMoveVariant : GdxgConstants.SEGMENT_THROUGH_WORLD_BOUNDS_VARIANTS) {
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

    public static float getDistanceBtwSegments(Point2s segment1, Point2s segment2) {
        return getDiffBtwSegments(segment1, segment2).len();
    }

    public double getSegmentDistanceToOrigin(int segmentX, int segmentY) {
        return CELL_SEGMENTS_ORIGIN.distance(segmentX, segmentY);
    }

    public enum Discovered {
        NOT_VISIBLE, VISIBLE
    }

}
