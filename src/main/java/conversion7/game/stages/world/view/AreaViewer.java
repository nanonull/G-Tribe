package conversion7.game.stages.world.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.CustomInputListener;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.InputProviderReset;
import conversion7.game.stages.GameStage;
import conversion7.game.stages.quest.dialog.DialogWithSquadQuest;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.tasks.single.ExploreTask;
import conversion7.game.stages.world.ai.tasks.single.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import org.slf4j.Logger;
import org.testng.Assert;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Known limitations:
 * 1) do not select object on viewer border cells;
 */
public class AreaViewer extends GameStage implements InputProviderReset {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static int DEVELOPER_AREA_VIEWER_CLICK_MODE =
            PropertiesLoader.getIntProperty("DEVELOPER.AREA_VIEWER_CLICK_MODE");
    private static final int AREA_TRANSITION_VIEW_CHECK_INTERVAL = 10;
    public final static int WIDTH_IN_AREAS = PropertiesLoader.getIntProperty("AreaViewer.WIDTH_IN_AREAS");
    protected final static int X_RADIUS = WIDTH_IN_AREAS / 2;
    public final static int HEIGHT_IN_AREAS = PropertiesLoader.getIntProperty("AreaViewer.HEIGHT_IN_AREAS");
    protected final static int Y_RADIUS = HEIGHT_IN_AREAS / 2;
    public final static int WIDTH_IN_CELLS = WIDTH_IN_AREAS * Area.WIDTH_IN_CELLS;
    public final static int HEIGHT_IN_CELLS = HEIGHT_IN_AREAS * Area.HEIGHT_IN_CELLS;

    public AreaView[][] views;
    ViewHelper viewHelper;
    private CustomStage stage = new CustomStage(this.getClass().getSimpleName(), Gdxg.graphic.getCamera());

    public Cell mouseOverCell;
    AreaObject mouseOverObject;
    boolean updateViewInProgress = false;
    public boolean created = false;
    public Point2s cameraPositionInAreas = new Point2s();
    public Point2s cameraPositionInCellsWip = new Point2s();
    public Point2s lastFocusedAreaPosition = new Point2s();
    Vector3 savedCameraPosition;
    private AreaViewerInputResolver inputResolver;
    private int areaTransitionViewCounter;

    ModelActor waterModel;
    private boolean interactionEnabled = true;


    /** 0,0 area is loaded on scene coord 0,0 */
    public AreaViewer(Area startArea) {
        LOG.info("create");
        assertThat(WIDTH_IN_AREAS).as("AreaViewer.WIDTH_IN_AREAS should be >= 3").isGreaterThanOrEqualTo(3);
        assertThat(HEIGHT_IN_AREAS).as("AreaViewer.HEIGHT_IN_AREAS should be >= 3").isGreaterThanOrEqualTo(3);
        assertThat(WIDTH_IN_AREAS % 2).as("AreaViewer.WIDTH_IN_AREAS should be odd number (3,5,7...)").isNotEqualTo(0);
        assertThat(HEIGHT_IN_AREAS % 2).as("AreaViewer.HEIGHT_IN_AREAS should be odd number (3,5,7...)").isNotEqualTo(0);

        views = new AreaView[WIDTH_IN_AREAS][HEIGHT_IN_AREAS];
        for (int x = 0; x < WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < HEIGHT_IN_AREAS; y++) {
                AreaView areaView = new AreaView(this);
                views[x][y] = areaView;
                areaView.setPositionInViews(x - X_RADIUS, y - Y_RADIUS);
                areaView.applyPositions();
                stage.addNode(areaView.mainGroup);
            }
        }

        waterModel = Modeler.buildWaterModel();
        stage.addNode(waterModel);
        waterModel.setY(-0.2f);

        viewHelper = new ViewHelper(this);
        registerInputProcessors();

