package conversion7.engine;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.dialog.view.DialogWindow;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.quest.QuestWindow;
import org.slf4j.Logger;

public class CameraController {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final boolean CAMERA_AS_ORTHO = false;
    public static final int CAMERA_START_HEIGHT = 7;
    public static final int CAMERA_MIN_HEIGHT = CAMERA_START_HEIGHT - 1;
    public static int CAMERA_MAX_HEIGHT = (CAMERA_START_HEIGHT + 2) * (GdxgConstants.DEVELOPER_MODE ? 10 : 1);
    private static final float CAMERA_3D_MODE_LOOK_AT_SHIFT = CAMERA_START_HEIGHT / 4f;
    private static final float CAMERA_3D_X_FIX_FOR_DIALOG = CAMERA_START_HEIGHT / 4.2f;
    private static final float CAMERA_3D_Y_FIX_FOR_DIALOG = CAMERA_START_HEIGHT / 2.5f;
    private static final float CAMERA_3D_MODE_LOOK_AT_FIX_FOR_CENTERING_UNITS = CAMERA_START_HEIGHT / 8f;
    private static final float VELOCITY_MIN = 0.01f;
    private static final float VECTOR_DELTA_MOVEMENT_LIMIT = 0.8f;
    private static final float VECTOR_DELTA_MOVEMENT_LIMIT_SQR = VECTOR_DELTA_MOVEMENT_LIMIT * VECTOR_DELTA_MOVEMENT_LIMIT;
    public static float CAM_MOVEMENT_SPEED;

    private PerspectiveCamera camera;

    private float deltaCamMovement;
    private Vector3 collectedMovementVector = new Vector3();
    private Vector3 currentDeltaMovement = new Vector3();
    private float currentVelocity = VELOCITY_MIN;
    private boolean enabled = true;
    private double doubleClickDelta;

    public CameraController(PerspectiveCamera camera, float CAM_MOVEMENT_SPEED) {
        this.camera = camera;
        this.CAM_MOVEMENT_SPEED = CAM_MOVEMENT_SPEED;

        if (CameraController.CAMERA_AS_ORTHO) {
            switchCameraToOrtho();
        } else {
            switchCameraTo3d();
        }
    }

    public Point2s getCamera2dPosition() {
        return getCamera2dPosition(new Point2s());
    }

