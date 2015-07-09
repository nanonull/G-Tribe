package conversion7.game.ui.inputlisteners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import conversion7.engine.CameraController;
import conversion7.engine.DefaultClientGraphic;
import conversion7.engine.utils.Utils;
import conversion7.tests_standalone.misc.TestsCustomScene3d;
import org.slf4j.Logger;


/**
 * Used directly from render loop for very smooth things like keyboard press-and-hold.<br>
 * For single-press actions use GlobalInputListener.
 */
public class ContinuousInput {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static float DELTA_CAM_MOVEMENT;
    private static boolean enabled = true;
    private static DefaultClientGraphic defaultClientGraphic;

    public static void setEnabled(boolean enabled) {
        ContinuousInput.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void handle(float delta) {

        if (!enabled) return;

        DELTA_CAM_MOVEMENT = delta * CameraController.CAM_MOVEMENT_SPEED;

        if (TestsCustomScene3d.test_ModelAndDecalGroupsEnabled) {
            // handle keys in test
            TestsCustomScene3d.handleTestKeys(DELTA_CAM_MOVEMENT);
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { // -X in game
                TestsCustomScene3d.sceneNodeForMove.translate(-DELTA_CAM_MOVEMENT, 0, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { // +X in game
                TestsCustomScene3d.sceneNodeForMove.translate(DELTA_CAM_MOVEMENT, 0, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) { // +Y in game
                TestsCustomScene3d.sceneNodeForMove.translate(0, 0, -DELTA_CAM_MOVEMENT);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { // -Y in game
                TestsCustomScene3d.sceneNodeForMove.translate(0, 0, DELTA_CAM_MOVEMENT);
            }
        }

        // move camera vertical:
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // down
            defaultClientGraphic.getCameraController().appendMovementVertical(-DELTA_CAM_MOVEMENT);
            if (LOG.isDebugEnabled())
                LOG.debug("defaultClientGraphic.getCamera().position = " + defaultClientGraphic.getCamera().position);
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // up
            defaultClientGraphic.getCameraController().appendMovementVertical(DELTA_CAM_MOVEMENT);
            if (LOG.isDebugEnabled())
                LOG.debug("defaultClientGraphic.getCamera().position = " + defaultClientGraphic.getCamera().position);
        }

        // move camera:
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { // +X +Y in game
            defaultClientGraphic.getCameraController().appendMovementVector(DELTA_CAM_MOVEMENT, DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { // +X -Y in game
            defaultClientGraphic.getCameraController().appendMovementVector(DELTA_CAM_MOVEMENT, -DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { // -X -Y in game
            defaultClientGraphic.getCameraController().appendMovementVector(-DELTA_CAM_MOVEMENT, -DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { // -X +Y in game
            defaultClientGraphic.getCameraController().appendMovementVector(-DELTA_CAM_MOVEMENT, DELTA_CAM_MOVEMENT);
        }


        // rotate camera:
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) { // rotate <
            defaultClientGraphic.getCamera().rotate(delta * 100, 0, 1, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) { // rotate >
            defaultClientGraphic.getCamera().rotate(-delta * 100, 0, 1, 0);
        }
    }

    public static void init(DefaultClientGraphic defaultClientGraphic) {
        ContinuousInput.defaultClientGraphic = defaultClientGraphic;
    }
}
