package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Predicate;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.ui.float_lbl.CellFloatingStatusBatch;
import conversion7.engine.customscene.*;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.BattleConstants;
import conversion7.game.GameError;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.FloatingLabel;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldTrend;
import conversion7.game.stages.world.ai_deprecated.AiNode;
import conversion7.game.stages.world.ai_deprecated.AiNodeDistanceComparator;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.climate.Climate;
import conversion7.game.stages.world.inventory.CellInventory;
import conversion7.game.stages.world.objects.*;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.controllers.UnitDefeatedThrowable;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;
import org.slf4j.Logger;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static conversion7.engine.utils.MathUtils.testChance;


public class Cell extends Point2s implements HintProvider {

    public static final AiNodeDistanceComparator AI_NODE_DISTANCE_COMPARATOR = new AiNodeDistanceComparator();
    /** Should be even (2,4...) */
    public static final int CELL_TERRAIN_SEGMENTATION = 4;
    public static final float CELL_SEGMENT_SIZE = 1f / Cell.CELL_TERRAIN_SEGMENTATION;
    public static final int CELL_ORIGIN_IN_SEGMENTS = CELL_TERRAIN_SEGMENTATION / 2;
    public static final Point2s CELL_SEGMENTS_ORIGIN = new Point2s(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS);
    public static final float DISTANCE_FROM_CORNER_TO_ORIGIN_IN_SEGMENTS =
            Vector2.dst(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS, 0, 0);
    public static final float DISTANCE2_TO_ADJACENT_CORNER_CELL = 1.4f;
    public static final float MAX_DISTANCE2_TO_ADJACENT_CELL = DISTANCE2_TO_ADJACENT_CORNER_CELL;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int VALUE_PENALTY_PER_COLD_TEMPERATURE = 100;
    private static final int NOT_INITIALIZED_VALUE = java.lang.Integer.MIN_VALUE;
    private static final Comparator<? super Cell> VALUE_COMPARATOR = new Comparator<Cell>() {
        @Override
        public int compare(Cell o1, Cell o2) {
            int compareOrder = Integer.compare(o2.totalValue, o1.totalValue);
            return compareOrder;
        }
    };
    public final PathData pathData;
    private final Area area;
    private final Point2s worldPosInCells;
    private final Point2s worldPosInSegments;
    private final Point2s originWorldPosInSegments;
    private final int distanceToEquator;
    public CellFloatingStatusBatch floatingStatusBatch = new CellFloatingStatusBatch(this);
    public Array<FloatingLabel> postponedFloatingLabels = new Array<>();
    /** TOD Should be replaced by getObject */
    public AbstractSquad squad;
    public ResourcesGenerator resourcesGenerator;
    public Camp camp;
    public Array<AbstractSquad> visibleBySquads = new Array<>();
    public Color selectionColor;
    /** Ritual v1 */
    @Deprecated
    public Ritual ritual;
    public Cell downCell;
    public Cell upCell;
    public Cell leftCell;
    public Cell rightCell;
    public AbstractSquad lastDeadSquad;
    public Biom biomOrigin;
    int id;
    /**
     * WARN: it stores data for one viewer (data for player team only!)
     * null if not discovered
     */
    @Deprecated
    private Discovered discovered;
    private Landscape landscape;
    private LandscapeController landscapeController;
    private int baseTemperature;
    private int food;
    private int water;
    private int totalValue;
    private int totalValueCellsAround = NOT_INITIALIZED_VALUE;
    private int totalValueWithCellsAround = NOT_INITIALIZED_VALUE;
    private Array<Cell> cellsAround;
    private CellInventory inventory = new CellInventory(this);
    private boolean refreshedInView;
    private DecalActor explorationDecal;
    private ModelGroup forestGroup;
    private ModelActor stoneGroup;
    private SceneNode3dWith2dActor inventoryItemsIndicatorNode;
    private SceneNode3dWith2dActor bogIndicatorNode;
    private Label inventoryItemsIndicatorLabel;
    private Label bogIndicatorLabel;
    /** Totems affects this cell (cell in radius of totems) */
    private Array<AbstractTotem> effectiveTotems = new Array<>();
    /** Should be replaced by getObject */
    @Deprecated
    private AbstractTotem totem;
    private Array<AreaObject> objectsOnCell = new Array<>();
    private Array<AreaObject> lightSources = new Array<>();