    public float getCameraHeight() {
        return camera.position.y;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void scheduleCameraFocusOnPlayerSquad() {
        SchedulingSystem.schedule("scheduleCameraFocusOnPlayerSquad", 250, () -> {
            Team playerTeam = Gdxg.core.world.getLastActivePlayerTeam();
            if (playerTeam != null) {
                if (playerTeam.getSquads().size == 0) {
                    // postpone
                    scheduleCameraFocusOnPlayerSquad();
                } else {
                    scheduleCameraFocusOn(0, playerTeam.getSquads().get(0).getLastCell());
                }
            }
        });
    }

    public static void scheduleCameraFocusOn(int delayMillis, final Cell cell) {
        SchedulingSystem.schedule("scheduleCameraFocusOn", delayMillis, () -> {
            Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
        });
    }

    /** Look from top */
    public void switchCameraToOrtho() {
        camera.lookAt(camera.position.x,
                camera.position.y - CAMERA_START_HEIGHT,
                camera.position.z);
    }

    public void switchCameraTo3d() {
        camera.lookAt(camera.position.x + CAMERA_3D_MODE_LOOK_AT_SHIFT,
                camera.position.y - CAMERA_START_HEIGHT,
                camera.position.z - CAMERA_3D_MODE_LOOK_AT_SHIFT);
    }

    public Point2s getCamera2dPosition(Point2s outPoint) {
        outPoint.setLocation((int) camera.position.x, (int) -camera.position.z);
        return outPoint;
    }

    public void setCamera2dPosition(float x, float y) {
        camera.position.set(x, camera.position.y, -y);
    }

    public void setCamera3dPosition(float x, float y, float heightZ) {
        camera.position.set(x, heightZ, -y);
    }

    public void translateCamera(float x, float y) {
        camera.translate(x, 0, -y);
    }

    public void appendMovementVertical(float value) {
        if (enabled) {
            collectedMovementVector.y += value;
        }
    }

    public void update(float delta) {
        updateDoubleClickDelta(delta);

        deltaCamMovement = delta * currentVelocity * CAM_MOVEMENT_SPEED;

        currentDeltaMovement.interpolate(collectedMovementVector, deltaCamMovement, Interpolation.linear);
        // ##### Vector3#limit()
        float len2 = currentDeltaMovement.len2();
        if (len2 > VECTOR_DELTA_MOVEMENT_LIMIT_SQR) {
            // Vector3#nor()
            if (len2 != 0f && len2 != 1f) {
                float scalar = 1f / (float) Math.sqrt(len2);
                currentDeltaMovement.set(currentDeltaMovement.x * scalar,
                        currentDeltaMovement.y * scalar,
                        currentDeltaMovement.z * scalar);
            }
            // Vector3#scl(limit)
            currentDeltaMovement.set(currentDeltaMovement.x * VECTOR_DELTA_MOVEMENT_LIMIT,
                    currentDeltaMovement.y * VECTOR_DELTA_MOVEMENT_LIMIT,
                    currentDeltaMovement.z * VECTOR_DELTA_MOVEMENT_LIMIT);
        }
        collectedMovementVector.sub(currentDeltaMovement);

        if (collectedMovementVector.len() > 1) {
            currentVelocity += delta;
        } else {
            currentVelocity -= delta;
        }

        if (currentVelocity < VELOCITY_MIN) {
            currentVelocity = VELOCITY_MIN;
        } else if (currentVelocity > VECTOR_DELTA_MOVEMENT_LIMIT) {
            currentVelocity = VECTOR_DELTA_MOVEMENT_LIMIT;
        }

        camera.translate(currentDeltaMovement);
        currentDeltaMovement.set(0, 0, 0);
        camera.update();
    }

    public void switchCameraToFront() {
        camera.lookAt(camera.position.x + 1,
                camera.position.y,
                camera.position.z - 1);
    }

    public void moveCameraToLookAtSelectedAreaObject() {
        if (Gdxg.core.isAreaViewerActiveStage()) {
            if (Gdxg.core.areaViewer.getSelectedSquad() != null) {
                moveCameraToLookAtWorldCell(Gdxg.core.areaViewer.getSelectedSquad().getLastCell());
            }
        }
    }

    /** Look-at will focus correctly only for 3d-view mode. */
    public void moveCameraToLookAtWorldCell(Cell lookAtCell) {
        if (lookAtCell == null) {
            return;
        }
        Cell currentPositionInWorldCell = getPositionInCells();
        Point2s diffCurrentPosWithCellOnPos = currentPositionInWorldCell.getDiffWithCell(lookAtCell);

//        diffCurrentPosWithCellOnPos.x -= CAMERA_3D_MODE_LOOK_AT_SHIFT + CAMERA_3D_MODE_LOOK_AT_FIX_FOR_CENTERING_UNITS;
//        diffCurrentPosWithCellOnPos.y -= CAMERA_3D_MODE_LOOK_AT_SHIFT + CAMERA_3D_MODE_LOOK_AT_FIX_FOR_CENTERING_UNITS;

        QuestWindow questWindow = Gdxg.clientUi.getQuestWindow();
        DialogWindow dialogWindow = Gdxg.clientUi.getDialogWindow();
        if (((dialogWindow.isDisplayed() && !dialogWindow.isHiding()) || dialogWindow.isShowing())
                || (questWindow.isDisplayed() && !questWindow.isHiding()) || questWindow.isShowing()) {
            diffCurrentPosWithCellOnPos.x -= CAMERA_3D_X_FIX_FOR_DIALOG;
            diffCurrentPosWithCellOnPos.y += CAMERA_3D_Y_FIX_FOR_DIALOG;
        }
        setMovementVector(diffCurrentPosWithCellOnPos.x,
                diffCurrentPosWithCellOnPos.y);
    }

    public Cell getPositionInCells() {
        Vector3 gameCoordWip = MathUtils.toGameCoords(camera.position);
        gameCoordWip.x += CAMERA_3D_MODE_LOOK_AT_SHIFT + CAMERA_3D_MODE_LOOK_AT_FIX_FOR_CENTERING_UNITS;
        gameCoordWip.y += CAMERA_3D_MODE_LOOK_AT_SHIFT + CAMERA_3D_MODE_LOOK_AT_FIX_FOR_CENTERING_UNITS;
        Cell currentPositionInWorldCell = Gdxg.core.world.getCellByGameCoordinate(gameCoordWip);
        PoolManager.VECTOR_3_POOL.free(gameCoordWip);
        return currentPositionInWorldCell;
    }

    public void appendMovementVector(float gameX, float gameY) {
        if (enabled) {
            collectedMovementVector.x += gameX;
            collectedMovementVector.z -= gameY;
        }
    }

    public void setMovementVector(float gameX, float gameY) {
        if (enabled) {
            collectedMovementVector.x = gameX;
            collectedMovementVector.z = -gameY;
        }
    }

    public void updateDoubleClickDelta(float delta) {
        doubleClickDelta += delta;
    }

    public void leftClick() {
        if (doubleClickDelta < 0.5f) {
            scheduleCameraFocusOn(0, Gdxg.core.areaViewer.selectedCell);
        }
        doubleClickDelta = 0;
    }
}
