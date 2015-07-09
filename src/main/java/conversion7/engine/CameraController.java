package conversion7.engine;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.ui.quest.QuestWindow;
import org.slf4j.Logger;

public class CameraController {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final boolean CAMERA_AS_ORTHO =
            PropertiesLoader.getIntProperty("CAMERA_3D_AS_ORTHO") == 1;
    public static final int CAMERA_START_HEIGHT =
            PropertiesLoader.getIntProperty("CAMERA_3D_START_HEIGHT");
    private static final int CAMERA_3D_MODE_LOOK_AT_SHIFT = CAMERA_START_HEIGHT / 2;
    private static final float VELOCITY_MIN = 0.02f;
    private static final float VECTOR_DELTA_MOVEMENT_LIMIT = 0.8f;
    private static final float VECTOR_DELTA_MOVEMENT_LIMIT_SQR = VECTOR_DELTA_MOVEMENT_LIMIT * VECTOR_DELTA_MOVEMENT_LIMIT;
    private static final float MAGIC_FOCUS_SHIFT_IN_QUEST_VIEW = .7f;
    public static float CAM_MOVEMENT_SPEED;

    private PerspectiveCamera camera;

    private float deltaCamMovement;
    private Vector3 collectedMovementVector = new Vector3();
    private Vector3 currentDeltaMovement = new Vector3();
    private float currentVelocity = VELOCITY_MIN;
    private boolean enabled = true;

    public CameraController(PerspectiveCamera camera, float CAM_MOVEMENT_SPEED) {
        this.camera = camera;
        this.CAM_MOVEMENT_SPEED = CAM_MOVEMENT_SPEED;

        if (CameraController.CAMERA_AS_ORTHO) {
            switchCameraToOrtho();
        } else {
            switchCameraTo3d();
        }
    }

    public Point2s getCamera2dPosition(Point2s outPoint) {
        outPoint.setLocation((int) camera.position.x, (int) -camera.position.z);
        return outPoint;
    }

    public Point2s getCamera2dPosition() {
        return getCamera2dPosition(new Point2s());
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

    public void appendMovementVector(float gameX, float gameY) {
        if (enabled) {
            collectedMovementVector.x += gameX;
            collectedMovementVector.z -= gameY;
        }
    }

    public void appendMovementVertical(float value) {
        if (enabled) {
            collectedMovementVector.y += value;
        }
    }

    public void update(float delta) {
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
        } else if (currentVelocity > 1) {
            currentVelocity = 1;
        }

        camera.translate(currentDeltaMovement);
        currentDeltaMovement.set(0, 0, 0);
        camera.update();
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

    public void switchCameraToFront() {
        camera.lookAt(camera.position.x + 1,
                camera.position.y,
                camera.position.z - 1);
    }

    /** Look-at will focus correctly only for 3d-view mode. */
    public void moveCameraToLookAtWorldCell(Cell lookAtCell) {
        Vector3 gameCoordWip = MathUtils.toGameCoords(camera.position);
        Cell currentPositionInWorldCell = World.getCellByGameCoordinate(gameCoordWip);
        Point2s diffCurrentPosWithCellOnPos = currentPositionInWorldCell.diffWithCell(lookAtCell);

        QuestWindow questWindow = Gdxg.clientUi.getQuestWindow();
        if ((questWindow.isDisplayed() && !questWindow.isHiding()) || questWindow.isShowing()) {
            appendMovementVector(
                    diffCurrentPosWithCellOnPos.x - CAMERA_3D_MODE_LOOK_AT_SHIFT * (1 + MAGIC_FOCUS_SHIFT_IN_QUEST_VIEW),
                    diffCurrentPosWithCellOnPos.y - CAMERA_3D_MODE_LOOK_AT_SHIFT * (1 - MAGIC_FOCUS_SHIFT_IN_QUEST_VIEW));
        } else {
            appendMovementVector(
                    diffCurrentPosWithCellOnPos.x - CAMERA_3D_MODE_LOOK_AT_SHIFT,
                    diffCurrentPosWithCellOnPos.y - CAMERA_3D_MODE_LOOK_AT_SHIFT);
        }

        PoolManager.VECTOR_3_POOL.free(gameCoordWip);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void moveCameraToLookAtSelectedAreaObject() {
        if (ClientCore.core.isAreaViewerActiveStage()) {
            if (World.getAreaViewer().selectedObject != null) {
                moveCameraToLookAtWorldCell(World.getAreaViewer().selectedObject.getCell());
            }
        }
    }
}
