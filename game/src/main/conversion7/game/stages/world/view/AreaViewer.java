package conversion7.game.stages.world.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import conversion7.engine.AudioPlayer;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.audio.CellAudioSystem;
import conversion7.engine.artemis.engine.time.BasePollingComponent;
import conversion7.engine.artemis.engine.time.PollingSystem;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.artemis.scene.RefreshAreaViewCellsSystem;
import conversion7.engine.artemis.ui.AreaViewerCellSelectionSystem;
import conversion7.engine.artemis.ui.SwitchSquadHighlightSystem;
import conversion7.engine.artemis.ui.UnitUnderControlIndicatorSystem;
import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.Scene3dInputListener;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.dialogs.DialogWithUnit;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.InputProviderReset;
import conversion7.game.stages.GameStage;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldBattle;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.stream.Stream;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Known limitations:
 * 1) do not select object on viewer border cells;
 */
public class AreaViewer extends GameStage implements InputProviderReset {

    public final static int WIDTH_IN_AREAS = PropertiesLoader.getIntProperty("AreaViewer.WIDTH_IN_AREAS");
    public final static int HEIGHT_IN_AREAS = PropertiesLoader.getIntProperty("AreaViewer.HEIGHT_IN_AREAS");
    public final static int WIDTH_IN_CELLS = WIDTH_IN_AREAS * Area.WIDTH_IN_CELLS;
    public final static int HEIGHT_IN_CELLS = HEIGHT_IN_AREAS * Area.HEIGHT_IN_CELLS;
    public static final SceneGroup3d cellsSelectionGroup = new SceneGroup3d();
    protected final static int X_RADIUS = WIDTH_IN_AREAS / 2;
    protected final static int Y_RADIUS = HEIGHT_IN_AREAS / 2;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int AREA_TRANSITION_VIEW_CHECK_INTERVAL = 10;
    public static int DEVELOPER_AREA_VIEWER_CLICK_MODE =
            PropertiesLoader.getIntProperty("DEVELOPER.AREA_VIEWER_CLICK_MODE");
    public AreaView[][] views;
    public Cell mouseOverCell;
    public AbstractSquad mouseOverSquad;
    public boolean updateViewInProgress = false;
    public boolean created = false;
    public Point2s cameraPositionInAreas = new Point2s();
    public Point2s cameraPositionInCellsWip = new Point2s();
    public Point2s lastFocusedAreaPosition = new Point2s();
    public AreaViewerInputResolver inputResolver;
    public Cell selectedCell;
    public AbstractSquad selectedSquad;
    public AbstractSquad lastSelectedSquad;
    public Array<AbstractSquad> hintsUpdatedOnSquads = new Array<>();
    ViewHelper viewHelper;
    Vector3 savedCameraPosition;
    ModelActor waterModel;
    private CustomStage stage = new CustomStage(this.getClass().getSimpleName(), Gdxg.graphic.getCamera());
    private int areaTransitionViewCounter;
    private boolean interactionEnabled = true;
    private ModelActor mouseOverCellModel;
    private SceneNode3d actionHighlightNode;
    private ModelActor selectedCellModel;
    private ClientCore core;