    public Cell(Area a, int x, int y) {
        super(x, y);
        this.area = a;
        this.id = Utils.getNextId();
        resourcesGenerator = new ResourcesGenerator(this);
        worldPosInCells = new Point2s(area.worldPosInCells).plus(x, y);
        worldPosInSegments = new Point2s(worldPosInCells.x * CELL_TERRAIN_SEGMENTATION,
                worldPosInCells.y * CELL_TERRAIN_SEGMENTATION);
        originWorldPosInSegments = new Point2s(worldPosInSegments).plus(CELL_ORIGIN_IN_SEGMENTS, CELL_ORIGIN_IN_SEGMENTS);
        distanceToEquator = area.world.centralCellCoord.y - worldPosInCells.y;
        landscapeController = new LandscapeController(this);
        pathData = new PathData(this);

        baseTemperature = Climate.TEMPERATURE_CENTER + Climate.TEMPERATURE_RADIUS
                - Math.round(Math.abs(distanceToEquator) * Climate.TEMPERATURE_PER_CELL);

        if (!area.world.settings.fogOfWar) {
            setDiscovered(Discovered.VISIBLE);
        }
    }

    public static void sortByValue(Array<Cell> cells) {
        cells.sort(VALUE_COMPARATOR);
    }

    public boolean isVisibleForHumanPlayer() {
        for (AbstractSquad squad : visibleBySquads) {
            if (squad.team.isHumanPlayer()) {
                return true;
            }
        }
        return false;
    }

    public DecalActor getExplorationDecal() {
        return explorationDecal;
    }

    public void setExplorationDecal(DecalActor explorationDecal) {
        this.explorationDecal = explorationDecal;
    }

    public int getBaseTemperature() {
        return baseTemperature;
    }

    public SceneNode3d getBogIndicatorNode() {
        if (bogIndicatorNode == null) {
            bogIndicatorLabel = new Label(" B", Assets.labelStyle14orange);
            ClientUi.CELL_INDICATORS_LAYER.addActor(bogIndicatorLabel);
            bogIndicatorNode = new SceneNode3dWith2dActor(bogIndicatorLabel);
        }
        return bogIndicatorNode;
    }

    public Array<AbstractTotem> getEffectiveTotems() {
        return effectiveTotems;
    }

    public AbstractTotem getTotem() {
        return totem;
    }

    public void setTotem(AbstractTotem newTotem) {
        this.totem = newTotem;
        if (newTotem == null) {
            forceRefreshViewerForMe();
        } else {
            newTotem.validateView();
        }
    }

    public Array<AreaObject> getObjectsOnCell() {
        return objectsOnCell;
    }

    public Camp getCamp() {
        return camp;
    }

    public void setCamp(Camp camp) {
        this.camp = camp;
    }

    public ModelGroup getForestGroup() {
        return forestGroup;
    }

    public void setForestGroup(ModelGroup forestGroup) {
        this.forestGroup = forestGroup;
    }

    public ModelActor getStoneGroup() {
        return stoneGroup;
    }

    public void setStoneGroup(ModelActor stoneGroup) {
        this.stoneGroup = stoneGroup;
    }

    public boolean isRefreshedInView() {
        return refreshedInView;
    }

    public void setRefreshedInView(boolean refreshedInView) {
        this.refreshedInView = refreshedInView;
    }

    public Point2s getOriginWorldPosInSegments() {
        return originWorldPosInSegments;
    }