        stage.addListener(new CustomInputListener() {
            Vector3 cellCoord;

            @Override
            public boolean touchDown(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
                if (!interactionEnabled) {
                    return false;
                }

                cellCoord = MathUtils.toGameCoords(touchPoint);
                if (LOG.isDebugEnabled())
                    LOG.debug("Got touch in AreaViewer! x/y = " + cellCoord.x + "/" + cellCoord.y + ", button: " + button);
                mouseOverCell = World.getCellByGameCoordinate(cellCoord);
                if (mouseOverCell.getDiscovered() != null && mouseOverCell.getDiscovered().equals(Cell.Discovered.VISIBLE)) {
                    mouseOverObject = mouseOverCell.getSeizedBy();
                    if (LOG.isDebugEnabled()) LOG.debug("cell = " + mouseOverCell);
                    if (button == Input.Buttons.LEFT) {
                        leftClick();
                    } else if (button == Input.Buttons.RIGHT) {
                        rightClick();
                    }
                }
                return true;
            }

            @Override
            public boolean mouseMoved(CustomInputEvent event, Vector3 touchPoint) {
                if (!interactionEnabled) {
                    return false;
                }

                cellCoord = MathUtils.toGameCoords(touchPoint);
                Cell newSelectedCell = World.getCellByGameCoordinate(cellCoord);
                if (!newSelectedCell.equals(mouseOverCell)) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("mouseMoved in AreaViewer! x/y = " + cellCoord.x + "/" + cellCoord.y);
                    mouseOverCell = newSelectedCell;
                    if (LOG.isDebugEnabled()) LOG.debug("cell = " + mouseOverCell);
                }

                if (newSelectedCell.getDiscovered() != null && newSelectedCell.getDiscovered().equals(Cell.Discovered.VISIBLE)) {
                    showHintOnCell(false);
                } else {
                    showHintOnCell(true);
                    Gdxg.clientUi.getHighlightedCellBar().hide();
                }
                return true;
            }
        });

        LOG.info("created");

        loadView(startArea);
        created = true;
    }

    public boolean isInteractionEnabled() {
        return interactionEnabled;
    }

    public void setInteractionEnabled(boolean interactionEnabled) {
        this.interactionEnabled = interactionEnabled;
    }

    public CustomStage getStage() {
        return stage;
    }

    /** Reloads whole Viewer. Slow. */
    public void loadView(Area focusedArea) {
        updateViewInProgress = true;
        LOG.info("< loadView, focusedArea: " + focusedArea);
        Timer timer = new Timer(LOG);

        for (int x = 0; x < WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < HEIGHT_IN_AREAS; y++) {
                // load areas into views
                views[x][y].assignArea(World.getArea(
                        focusedArea.worldPosInAreas.x - X_RADIUS + x,
                        focusedArea.worldPosInAreas.y - Y_RADIUS + y));
                views[x][y].loadArea();
            }
        }

        updateWaterPlanePosition();

        LOG.info("focused area: " + getFocusedArea());
        timer.stop("> loadView");
        updateViewInProgress = false;
    }

    /** Cells added in {@link AreaViewer#addCellsToBeRefreshedInView(Array)} will be refreshed */
    public void refreshView() {
        updateViewInProgress = true;
        LOG.info("< refreshView, focusedArea: " + getFocusedArea());

        for (int x = 0; x < WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < HEIGHT_IN_AREAS; y++) {
                views[x][y].refreshArea();
            }
        }

        LOG.info("> refreshView");
        updateViewInProgress = false;
    }

    public void addCellsToBeRefreshedInView(Array<Cell> invalidCells) {
        for (Cell invalidCell : invalidCells) {
            addCellToBeRefreshedInView(invalidCell);
        }
        PoolManager.ARRAYS_POOL.free(invalidCells);
    }

    public void addCellToBeRefreshedInView(Cell invalidCell) {
        invalidCell.setRefreshedInView(false);
    }

    private void updateWaterPlanePosition() {
        Vector3 centerPosition = getFocusedArea().areaView.mainGroup.globalPosition;
        // y and z is used in shader own dimension
        waterModel.setPosition(
                centerPosition.x - Water.VIEWER_BACK_OFFSET_MAGIC,
                waterModel.localPosition.y,
                centerPosition.z + Water.VIEWER_BACK_OFFSET_MAGIC - Gdxg.graphic.water.getPlaneHeight()
        );
    }

    /** Shift Views of Viewer. Faster than {@link AreaViewer#loadView(Area)} */
    public void shiftView(Point2s shiftBy) {
        assertThat(Math.abs(shiftBy.x) <= 1 && Math.abs(shiftBy.x) <= 1).isTrue()
                .as("cannot shiftView by more than 1 area!");
        if (LOG.isDebugEnabled()) LOG.debug("< shiftView: " + shiftBy);
        Timer timer = new Timer(false);
        viewHelper.shiftView(shiftBy);
        updateWaterPlanePosition();
        if (LOG.isDebugEnabled()) LOG.debug(timer.stop("shiftView - new focused area: " + getFocusedArea()));
    }

    @Override
    public void draw() {
        stage.draw();
    }

    @Override
    public void registerInputProcessors() {
        inputProcessors.add(stage);
    }

    public void showHintOnCell(boolean debugOnly) {
        if (ContinuousInput.isEnabled()) {
            Gdxg.clientUi.getDebugCellHint().showOn(mouseOverCell);
            if (debugOnly) {
                return;
            }

            if (mouseOverCell.isSeized() && mouseOverCell.isSeizedBy(selectedObject)) {
                Gdxg.clientUi.getHighlightedCellBar().hide();
            } else {
                Gdxg.clientUi.getHighlightedCellBar().showOn(mouseOverCell, selectedObject);
            }
        }
    }

    @Override
    public void act(float delta) {
        World.getWorldTurnsQueue().queueLoop();

        areaTransitionViewCounter++;
        if (areaTransitionViewCounter > AREA_TRANSITION_VIEW_CHECK_INTERVAL) {
            areaTransitionViewCounter = 0;

            if (!updateViewInProgress && !viewHelper.shiftInProgress) {
                updateCameraAreaPosByCellsPos(
                        ClientCore.core.graphic.getCameraController().getCamera2dPosition(cameraPositionInCellsWip),
                        cameraPositionInAreas);

                if (GdxgConstants.CAMERA_3D_HANDLE_CHANGE_AREA_FOCUS &&
                        (cameraPositionInAreas.x != lastFocusedAreaPosition.x || cameraPositionInAreas.y != lastFocusedAreaPosition.y)) {
                    Point2s shiftBy = new Point2s(cameraPositionInAreas).minus(lastFocusedAreaPosition.x, lastFocusedAreaPosition.y);
                    lastFocusedAreaPosition.setLocation(cameraPositionInAreas);
                    if (LOG.isDebugEnabled()) LOG.debug("lastFocusedAreaPosition = " + lastFocusedAreaPosition);
                    shiftView(shiftBy);
                }
            }
        }

        stage.act(delta);
    }

    private void updateCameraAreaPosByCellsPos(Point2s cameraPositionInCells, Point2s cameraPositionInAreasOutput) {
        if (cameraPositionInCells.x < 0) {
            cameraPositionInCells.x -= Area.WIDTH_IN_CELLS;
        }
        if (cameraPositionInCells.y < 0) {
            cameraPositionInCells.y -= Area.HEIGHT_IN_CELLS;
        }
        cameraPositionInAreasOutput.setLocation(
                cameraPositionInCells.x / Area.WIDTH_IN_CELLS,
                cameraPositionInCells.y / Area.HEIGHT_IN_CELLS);
    }

    @Override
    public void onShow() {
        if (savedCameraPosition != null) {
            Gdxg.graphic.getCamera().position.set(savedCameraPosition);
        } else {
            savedCameraPosition = new Vector3();
            placeCameraOnAreaView(getCenterAreaView());
        }
    }

    @Override
    public void onHide() {
        savedCameraPosition.set(Gdxg.graphic.getCamera().position);
    }

    private AreaView getCenterAreaView() {
        return views[X_RADIUS][Y_RADIUS];
    }

    public Area getFocusedArea() {
        return getCenterAreaView().loadedArea;
    }

    /** Initial position on view. Not support place on negative position. */
    public void placeCameraOnAreaView(AreaView areaView) {
        Point2s cameraPosInCells = new Point2s(areaView.position.x, areaView.position.y);
        updateCameraAreaPosByCellsPos(cameraPosInCells, lastFocusedAreaPosition);
        if (LOG.isDebugEnabled()) LOG.debug("placeCameraOnAreaView = " + lastFocusedAreaPosition);
        Gdxg.graphic.getCameraController().setCamera2dPosition(
                cameraPosInCells.x - Area.WIDTH_IN_CELLS / 4,
                cameraPosInCells.y - Area.HEIGHT_IN_CELLS / 4);
    }

    // ************************************************************************************************************
    //          GAME INPUT
    // press on cells and actors
    // ************************************************************************************************************

    public AreaObject selectedObject;
    public static final SceneGroup3d selectionGroup = new SceneGroup3d();
    DecalActor unitCountLabelDecal;

    private void leftClick() {
        if (inputResolver == null) {
            // normal
            if (mouseOverObject == null) {
                if (selectedObject != null) {
                    deselect();
                }
                if (DEVELOPER_AREA_VIEWER_CLICK_MODE != 0) {
                    AreaViewerClickModeDebugger.addCell(mouseOverCell);
                }
            } else if (mouseOverObject.getTeam().equals(World.getActiveTeam()) || GdxgConstants.DEVELOPER_MODE) {
                if (!mouseOverObject.getTeam().equals(World.getActiveTeam())) {
                    UiLogger.addInfoLabel("[DEV] selected object of another team");
                }
                select(mouseOverObject);
                LOG.info(" newSelected = " + selectedObject);
            }

        } else {
            inputResolver.cancel();
            resetInputProvider();
            unhideSelection();
        }

    }

    private void rightClick() {
        if (inputResolver == null) {
            // NORMAL
            if (selectedObject == null) return;

            if (mouseOverObject == null) {
                //move
                if (selectedObject.isSquad() && mouseOverCell.couldBeSeized()) {
                    if (!selectedObject.couldMove()) {
                        LOG.info("couldMove false");
                        if (selectedObject.couldPartiallyMove()) {
                            LOG.info("couldPartiallyMove");
                            Gdxg.clientUi.getAreaObjectCouldNotMoveDialog().showFor(selectedObject, mouseOverCell);
                        } else {
                            LOG.info("couldPartiallyMove false");
                        }
                        return;
                    }

                    AbstractSquad selectedSquad = (AbstractSquad) selectedObject;

                    if (selectedSquad.getActiveTask() != null) {
                        selectedSquad.getActiveTask().cancel();
                    }

                    if (GdxgConstants.DEVELOPER_MODE) {
                        selectedSquad.moveOn(mouseOverCell);
                    } else { // move 1 cell
                        if (!selectedSquad.moveOneStepTo(mouseOverCell)) {
                            selectedSquad.setCurrentObjectTask(new MoveTask(selectedSquad, mouseOverCell));
                        }
                    }

                    deselect();
                }

            } else if (selectedObject.isNeighborOf(mouseOverObject)) {
                new DialogWithSquadQuest(selectedObject, mouseOverObject).start();
            }

        } else {
            // RESOLVE INPUT
            if (inputResolver.couldAcceptInput(mouseOverCell)) {
                inputResolver.handleInput(mouseOverCell);
                inputResolver.onInputHandled();
                resetInputProvider();
            }
        }
    }

    public void refreshSelectionBars() {
        Gdxg.clientUi.hideBarsForSelectedObject();
        Gdxg.clientUi.showBarsForSelectedObject();
    }

    public void select(AreaObject object) {
        if (selectedObject != null) {
            deselect();
        }
        selectedObject = object;
        selectedObject.getTeam().setLastSelectedObject(selectedObject);
        showSelectionGroup();
        Gdxg.clientUi.showBarsForSelectedObject();
    }

    public void showSelectionGroup() {
        if (selectionGroup.getParent() != null) {
            clearVisualSelection();
        }
        selectedObject.getSceneGroup().addNode(selectionGroup);

        String unitCountText = String.valueOf(selectedObject.getUnits().size);
        unitCountLabelDecal = Modeler.getLabelDecal(unitCountText, unitCountText.length() * 0.2f + 0.4f, 0.35f);
        selectionGroup.addNode(unitCountLabelDecal);
        unitCountLabelDecal.faceToCamera = true;
        unitCountLabelDecal.setPosition(MathUtils.toEngineCoords(0, 0, 1.5f));

        // show cells around
        if (selectedObject.isSquad() && selectedObject.couldPartiallyMove()) {
            for (Cell cellAround : selectedObject.getCell().getNeighborCells()) {
                ModelActor cellModel = null;
                if (!cellAround.isSeized()) { // not seized
                    if (cellAround.hasLandscapeAvailableForMove()) { // available for move
                        if (cellAround.hasEnoughResourcesFor(selectedObject)) { // enough resources
                            cellModel = Modeler.buildCellSelector(Color.GREEN);
                        } else {
                            cellModel = Modeler.buildCellSelector(Color.ORANGE);
                        }
                    }
                } else { // seized cell
                    if (!cellAround.isSeizedByTeam(selectedObject.getTeam())) { // enemy cell
                        cellModel = Modeler.buildCellSelector(Color.RED);
                    }
                }
                // add selector model
                float originCellHeight = selectedObject.getCell().getLandscape().getTerrainVertexData().getHeight();
                float neibCellHeight = cellAround.getLandscape().getTerrainVertexData().getHeight();
                if (cellModel != null) {
                    Point2s diff = selectedObject.getCell().diffWithCell(cellAround);
                    cellModel.setPosition(MathUtils.toEngineCoords(diff.x, diff.y, neibCellHeight - originCellHeight));
                    selectionGroup.addNode(cellModel);
                }
            }
        }
    }

    public void hideSelection() {
        selectionGroup.setVisible(false);
        Gdxg.clientUi.hideBarsForSelectedObject();
    }

    public void clearVisualSelection() {
        selectionGroup.removeFromParent();
        selectionGroup.clearChildren();
    }

    public void refreshAndUnhideSelection() {
        showSelectionGroup();
        unhideSelection();
    }

    // TODO check if unhideSelection could be replaced by refreshAndUnhideSelection
    public void unhideSelection() {
        selectionGroup.setVisible(true);
        Gdxg.clientUi.showBarsForSelectedObject();
    }

    public void deselect() {
        if (selectedObject != null) {
            selectedObject = null;
            clearVisualSelection();
            Gdxg.clientUi.hideBarsForSelectedObject();
        }
    }

    public void startInputResolving(AreaViewerInputResolver areaViewerInputResolver) {
        Assert.assertNotNull(areaViewerInputResolver);
        this.inputResolver = areaViewerInputResolver;

        if (inputResolver.getClass().equals(ExploreTask.class)) {
            Gdxg.graphic.getCameraController().setEnabled(false);
        }

        hideSelection();
    }

    @Override
    public void resetInputProvider() {
        inputResolver = null;
        Gdxg.graphic.getCameraController().setEnabled(true);
    }

}