    /** 0,0 area is loaded on scene coord 0,0 */
    public AreaViewer(Area startArea, ClientCore core) {
        this.core = core;
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

        stage.addListener(new Scene3dInputListener() {
            Vector3 cellCoord;

            @Override
            public boolean touchDown(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
                if (!interactionEnabled) {
                    return false;
                }

                cellCoord = MathUtils.toGameCoords(touchPoint);
                if (LOG.isDebugEnabled())
                    LOG.debug("Got touch in AreaViewer! x/y = " + cellCoord.x + "/" + cellCoord.y + ", button: " + button);
                if (mouseOverCell.getDiscovered() != null && mouseOverCell.getDiscovered().equals(Cell.Discovered.VISIBLE)) {
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
                Cell currentCell = Gdxg.core.world.getCellByGameCoordinate(cellCoord);
                if (!currentCell.equals(mouseOverCell)) {
                    mouseMovedOn(currentCell);
                    if (LOG.isDebugEnabled()) LOG.debug("cell = " + mouseOverCell);
                }
                if (LOG.isDebugEnabled())
                    LOG.debug("mouseMoved in AreaViewer! x/y = " + cellCoord.x + "/" + cellCoord.y);
                return true;
            }
        });

        LOG.info("created");

        loadView(startArea);
        created = true;
    }

    public static Vector3 getLoopedEnginePosForCell(Cell cell) {
        return new Vector3(cell.x + 0.5f - Area.WIDTH_IN_CELLS / 2f,
                cell.getLandscape().getTerrainVertexData().getHeight(),
                -(cell.y + 0.5f) + Area.HEIGHT_IN_CELLS / 2f);
    }

    public static void placeBodyOnCell(Cell cell, SceneNode3d sceneNode3d) {
        Assert.assertNull(sceneNode3d.getParent());
        Vector3 posInAreaViewer = getLoopedEnginePosForCell(cell);
        sceneNode3d.setPosition(posInAreaViewer);
    }

    public boolean isInteractionEnabled() {
        return interactionEnabled;
    }

    public void setInteractionEnabled(boolean interactionEnabled) {
        this.interactionEnabled = interactionEnabled;
    }

    public ModelActor getSelectedCellModel() {
        if (selectedCellModel == null) {
            selectedCellModel = Modeler.buildCellSelector(Color.WHITE);
        }
        return selectedCellModel;
    }

    public ModelActor getMouseOverCellModel() {
        if (mouseOverCellModel == null) {
            mouseOverCellModel = Modeler.buildCellSelector(Color.WHITE);
        }
        return mouseOverCellModel;
    }

    public SceneNode3d getActionHighlightNode() {
        if (actionHighlightNode == null) {
            actionHighlightNode = Modeler.buildCellSelector(Color.CYAN, 20f, 0.1f, Modeler.LEVEL1_SELECTION_ALPHA);
        }
        return actionHighlightNode;
    }

    public AbstractSquad getSelectedSquad() {
        return selectedSquad;
    }

    public Cell getSelectedCell() {
        return selectedCell;
    }

    public CustomStage getStage() {
        return stage;
    }

    public Area getFocusedArea() {
        return getCenterAreaView().loadedArea;
    }

    private AreaView getCenterAreaView() {
        return views[X_RADIUS][Y_RADIUS];
    }

    public Team getTeamOnView() {
        return core.world.lastActivePlayerTeam;
    }

    @Deprecated
    public void fullViewRefresh() {
        requestCellsRefresh();
    }

    /** Reloads whole Viewer. Slow. */
    public void loadView(Area focusedArea) {
        updateViewInProgress = true;
        LOG.info("< loadView, focusedArea: " + focusedArea);
        Timer timer = new Timer(LOG);

        for (int x = 0; x < WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < HEIGHT_IN_AREAS; y++) {
                // load areas into views
                views[x][y].assignArea(Gdxg.core.world.getArea(
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

    /** Cells added in {@link Cell#setRefreshedInView(boolean)} will be refreshed */
    public void requestCellsRefresh() {
        RefreshAreaViewCellsSystem.create(this);
    }

    private void updateWaterPlanePosition() {
        Vector3 centerPosition = getFocusedArea().areaView.mainGroup.globalPosition;
        // y and z is used in shader own dimension
        waterModel.setPosition(
                centerPosition.x - Water.VIEWER_BACK_OFFSET,
                waterModel.localPosition.y,
                centerPosition.z + Water.VIEWER_BACK_OFFSET - Gdxg.graphic.water.getPlaneHeight()
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

    public void showMouseOverCellHints() {
        if (ContinuousInput.isEnabled()) {
            Gdxg.clientUi.getDebugCellHint().showOn(mouseOverCell);
            if (mouseOverCell == null) {
                return;
            }
            Gdxg.clientUi.getCellDetailsRootPanel().getHighlightedCellPanel().showOn(mouseOverCell, selectedSquad);
            showMouseOverCellSelection(mouseOverCell);
            Gdxg.clientUi.getWorldMainWindow().worldUnitControlPanel.unitsComparisonPanel.mouseOverUnitTable.load(mouseOverSquad);
            CellAudioSystem cellAudioSystem = Gdxg.core.artemis.getSystem(CellAudioSystem.class);
            cellAudioSystem.resetMasterVol();
        }
    }

    private void hideMouseOverCellHints() {
        Gdxg.clientUi.getCellDetailsRootPanel().getHighlightedCellPanel().hide();
    }

    private void showMouseOverCellSelection(Cell mouseOverCell) {
        highlightCell(mouseOverCell, getMouseOverCellModel());
    }

    @Override
    public void act(float delta) {

        areaTransitionViewCounter++;
        if (areaTransitionViewCounter > AREA_TRANSITION_VIEW_CHECK_INTERVAL) {
            areaTransitionViewCounter = 0;

            if (!updateViewInProgress && !viewHelper.shiftInProgress) {
                updateCameraAreaPosByCellsPos(
                        Gdxg.graphic.getCameraController().getCamera2dPosition(cameraPositionInCellsWip),
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

    @Override
    public void onShow() {
        if (savedCameraPosition != null) {
            Gdxg.graphic.getCamera().position.set(savedCameraPosition);
        } else {
            savedCameraPosition = new Vector3();
            placeCameraOnAreaView(getCenterAreaView());
        }
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
    public void onHide() {
        savedCameraPosition.set(Gdxg.graphic.getCamera().position);
    }

    @Override
    public void dispose() {
        stage.dispose();
        for (Area area : core.world.areasList) {
            area.dispose();
        }
    }

    public void reset() {
        stage.clear();
    }

    private void mouseMovedOn(Cell newCell) {
        Cell mouseOverCellPrev = mouseOverCell;
        AbstractSquad mouseOverSquadPrev = mouseOverCellPrev == null ? null : mouseOverCellPrev.squad;
        if (mouseOverSquadPrev != null && mouseOverSquadPrev != selectedSquad) {
            switchSquadModelHighlight(mouseOverSquadPrev, false);
        }

        mouseOverCell = newCell;
        mouseOverSquad = mouseOverCell.getSquad();
        if (mouseOverSquad != null) {
            switchSquadModelHighlight(mouseOverSquad, true);
            Team playerTeam = newCell.getArea().world.lastActivePlayerTeam;
            if (playerTeam != null
                    && mouseOverSquad.team.isHumanPlayer()) {
                if (mouseOverSquad.hasBrainToTalk()) {
                    runEmbedUnitDialogBranch(playerTeam);
                }
            }
        }
        if (newCell.getDiscovered() != null && newCell.getDiscovered().equals(Cell.Discovered.VISIBLE)) {
            showMouseOverCellHints();
            if (mouseOverCell.getObjectsOnCell().size > 0) {
                if (mouseOverCell.hasSquad()) {
                    AudioPlayer.play("fx\\CLAV10.mp3").setVolume(0.1f);
                } else {
                    AudioPlayer.play("fx\\wooh6.mp3");
                }
            }
        } else {
            hideMouseOverCellHints();
        }
    }

    private void runEmbedUnitDialogBranch(Team playerTeam) {
        if (mouseOverSquad.isAnimal()) {
            return;
        }
        if (mouseOverSquad.team.isEnemyOf(playerTeam)) {
            if (testLuckShot()) {
                mouseOverSquad.batchFloatingStatusLines.addLine("Watch out!");
            }
        } else {
            if (testLuckShot()) {
                mouseOverSquad.batchFloatingStatusLines.addLine("Hey!");

            } else if (testLuckShot()) {
                if (mouseOverSquad.isArchon()) {
                    mouseOverSquad.batchFloatingStatusLines.addLine("Humans are very intelligent! Sometimes.");
                    if (testLuckShot()) {
                        for (AbstractSquad squad : new Array.ArrayIterable<>(mouseOverSquad.team.getSquads())) {
                            if (squad.isHuman()) {
                                SchedulingSystem.schedule("joke", 3000, () -> {
                                    squad.batchFloatingStatusLines.addLine("Does he kidding jokes?");
                                });
                                break;
                            }
                        }
                    }
                } else if (mouseOverSquad.isIlluminat()) {
                    mouseOverSquad.batchFloatingStatusLines.addLine("Humans are very important!");
                }
            }
        }

    }

    private boolean testLuckShot() {
        return MathUtils.testPercentChance(5);
    }

    public void switchSquadModelHighlight(AbstractSquad squad, boolean toHighlight) {
        SwitchSquadHighlightSystem.components.create(squad.entityId)
                .init(squad, toHighlight);
    }

    private void leftClick() {
        if (AnimationSystem.isLocking()) {
            UiLogger.addInfoLabel("Animation is active");
            return;
        }
        if (inputResolver == null) {
            mainAction();

        } else {
            completeInputeResolver();
        }
    }

    public void focusOnCellIfDoubleClick() {
        Gdxg.core.getGraphic().getCameraController().leftClick();
    }

    private void rightClick() {
        if (AnimationSystem.isLocking()) {
            return;
        }

        if (core.world.isPlayerTeamActive()) {
            if (inputResolver == null) {
                if (selectedSquad != null && mouseOverSquad != null) {
                    if (/*selectedCell.isNeighborOf(mouseOverCell)
                            && */!selectedSquad.isEnemyWith(mouseOverSquad.unit)) {
                        new DialogWithUnit(selectedSquad, mouseOverSquad).start();
                    } else {
                        if (!core.world.isBattleActive()) {
                            if (selectedSquad.hasAttackAp()) {
                                core.world.startBattle(selectedSquad.team, mouseOverSquad);
                            } else {
                                selectedCell.floatingStatusBatch.addLine("Need Attack AP to start battle");
                                selectedCell.floatingStatusBatch.flush(Color.LIGHT_GRAY);
                            }
                        }
                    }
                }
            } else {
                cancelInputResolver();
            }
        }
    }

    private void cancelInputResolver() {
        inputResolver.cancel();
        AudioPlayer.play("fx\\out.mp3");
        resetInputProvider();
    }

    private void mainAction() {
        World world = mouseOverCell.getArea().world;
        if (!world.isBattleActive()) {
//        }
//        if (selectedSquad == null
//                || (!world.isBattleActive() && mouseOverSquad != null && selectedSquad.team == mouseOverSquad.team)
//                /*|| (world.isBattleActive() && world.getActiveFight().getActiveSquad() == mouseOverSquad)*/) {
            if (mouseOverSquad != null) {
                selectCell(mouseOverCell);
                AudioPlayer.play("fx\\click1.mp3");
                return;
            }
        }

        if (selectedSquad.getTeam() != Gdxg.core.world.activeTeam && !GdxgConstants.DEVELOPER_MODE) {
            return;
        }

        if (mouseOverSquad == null) {
            //move
            if (mouseOverCell.canBeSeized()) {
                if (!selectedSquad.canMove() && !GdxgConstants.DEVELOPER_MODE) {
                    LOG.info("canMove false");
                    FloatingStatusOnCellSystem.scheduleMessage(selectedSquad.unit, "Can not move", Color.WHITE);
                    return;
                }

                Gdxg.clientUi.getCellDetailsRootPanel().getHighlightedCellPanel().hide();
                if (selectedSquad.getActiveTask() != null) {
                    selectedSquad.getActiveTask().cancel();
                }

                selectedSquad.moveStepsTo(mouseOverCell, GdxgConstants.DEVELOPER_MODE ? 999 : selectedSquad.getMoveAp());
            }

        } else if (selectedSquad.isNeighborOf(mouseOverSquad)) {
            if (selectedSquad.isEnemyWith(mouseOverSquad.unit)) {
                if (selectedSquad.canMeleeAttack()) {
                    if (world.isBattleActive()) {
                        WorldBattle activeFight = world.getActiveBattle();
                        if (activeFight.hasMember(mouseOverSquad)
                                && activeFight.hasMember(selectedSquad)) {
                            hideSelection();
                            AnimationSystem.lockAnimation();
                            selectedSquad.meleeAttack(mouseOverSquad);
                        } else {
                            FloatingStatusOnCellSystem.scheduleMessage(selectedSquad.unit,
                                    "Unit out of battle", Color.WHITE);
                        }
                    }
                } else {
                    FloatingStatusOnCellSystem.scheduleMessage(selectedSquad.unit, "Can not attack", Color.WHITE);
                }
            }
        }
    }

    private void completeInputeResolver() {
        if (inputResolver.hasAcceptableDistanceTo(mouseOverCell)
                && inputResolver.couldAcceptInput(mouseOverCell)) {
            AudioPlayer.play("fx\\CLAV3.mp3").setVolume(0.5f);
            inputResolver.beforeInputHandle();
            inputResolver.handleAcceptedInput(mouseOverCell);
            inputResolver.afterInputHandled();
            resetInputProvider();
        } else {
            AudioPlayer.playFail();
        }
    }

    public void refreshUnitActionSelectionInWorld(Array<AbstractWorldTargetableAction> actions) {
        if (actions.size == 0 || selectedSquad == null) {
            return;
        }

        if (selectedSquad.getTeam() != core.world.activeTeam
                && !GdxgConstants.DEVELOPER_MODE) {
            return;
        }

        if (cellsSelectionGroup.getParent() == null) {
            selectedSquad.getSceneBody().addNode(cellsSelectionGroup);
        }

        // show cells around
        for (AbstractWorldTargetableAction action : actions) {
            for (Cell cell : action.calculateAcceptableCells()) {
                Assert.assertNotNull(cell.selectionColor);
                ModelActor cellModel = Modeler.buildCellSelector(cell.selectionColor);

                // add selector model
                float originCellHeight = selectedSquad.getLastCell().getLandscape().getTerrainVertexData().getHeight();
                float neibCellHeight = cell.getLandscape().getTerrainVertexData().getHeight();
                if (cellModel != null) {
                    Point2s diff = selectedSquad.getLastCell().getDiffWithCell(cell);
                    cellModel.setPosition(MathUtils.toEngineCoords(diff.x, diff.y, neibCellHeight - originCellHeight));
                    cellsSelectionGroup.addNode(cellModel);
                }
            }
        }
    }

    public void clearCellsSelectionSelection() {
        cellsSelectionGroup.removeFromParent();
        cellsSelectionGroup.clearChildren();
    }

    public void unhideSelection() {
        cellsSelectionGroup.setVisible(true);
    }

    public void startInputResolving(AreaViewerInputResolver areaViewerInputResolver) {
        Assert.assertNotNull(areaViewerInputResolver);
        this.inputResolver = areaViewerInputResolver;
    }

    public void hideSelection() {
        cellsSelectionGroup.setVisible(false);
    }

    public void showSelection() {
        cellsSelectionGroup.setVisible(true);
    }

    @Override
    public void resetInputProvider() {
        inputResolver = null;
        Gdxg.graphic.getCameraController().setEnabled(true);
        Gdxg.clientUi.getWorldHintPanel().hide();
    }

    public void reselectionIfMoved(AreaObject movedSquad) {
        if (selectedSquad == movedSquad) {
            selectCell(movedSquad.getLastCell());
        }
    }

    public void selectCell(Cell newCell) {
        Assert.assertNotNull(newCell);
//        if (newCell == selectedCell && newCell.isRefreshedInView()) {
//            return;
//        }
//
//        focusOnCellIfDoubleClick();
//        deselectCell();
//
//        selectedCell = newCell;
//        showCellSelectedModel();
//        selectedSquad = newCell.squad;
//        LOG.info(" selectedSquad = " + selectedSquad);
//        if (selectedSquad != null) {
//            selectedSquad.getActionsController().forceTreeValidationFromThisNode();
//            switchSquadModelHighlight(selectedSquad, true);
//            if (selectedSquad.team.isHumanPlayer()) {
//                if (selectedSquad.canEquipItems()) {
//                    selectedSquad.checkAndNotifyIfCanEquipBetter();
//                }
//            }
//            selectedSquad.batchFloatingStatusLines.flush(Color.WHITE);
//        }
//        if (selectedCell.hasCamp()) {
//            highlightCamp(selectedCell.camp);
//        }
//        if (selectedCell.hasTotem()) {
//            highlightTotem(selectedCell.getTotem());
//        }
//        Gdxg.core.artemis.getSystem(UnitSelectionUiSystem.class).scheduleReselectionUnitAction();
        Gdxg.core.artemis.getSystem(AreaViewerCellSelectionSystem.class).selectCell(newCell);
    }

    public void deselectCell() {
        Cell prevCell = selectedCell;
        if (prevCell != null) {
            if (prevCell.hasSquad()) {
                lastSelectedSquad = prevCell.squad;
                switchSquadModelHighlight(lastSelectedSquad, false);
            }
            if (prevCell.hasCamp()) {
                PoolManager.CAMP_CELL_SELECTION.flush();
                PoolManager.CAMP_NET_CELL_SELECTION.flush();
            }
            if (prevCell.hasTotem()) {
                PoolManager.ALLY_TOTEM_CELL_SELECTION.flush();
                PoolManager.OTHERS_TOTEM_CELL_SELECTION.flush();
            }
        }
        selectedCell = null;
    }

    public void highlightTotem(AbstractTotem totem) {
        Pool<SceneNode3d> pool;
        if (core.world.isPlayerTeamAlive() && totem.team.isAllyOf(core.world.lastActivePlayerTeam)) {
            pool = PoolManager.ALLY_TOTEM_CELL_SELECTION;
        } else {
            pool = PoolManager.OTHERS_TOTEM_CELL_SELECTION;
        }
        SchedulingSystem.schedule("highlightTotem (OpenGL context)", 0, () -> {
            for (Cell cell : totem.getAffectedCells()) {
                highlightCell(cell, pool.obtain());
            }
        });
    }

    public void highlightCamp(Camp camp) {
        ObjectSet<Cell> netCells = camp.getNet().getCells();
        Array<Cell> selectedCampCells = camp.getCampCells();
        for (Cell selectedCampCell : selectedCampCells) {
            netCells.remove(selectedCampCell);
        }

        SchedulingSystem.schedule("highlightCamp (OpenGL context)", 0, () -> {
            for (Cell cell : selectedCampCells) {
                highlightCell(cell, PoolManager.CAMP_CELL_SELECTION.obtain());
            }
            for (Cell cell : netCells) {
                highlightCell(cell, PoolManager.CAMP_NET_CELL_SELECTION.obtain());
            }
        });
    }

    public void showCellSelectedModel() {
        showSelection();
        highlightCell(selectedCell, getSelectedCellModel());
    }

    public BasePollingComponent highlightCell(Cell cell, SceneNode3d selectionModel) {
        return PollingSystem.schedule("highlightCell", 100, 2f, () -> {
            if (cell.getArea().areaView == null) {
                return false;
            }
            Point2s positionOnViewInCells = cell.getGamePosOnViewInCells();
            Vector3 positionOnViewInCellsVec = new Vector3(positionOnViewInCells.x + 0.5f, positionOnViewInCells.y + 0.5f,
                    cell.getLandscape().getTerrainVertexData().getHeight());
            selectionModel.setPosition(MathUtils.toEngineCoords(positionOnViewInCellsVec));
            if (selectionModel.getParent() == null) {
                stage.addNode(selectionModel);
            }
            return true;
        });
    }

    public void clearSelectedCellUi() {
        clearCellsSelectionSelection();
        for (AbstractSquad squad : hintsUpdatedOnSquads) {
            squad.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().resetCellSelectionDependentIndicators();
            UnitUnderControlIndicatorSystem.components.create(squad.entityId).squad = squad;
        }
        hintsUpdatedOnSquads.clear();
    }

    public void showStealthIcons() {
        if (selectedSquad == null) {
            return;
        }
        Stream.concat(
                Stream.of(selectedSquad.visibleObjects.toArray()),
                Stream.of(selectedSquad.visibleForObjects.toArray()))
                .forEach(object -> {
                    if (object instanceof AbstractSquad) {
                        AbstractSquad abstractSquad = (AbstractSquad) object;
                        if (abstractSquad.getTeam() != selectedSquad.getTeam()) {
                            hintsUpdatedOnSquads.add(abstractSquad);
                            if (selectedSquad.visibleForObjects.contains(abstractSquad, true)) {
                                abstractSquad.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setIsVisible(true);
                            } else {
                                abstractSquad.getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setIsVisible(false);
                            }
                        }
                    }
                });
    }
}