    public Discovered getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Discovered discovered) {
        this.discovered = discovered;
        setRefreshedInView(false);
    }

    public CellInventory getInventory() {
        return inventory;
    }

    public Area getArea() {
        return area;
    }

    public AbstractSquad getSquad() {
        return squad;
    }

    public void setSquad(AbstractSquad newSquad) {
        if (this.hasSquad()) {
            removeFromObjectsOnCell(this.squad);
        }
        if (newSquad != null && !objectsOnCell.contains(newSquad, true)) {
            addObject(newSquad);
        }
        this.squad = newSquad;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
        this.landscape.setCell(this);
    }

    public LandscapeController getLandscapeController() {
        return landscapeController;
    }

    public int getTemperature() {
        int temperatureWip = baseTemperature;
        Winter winter = getObject(Winter.class);
        if (winter != null) {
            temperatureWip += winter.getTemperatureAdd();
        }
        return temperatureWip;
    }

    public void setTemperature(int temperature) {
        this.baseTemperature = temperature;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public String getFoodUi() {
        return hasEnoughUnitFood() ? "enough" : "not enough";
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public String getWaterUi() {
        return hasEnoughUnitWater() ? "enough" : "not enough";
    }

    /** (Food + water) - howMuchCold * baseTemperature-penalty */
    public int getTotalValue() {
        return totalValue;
    }

    public Point2s getWorldPosInCells() {
        return worldPosInCells;
    }

    public int getDistanceToEquator() {
        return distanceToEquator;
    }

    public Array<Cell> getCellsAround() {
        return new Array<>(cellsAround);
    }

    public String getHint() {
        StringBuilder sb = new StringBuilder()
                .append("position: ").append(super.toString()).append(GdxgConstants.HINT_SPLITTER)
                .append(area).append(GdxgConstants.HINT_SPLITTER)
                .append("baseTemperature: ").append(getTemperatureString()).append(GdxgConstants.HINT_SPLITTER)
                .append("food: ").append(food).append(GdxgConstants.HINT_SPLITTER)
                .append("water: ").append(water).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public String getTemperatureString() {
        return UiUtils.getNumberWithSign(getTemperature());
    }

    public boolean isVisibleOnView() {
        return area.areaView != null;
    }

    public Point2s getGamePosOnViewInCells() {
        return new Point2s(this.area.areaView.posInViews.x * Area.WIDTH_IN_CELLS + this.x,
                this.area.areaView.posInViews.y * Area.WIDTH_IN_CELLS + this.y);
    }

    public boolean isFromSouthHemisphere() {
        return distanceToEquator > 0;
    }

    public Cell getCouldBeSeizedNeighborCell() {
        Optional<Cell> cellFound = Stream.of(cellsAround.toArray())
                .filter(Cell::canBeSeized)
                .findAny();
        if (cellFound.isPresent()) {
            return cellFound.get();
        } else {
            return null;
        }
    }

    /** At least 1 cell from HEALTHY_TEMPERATURE_MIN limit */
    public boolean isGoodToBeNode() {
        return getTemperature() > Unit.HEALTHY_TEMPERATURE_MIN && getTotalValueWithCellsAround() >= AiTeamControllerOld.GOOD_CELL_FOR_NODE_WITH_AROUND_VALUES_LIMIT;
    }

    public int getTotalValueWithCellsAround() {
        if (totalValueWithCellsAround == NOT_INITIALIZED_VALUE) {
            totalValueWithCellsAround = totalValue + getTotalValueCellsAround();
        }
        return totalValueWithCellsAround;
    }

    public int getTotalValueCellsAround() {
        if (totalValueCellsAround == NOT_INITIALIZED_VALUE) {
            totalValueCellsAround = 0;
            for (Cell cell : cellsAround) {
                totalValueCellsAround += cell.totalValue;
            }
        }
        return totalValueCellsAround;
    }

    public boolean isBlockingSight() {
        return landscape.type == Landscape.Type.MOUNTAIN || landscape.hasForest();
    }

    public boolean isGoodForCamp() {
        return true;
//        return food >= Camp.REQUIRED_CELL_FOOD
//                && water >= Camp.REQUIRED_CELL_WATER
//                /*&& hasHealthyTemperature()// try it*/;
    }

    public SceneNode3d getInventoryItemsIndicatorNode() {
        if (inventoryItemsIndicatorNode == null) {
            inventoryItemsIndicatorLabel = new Label("+", Assets.labelStyle14white2);
            ClientUi.CELL_INDICATORS_LAYER.addActor(inventoryItemsIndicatorLabel);
            inventoryItemsIndicatorNode = new SceneNode3dWith2dActor(inventoryItemsIndicatorLabel);
        }
        return inventoryItemsIndicatorNode;
    }

    public int getGatheringValue() {
        double gathValue = food * Team.EVOLUTION_EXP_PER_1_CLAIMED_FOOD;

        // plus
        if (area.world.trends.contains(WorldTrend.EXP_PLUS)) {
            gathValue *= WorldTrend.Mappings.EXP_PLUS_MLT;
        }

        // minus
        if (area.world.trends.contains(WorldTrend.EXP_MINUS)) {
            gathValue *= WorldTrend.Mappings.EXP_MINUS_MLT;
        }

        if (landscape.hasFireEffect()) {
            gathValue *= BurningForest.FIRE_EFFECT_FOOD_MLT;
        }
        Winter winter = getObject(Winter.class);
        if (winter != null) {
            gathValue = gathValue - (gathValue * winter.getWinterValue());
        }
        gathValue = Math.max(0, gathValue);


        return (int) Math.round(gathValue);
    }

    public Team getTeamByObjectsOnCell() {
        for (AreaObject object : objectsOnCell) {
            if (object.team != null) {
                return object.team;
            }
        }
        return null;
    }

    public void addFloatLabel(String txt, Color color) {
        addFloatLabel(txt, color, false);
    }

    public void addFloatLabel(String txt, Color color, boolean postponeTillFocus) {
        if (postponeTillFocus) {
            postponedFloatingLabels.add(new FloatingLabel(txt, color, area.world.step));
        } else {
            floatingStatusBatch.start();
            floatingStatusBatch.addLine(txt);
            floatingStatusBatch.flush(color);
        }
    }

    private void forceRefreshViewerForMe() {
        setRefreshedInView(false);
        refreshViewer();
    }

    public boolean hasTotem() {
        return totem != null;
    }

    public boolean hasSquad() {
        return squad != null;
    }

    public boolean canBeSeized() {
        return hasFreeMainSlot();
    }

    public boolean hasFreeMainSlot() {
        return Filters.CAN_SET_MAIN_SLOT_OBJ.evaluate(this);
    }

    public boolean hasLandscapeAvailableForMove() {
        return landscape.type.equals(Landscape.Type.COMMON);
    }

    public boolean hasLandscapeAvailableForBuilding() {
        return hasFreeMainSlot();
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

    @Override
    public int hashCode() {
        return super.hashCode() + area.worldPosInAreas.x + area.worldPosInAreas.y;
    }

    /** Save links on neighbor cells (radius 1) */
    public void initCellsAround() {
        cellsAround = new Array<>();
        for (int i = 0; i < BattleConstants.CELLS_AROUND.size; i++) {
            Point cellDiff = BattleConstants.CELLS_AROUND.get(i);
            Cell adjCell = area.getCell(this.x + cellDiff.x, this.y + cellDiff.y);
            if (adjCell == null) {
                throw new GdxRuntimeException(" adjCell == null on getCellsAround!");
            }
            cellsAround.add(adjCell);
            if (cellDiff.x == 0) {
                if (cellDiff.y == -1) {
                    downCell = adjCell;
                } else if (cellDiff.y == 1) {
                    upCell = adjCell;
                }
            } else if (cellDiff.y == 0) {
                if (cellDiff.x == -1) {
                    leftCell = adjCell;
                } else if (cellDiff.x == 1) {
                    rightCell = adjCell;
                }
            }
        }
    }

    public void updateTotalValue() {
        this.totalValue = food + water;
        int howMuchCold = Unit.HEALTHY_TEMPERATURE_MIN - getTemperature();
        if (howMuchCold > 0) {
            this.totalValue -= howMuchCold * VALUE_PENALTY_PER_COLD_TEMPERATURE;
        }
    }

    public boolean isSeizedBy(AbstractSquad doesSeize) {
        return squad != null && squad.equals(doesSeize);
    }

    public boolean isFarEnoughFromTeamNodes(AiTeamControllerOld controller) {
        for (AiNode node : controller.nodes) {
            if (node.origin.distanceTo(this) < AiTeamControllerOld.MINIMUM_DISTANCE_BETWEEN_NODES) {
                return false;
            }
        }
        return true;
    }

    /**
     * [0,0].distanceTo([0,1]) ==> 1<br>
     * [0,0].distanceTo([1,1]) ==> 1.4142135623730951<br>
     * ...<br>
     */
    public float distanceTo(Cell cell) {
        return getDiffWithCell(cell).len();
    }

    /**
     * [0,0].distanceTo([0,1]) ==> 1<br>
     * [0,0].distanceTo([1,1]) ==> 1<br>
     * ...<br>
     */
    public int distanceIntTo(Cell cell) {
        return (int) distanceTo(cell);
    }

    /**
     * Slower than variant 1<br>
     * [0,0].distanceTo([0,1]) ==> 1<br>
     * [0,0].distanceTo([1,1]) ==> 1.4<br>
     * ...<br>
     */
    public float distanceTo2(Cell cell) {
        return new BigDecimal(getDiffWithCell(cell).len()).setScale(1, RoundingMode.DOWN).floatValue();
    }

    /**
     * The shortest vector (through world bounds) to the target cell.<p></p>
     * Coordinates direction depends on from which cell diff started: <br>
     * [0,0].getDiffWithCell[1,1] = 1,1<br>
     * [1,1].getDiffWithCell[0,0] = -1,-1<br>
     */
    public Point2s getDiffWithCell(Cell cellTo) {
        Point2s fromPos = this.worldPosInCells;
        Point2s toPos = cellTo.worldPosInCells;

        int toMinusFromX = toPos.x - fromPos.x;
        int toMinusFromY = toPos.y - fromPos.y;
        Point2s theShortestDiffPoint = new Point2s();
        float theShortestDiffLen = java.lang.Float.MAX_VALUE;

        float curDiffLen;
        int curDiffX;
        int curDiffY;
        for (Point2s curMoveVariant : area.world.cellThroughWorldBoundsVariants) {
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

    public AiNode getTheClosestNodeFrom(AiTeamControllerOld controller) {
        if (controller.nodes.size <= 0) {
            return null;
        }
        return sortNodesByDistance(controller).get(0);
    }

    public Array<AiNode> sortNodesByDistance(AiTeamControllerOld controller) {
        Cell.AI_NODE_DISTANCE_COMPARATOR.sort(controller.nodes, this);
        return controller.nodes;
    }

    public boolean isNeighborOf(Cell cellForCheck) {
        return cellsAround.contains(cellForCheck, true);
    }

    public <T extends AreaObject> Array<T> getNeighborObjectsOfTeam(Class<T> objectClass, Team team) {
        Array<T> objects = PoolManager.ARRAYS_POOL.obtain();
        for (Cell cell : cellsAround) {
            if (cell.isSeizedByTeam(team)
                    && objectClass.isInstance(cell.squad)) {
                objects.add((T) cell.squad);
            }
        }
        return objects;
    }

    public boolean isSeizedByTeam(Team teamOwner) {
        return hasSquad() && squad.getTeam().equals(teamOwner);
    }

    /** Get all cells around on all circles from radius 1 to #radius (inclusively) */
    public Array<Cell> getCellsAroundToRadiusInclusively(int radius) {
        return getCellsAroundFromToRadiusInclusively(1, radius);
    }

    /** Get all cells around on all circles #fromRadius #toRadius (inclusively) */
    @Deprecated
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

    /**
     * Get all cells around on all circles #fromRadius #toRadius (inclusively)<br>
     * getCellsAround(0,1) ==>> get thisCell + 8 adjacent cells == 9 cells
     */
    public Array<Cell> getCellsAround(int fromRadius, int toRadius, Array<Cell> toArray) {
        int curRadius = fromRadius;
        while (curRadius <= toRadius) {
            getCellsAroundOnRadius(curRadius, toArray);
            curRadius++;
        }
        return toArray;
    }

    /**
     * 0 - me
     * 1 - get adjacent cells (circle on radius 1),
     * 2 - cells from circle after neighbors,
     * 3...n
     */
    public Array<Cell> getCellsAroundOnRadius(int onRadius, Array<Cell> toArray) {
        if (onRadius < 0) {
            throw new GdxRuntimeException("Radius must be >= 0!");
        }

        if (0 == onRadius) {
            toArray.add(this);
        } else if (1 == onRadius) {
            toArray.addAll(cellsAround);
        } else {
            int x;
            int y;
            y = onRadius;
            for (x = -onRadius; x <= onRadius; x++) {
                toArray.add(area.getCell(this.x + x, this.y + y));
            }
            x = onRadius;
            for (y = onRadius - 1; y >= -onRadius; y--) {
                toArray.add(area.getCell(this.x + x, this.y + y));
            }
            y = -onRadius;
            for (x = onRadius - 1; x >= -onRadius; x--) {
                toArray.add(area.getCell(this.x + x, this.y + y));
            }
            x = -onRadius;
            for (y = -onRadius + 1; y <= onRadius - 1; y++) {
                toArray.add(area.getCell(this.x + x, this.y + y));
            }
        }

        return toArray;
    }


    /**
     * 0 - me
     * 1 - get neighbor cells (circle on radius 1),
     * 2 - cells from circle after neighbors,
     * 3...n
     */
    @Deprecated
    public Array<Cell> getCellsAroundOnRadius(int radius) {
        if (radius < 0) {
            throw new GdxRuntimeException("Radius must be >= 0!");
        }

        Array<Cell> cellsAroundOnRadius = PoolManager.ARRAYS_POOL.obtain();
        if (0 == radius) {
            cellsAroundOnRadius.add(this);
        } else if (1 == radius) {
            cellsAroundOnRadius.addAll(cellsAround);
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

    public <A extends AreaObject> Array<A> getObjectsAroundFromToRadiusInclusively(int fromRadius, int toRadius, Class<A> objectsOfClass) {
        Array<Cell> cellsAround = getCellsAroundFromToRadiusInclusively(fromRadius, toRadius);
        Array<A> objects = new Array<>();
        for (Cell cell : cellsAround) {
            for (AreaObject objectOnCell : cell.objectsOnCell) {
                if (objectsOfClass.isInstance(objectOnCell)) {
                    objects.add((A) objectOnCell);
                }
            }
        }

        PoolManager.ARRAYS_POOL.free(cellsAround);
        return objects;
    }

    public <A extends AreaObject> Array<A> getObjectsAround(Class<A> objectsOfClass) {
        return getObjectsAroundFromToRadiusInclusively(1, 1, objectsOfClass);
    }

    public boolean isComfortableFor(AbstractSquad object) {
        return hasHealthyTemperature() && hasEnoughResourcesForUnit();
    }

    /** No equipment affect */
    public boolean hasHealthyTemperature() {
        return getTemperature() >= Unit.HEALTHY_TEMPERATURE_MIN;
    }

    public boolean hasEnoughResourcesForUnit() {
        return hasEnoughUnitFood() && hasEnoughUnitWater();
    }

    public boolean hasEnoughUnitFood() {
        return this.food >= Unit.HEALTHY_CELL_FOOD_MIN;
    }

    public boolean hasEnoughUnitWater() {
        return this.water >= Unit.HEALTHY_CELL_WATER_MIN;
    }

    /** Segment coord should be from 0 to {@link Cell#CELL_ORIGIN_IN_SEGMENTS} */
    public Point2s getSegmentWorldPos(int segmentX, int segmentY) {
        return new Point2s(worldPosInSegments).plus(segmentX, segmentY);
    }

    public double getSegmentDistanceToOrigin(int segmentX, int segmentY) {
        return CELL_SEGMENTS_ORIGIN.distance(segmentX, segmentY);
    }

    public String toStringAsWorldCoordinate() {
        return new StringBuilder("A-").append(area.worldPosInAreas.x).append("-").append(area.worldPosInAreas.y)
                .append(" C-").append(x).append("-").append(y).toString();
    }

    public Cell getCell(int diffX, int diffY) {
        return getArea().getCell(this, diffX, diffY);
    }

    public void validateInventoryItemsIndicator() {
        if (inventoryItemsIndicatorNode == null) {
            return;
        }
        if (!inventory.isEmpty() && discovered == Discovered.VISIBLE) {
            inventoryItemsIndicatorNode.setVisible(true);
            inventoryItemsIndicatorNode.getActor().setZIndex(ClientUi.Z_INDEX_CELL_INDICATOR);
            if (inventory.isContainsOnlyResources()) {
                inventoryItemsIndicatorLabel.setColor(Color.WHITE);
            } else {
                inventoryItemsIndicatorLabel.setColor(Color.YELLOW);
            }
        } else {
            inventoryItemsIndicatorNode.setVisible(false);
        }
    }

    public boolean hasCamp() {
        return camp != null;
    }

    public boolean hasRitual() {
        return ritual != null;
    }

    public void addEffectiveTotem(AbstractTotem totem) {
        this.effectiveTotems.add(totem);
    }

    public <B extends AbstractTotem> B getEffectiveTotem(Class<? extends AbstractTotem> totemClass, Team team) {
        for (AbstractTotem totem : this.effectiveTotems) {
            if (totem.getClass() == totemClass && totem.team == team) {
                return (B) totem;
            }
        }
        return null;
    }

    public <A extends AreaObject> A getObject(Class<A> objectsOfClass) {
        for (AreaObject object : objectsOnCell) {
            if (objectsOfClass.isInstance(object)) {
                return (A) object;
            }
        }
        return null;
    }

    public void addObject(AreaObject newObj) {
        if (newObj.isCellMainSlotObject()) {
            for (AreaObject object : objectsOnCell) {
                if (object.isCellMainSlotObject()) {
                    throw new GameError("Max CellMainSlotObjects = 2");
                }
            }
        }

        objectsOnCell.add(newObj);
    }

    public void removeFromObjectsOnCell(AreaObject object) {
        object.addSnapshotLog("removeFromObjectsOnCell " + getWorldPosInCells(), "");
        objectsOnCell.removeValue(object, true);
    }

    public <A extends AreaObject> A removeObjectIfExist(Class<A> objectsOfClass) {
        A found = null;
        for (AreaObject object : objectsOnCell) {
            if (objectsOfClass.isInstance(object)) {
                found = (A) object;
                break;
            }
        }
        if (found != null) {
            found.removeFromWorld();
        }
        return found;
    }

    public void endStepSimulation() {
        resourcesGenerator.refresh();

        if (squad != null) {
            if (!squad.isRemovedFromWorld()) {
                try {
                    squad.getStepEndController().execute();
                } catch (UnitDefeatedThrowable unitDefeatedEvent) {
                }
            }
        }

        if (camp != null && camp.isConstructionCompleted()) {
            camp.endStepSimulation();
        }

        for (AreaObject object : new Array<>(objectsOnCell)) {
            if (object.isAlive()) {
                if (object instanceof AreaObjectTickable) {
                    ((AreaObjectTickable) object).tick();
                }
            } else {
                objectsOnCell.removeValue(object, true);
            }
        }

        tryGeneratePrimalExp();
    }

    private void tryGeneratePrimalExp() {
        if (!hasSquad() && testChance(1, 100)
                && PrimalExperienceJewel.canBeCreatedOn(this)) {
            new PrimalExperienceJewel(this);
        }
    }

    public void refreshViewer() {
        AreaViewer areaViewer = Gdxg.getAreaViewer();
        if (areaViewer != null) {
            areaViewer.requestCellsRefresh();
        }
    }

    public boolean canBeFired() {
        return getLandscape().hasForest() && !getLandscape().hasFireEffect();
    }

    public void validateBogIndicator() {
        if (bogIndicatorNode == null) {
            return;
        }
        if (discovered == Discovered.VISIBLE) {
            bogIndicatorNode.setVisible(true);
            bogIndicatorNode.getActor().setZIndex(ClientUi.Z_INDEX_CELL_INDICATOR);
            bogIndicatorLabel.setColor(Color.ORANGE);
        } else {
            bogIndicatorNode.setVisible(false);
        }
    }

    public boolean hasLight() {
        return lightSources.size > 0;
    }

    public void addLightSource(AreaObject areaObject) {
        setRefreshedInView(false);
        lightSources.add(areaObject);
        if (lightSources.size == 1) {
            refreshVisibilityDueToLightingChange();
        }
    }

    public void removeLightSource(AreaObject object) {
        setRefreshedInView(false);
        lightSources.removeValue(object, true);
        if (lightSources.size == 0) {
            refreshVisibilityDueToLightingChange();
        }
    }

    private void refreshVisibilityDueToLightingChange() {
        Array<Cell> cellsAround = getCellsAround(0, World.MAX_VIEW_RADIUS, new Array<>());
        ObjectSet<Team> teamsToRefresh = new ObjectSet<>();
        for (Cell cell : cellsAround) {
            if (cell.hasSquad() && !cell.squad.isRemovedFromWorld()) {
                cell.squad.refreshVisibleCells();
                teamsToRefresh.add(cell.squad.team);
            }
        }

        for (Team team : teamsToRefresh) {
            team.recalculateVisibleCellsPlayerTribeOnly();
        }
        refreshViewer();
    }

    public boolean hasWinter() {
        return getObject(Winter.class) != null;
    }

    // see main slot
    @Deprecated
    public boolean canContainObject(Class<? extends AreaObject> aClass) {
        if (aClass == MountainDebris.class) {
            return canBeSeized();
        }
        return true;
    }

    public boolean canBeAttacked() {
        return hasSquad() || hasDestroyableObject();
    }

    private boolean hasDestroyableObject() {
        for (AreaObject object : objectsOnCell) {
            if (object.hasPower()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasObjectWithPower() {
        for (AreaObject areaObject : getObjectsOnCell()) {
            if (areaObject.hasPower() && areaObject.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCornerCellTo(Cell otherCell) {
        return x != otherCell.x && y != otherCell.y;
    }

    private boolean hasResourceObject() {
        for (AreaObject object : objectsOnCell) {
            if (object instanceof ResourceObject) {
                return true;
            }
        }

        return false;
    }

    private boolean hasMainSlotObject() {
        for (AreaObject object : objectsOnCell) {
            if (object.isCellMainSlotObject()) {
                return true;
            }
        }
        return false;
    }

    public boolean canProvideCornerDefence() {
        if (getLandscape().hasHill()
                || getLandscape().hasForest()
                || getLandscape().hasMountain()) {
            return true;
        } else {
            for (AreaObject object : objectsOnCell) {
                if (object.givesCornerDefenceBonus()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsObject(Class<? extends AreaObject> cls) {
        return getObject(cls) != null;
    }

    public enum Discovered {
        NOT_VISIBLE, VISIBLE
    }

    public static class Filters {
        public static final Predicate<Cell> NONE = cell -> true;
        public static final Predicate<Cell> CAN_SET_MAIN_SLOT_OBJ = cell ->
                cell.hasLandscapeAvailableForMove() &&
                        !cell.hasMainSlotObject();
        public static final Predicate<Cell> CAN_SET_RESOURCE_OBJ = cell -> !cell.hasResourceObject()
                && cell.hasLandscapeAvailableForBuilding();
        public static final Predicate<Cell> CAN_SET_DEBRIS = cell -> cell.canBeSeized();
        public static final Predicate<Cell> CAN_SET_TOTEM = cell -> AbstractTotem.canBeCreatedOn(cell);
        public static final Predicate<Cell> CAN_PLACE_AI_TRIBE = cell -> cell.canBeSeized();
        public static final Predicate<Cell> CAN_SET_SQUAD = cell -> cell.canBeSeized();

        public static final Predicate<Cell> CAN_SET_JEWEL_EXP = cell -> cell.getObject(PrimalExperienceJewel.class) == null
                && cell.canBeSeized();
    }


}
