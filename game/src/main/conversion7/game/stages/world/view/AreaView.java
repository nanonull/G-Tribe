package conversion7.game.stages.world.view;

import com.badlogic.gdx.math.Vector3;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.DecalGroup;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.terrain.TerrainVertexData;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.Landscape;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class AreaView {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final float TREE_RADIUS = Cell.CELL_SEGMENT_SIZE;

    public Area loadedArea;
    public Area newAssignedArea;

    /** High-level, contains only groups, is not reset */
    SceneGroup3d mainGroup;
    /** Contains decals, reset */
    private DecalGroup floorCellsGroup;
    /** Contains areaObjects and other models, is reset */
    public SceneGroup3d cellsGroup;

    /**
     * Game coords in Cell units.<br>
     * Could not be < 0.<br>
     * from center.
     */
    public Vector3 position = new Vector3();

    /**
     * Updated position.<p>
     * Game coords in Area units.<br>
     * from corner
     */
    public Point2s posInViews = new Point2s();
    boolean positionUpdated = false;
    private AreaViewer areaViewer;

    public AreaView(AreaViewer areaViewer) {
        this.areaViewer = areaViewer;
        mainGroup = new SceneGroup3d("AreaView mainGroup");
        mainGroup.setDimensions(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS, 4);
        mainGroup.createBoundingBox();
        mainGroup.setDoNotReturnMeOnTouch(true);
        mainGroup.frustrumRadius = MathUtils.getCircleRadiusAroundSquare(
                MathUtils.simpleMax(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS));

        floorCellsGroup = new DecalGroup("floorCellsGroup", Gdxg.decalBatchCommon);
        mainGroup.addNode(floorCellsGroup);
        floorCellsGroup.setDimensions(Area.WIDTH_IN_CELLS, Area.HEIGHT_IN_CELLS, 1);
        floorCellsGroup.createBoundingBox();

        cellsGroup = new SceneGroup3d("cellsGroup");
        mainGroup.addNode(cellsGroup);
        cellsGroup.setDimensions(mainGroup.widthX, mainGroup.widthY, mainGroup.height / 2);
        cellsGroup.createBoundingBox();
        cellsGroup.setDoNotReturnMeOnTouch(true);

    }

    public void applyPositions() {
        if (!positionUpdated) {
            return;
        }

        position.x = posInViews.x * Area.WIDTH_IN_CELLS + Area.WIDTH_IN_CELLS_HALF;
        position.y = posInViews.y * Area.HEIGHT_IN_CELLS + Area.HEIGHT_IN_CELLS_HALF;
        position.z = 0;

        Vector3 areaPosition = MathUtils.toEngineCoords(position.x, position.y, position.z);
        mainGroup.setPosition(areaPosition);
        floorCellsGroup.boundBoxActor.setY(-floorCellsGroup.height / 2f);
        cellsGroup.boundBoxActor.setY(cellsGroup.height / 2f);

        positionUpdated = false;
    }

    public void translateInViews(int x, int y) {
        setPositionInViews(posInViews.x + x, posInViews.y + y);
    }

    /** Game coordinates in AreaView units */
    public void setPositionInViews(int x, int y) {
        posInViews.x = x;
        posInViews.y = y;
        positionUpdated = true;
    }

    // TODO load 1 area around visible areas in background and then just link on newly decalGroupCells and update its position
    public void loadArea() {
        freeCurrentlyLoadedArea();

        loadedArea = newAssignedArea;
        newAssignedArea = null;
        loadedArea.areaView = this;

        mainGroup.addNode(loadedArea.getSceneGroup());
        loadedArea.buildTerrainModel();
        loadCells();
    }

    public void freeCurrentlyLoadedArea() {
        if (loadedArea == null) {
            return;
        }

        for (int x = 0; x < Area.WIDTH_IN_CELLS; x++) {
            for (int y = 0; y < Area.HEIGHT_IN_CELLS; y++) {
                freeCell(loadedArea.cells[x][y]);
            }
        }

        cellsGroup.clearChildren();
        floorCellsGroup.clearChildren();
        loadedArea.getSceneGroup().removeFromParent();
        loadedArea.areaView = null;
        loadedArea = null;
    }

    private void loadCells() {
        for (int x = 0; x < Area.WIDTH_IN_CELLS; x++) {
            Cell[] row = loadedArea.cells[x];
            for (int y = 0; y < Area.HEIGHT_IN_CELLS; y++) {
                loadCell(row[y]);
            }
        }
    }

    public void freeCell(Cell cell) {
        freeExplorationDecal(cell);

        if (cell.getStoneGroup() != null) {
            cell.getStoneGroup().returnToPool();
            cell.setStoneGroup(null);
        }

        if (cell.getForestGroup() != null) {
            cell.getForestGroup().returnToPool();
            cell.setForestGroup(null);
        }
    }

    public void loadCell(Cell cell) {
        // team to cell.discovered
        if (Gdxg.core.world.settings.fogOfWar) {
            Team teamOnView = areaViewer.getTeamOnView();
            if (teamOnView == null) {
                cell.setDiscovered(Cell.Discovered.VISIBLE);
            } else {
                if (teamOnView.canSeeCell(cell)) {
                    cell.setDiscovered(Cell.Discovered.VISIBLE);
                } else if (teamOnView.getExploredCells().contains(cell)) {
                    cell.setDiscovered(Cell.Discovered.NOT_VISIBLE);
                } else {
                    cell.setDiscovered(null);
                }
            }
        } else {
            cell.setDiscovered(Cell.Discovered.VISIBLE);
        }

        // use cell.discovered
        if (cell.getExplorationDecal() == null) {
            DecalActor newExplorationDecal = PoolManager.getExplorationDecal(cell);
            if (newExplorationDecal != null) {
                cell.setExplorationDecal(newExplorationDecal);
                floorCellsGroup.addDecal(newExplorationDecal);
                placeExplorationDecalOnCell(newExplorationDecal, cell.x, cell.y);
            }
        }

        // static objects
        if (cell.getDiscovered() != null) {
            if (cell.getLandscape().hasForest()) {
                if (cell.getForestGroup() == null) {
                    ModelGroup groupForest = PoolManager.FOREST_GROUP_POOL.obtain();
                    Point2s segment00 = cell.getSegmentWorldPos(1, 1);
                    Point2s segment10 = cell.getSegmentWorldPos(Cell.CELL_TERRAIN_SEGMENTATION - 1, 1);
                    Point2s segment01 = cell.getSegmentWorldPos(1, Cell.CELL_TERRAIN_SEGMENTATION - 1);
                    Point2s segment11 = cell.getSegmentWorldPos(Cell.CELL_TERRAIN_SEGMENTATION - 1, Cell.CELL_TERRAIN_SEGMENTATION - 1);

                    TerrainVertexData originVertex = cell.getLandscape().getTerrainVertexData();
                    TerrainVertexData vertexData;
                    vertexData = loadedArea.world.worldTerrainDataGrid.vertices[segment00.x][segment00.y];
                    groupForest.children.get(0).setPosition(-TREE_RADIUS, vertexData.getHeight() - originVertex.getHeight(), TREE_RADIUS);

                    vertexData = loadedArea.world.worldTerrainDataGrid.vertices[segment10.x][segment10.y];
                    groupForest.children.get(1).setPosition(TREE_RADIUS, vertexData.getHeight() - originVertex.getHeight(), TREE_RADIUS);

                    vertexData = loadedArea.world.worldTerrainDataGrid.vertices[segment01.x][segment01.y];
                    groupForest.children.get(2).setPosition(-TREE_RADIUS, vertexData.getHeight() - originVertex.getHeight(), -TREE_RADIUS);

                    vertexData = loadedArea.world.worldTerrainDataGrid.vertices[segment11.x][segment11.y];
                    groupForest.children.get(3).setPosition(TREE_RADIUS, vertexData.getHeight() - originVertex.getHeight(), -TREE_RADIUS);

                    cell.setForestGroup(groupForest);
                    cellsGroup.addNode(groupForest);
                    placeSceneNodeOnCell(groupForest, cell);
                }

            } else if (cell.getLandscape().type == Landscape.Type.MOUNTAIN) {
                if (cell.getStoneGroup() == null) {
                    ModelActor modelActor = PoolManager.STONE_MODEL_POOL.obtain();
                    cell.setStoneGroup(modelActor);
                    cellsGroup.addNode(modelActor);
                    placeSceneNodeOnCell(modelActor, cell);
                }
            }

            if (cell.getLandscape().type != Landscape.Type.MOUNTAIN) {
                SceneNode3d indicatorNode = cell.getInventoryItemsIndicatorNode();
                if (indicatorNode.getParent() == null) {
                    cellsGroup.addNode(indicatorNode);
                    placeSceneNodeOnCell(indicatorNode, cell);
                    indicatorNode.setY(0.05f);
                }
                cell.validateInventoryItemsIndicator();
            }

            if (cell.getLandscape().hasBog()) {
                SceneNode3d indicatorNode = cell.getBogIndicatorNode();
                if (indicatorNode.getParent() == null) {
                    cellsGroup.addNode(indicatorNode);
                    placeSceneNodeOnCell(indicatorNode, cell);
                    indicatorNode.setY(0.05f);
                }
                cell.validateBogIndicator();
            }
        }

        for (AreaObject areaObject : cell.getObjectsOnCell()) {
            areaObject.validateBodyInView();
        }

        if (areaViewer.selectedCell == cell) {
            areaViewer.selectCell(areaViewer.selectedCell);
        }
        cell.setRefreshedInView(true);
    }

    private void freeExplorationDecal(Cell cell) {
        if (cell.getExplorationDecal() != null) {
            cell.getExplorationDecal().removeFromParent();
            cell.setExplorationDecal(null);
        }
    }

    private void placeExplorationDecalOnCell(SceneNode3d sceneNode3d, int x, int y) {
        sceneNode3d.setPosition(x + 0.2f - Area.WIDTH_IN_CELLS / 2f,
                sceneNode3d.localPosition.y,
                -(y + 0.2f) + Area.HEIGHT_IN_CELLS / 2f);
    }

    private void placeSceneNodeOnCell(SceneNode3d sceneNode3d, Cell cell) {
        sceneNode3d.setPosition(cell.x + 0.5f - Area.WIDTH_IN_CELLS / 2f,
                cell.getLandscape().getTerrainVertexData().getHeight(),
                -(cell.y + 0.5f) + Area.HEIGHT_IN_CELLS / 2f);
    }

    public void refreshArea() {
        refreshCells();
    }

    private void refreshCells() {
        for (int x = 0; x < Area.WIDTH_IN_CELLS; x++) {
            for (int y = 0; y < Area.HEIGHT_IN_CELLS; y++) {
                Cell cell = loadedArea.cells[x][y];
                if (!cell.isRefreshedInView()) {
                    freeExplorationDecal(cell);
                    loadCell(cell);
                }
            }
        }
    }

    public void assignArea(Area area) {
        newAssignedArea = area;
        if (LOG.isDebugEnabled()) LOG.debug("assignArea: " + newAssignedArea);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName().toUpperCase())
                .append(" for: ").append(loadedArea);
        return sb.toString();
    }
}
