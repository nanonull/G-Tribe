package conversion7.game.ui.inputlisteners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import conversion7.engine.CameraController;
import conversion7.engine.DefaultClientGraphic;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.ufo.BaalBoss;
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
    private static int movedToAnimalSpawn;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        ContinuousInput.enabled = enabled;
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
        CameraController cameraController = defaultClientGraphic.getCameraController();
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // down
            if (cameraController.getCameraHeight() > CameraController.CAMERA_MIN_HEIGHT) {
                cameraController.appendMovementVertical(-DELTA_CAM_MOVEMENT);
                if (LOG.isDebugEnabled())
                    LOG.debug("graphic.getCamera().position = " + defaultClientGraphic.getCamera().position);
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // up
            if (cameraController.getCameraHeight() < CameraController.CAMERA_MAX_HEIGHT) {
                cameraController.appendMovementVertical(DELTA_CAM_MOVEMENT);
                if (LOG.isDebugEnabled())
                    LOG.debug("graphic.getCamera().position = " + defaultClientGraphic.getCamera().position);
            }
        }

        // move camera:
        if (Gdx.input.isKeyPressed(Input.Keys.W)) { // +X +Y in game
            cameraController.appendMovementVector(DELTA_CAM_MOVEMENT, DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { // +X -Y in game
            cameraController.appendMovementVector(DELTA_CAM_MOVEMENT, -DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { // -X -Y in game
            cameraController.appendMovementVector(-DELTA_CAM_MOVEMENT, -DELTA_CAM_MOVEMENT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { // -X +Y in game
            cameraController.appendMovementVector(-DELTA_CAM_MOVEMENT, DELTA_CAM_MOVEMENT);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            moveCameraToNextAnimalSpawn();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
            moveCameraToNextUnit(BaalBoss.class);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.U)) {
        }

        // rotate camera:
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) { // rotate <
            defaultClientGraphic.getCamera().rotate(delta * 100, 0, 1, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) { // rotate >
            defaultClientGraphic.getCamera().rotate(-delta * 100, 0, 1, 0);
        }
    }

    private static void moveCameraToNextTribe() {
        World world = Gdxg.core.world;
    }

    private static void moveCameraToNextUnit(Class<? extends Unit> targClass) {
        World world = Gdxg.core.world;
        for (Team team : world.teams) {
            for (AbstractSquad squad : team.getSquads()) {
                if (squad.unit.getClass() == targClass) {
                    CameraController.scheduleCameraFocusOn(0, squad.getLastCell());
                    break;
                }
            }
        }
    }

    private static void moveCameraToNextAnimalSpawn() {
        World world = Gdxg.core.world;
        AnimalSpawn spawn = world.animalSpawns.get(movedToAnimalSpawn);
        CameraController.scheduleCameraFocusOn(0, spawn.getLastCell());
        movedToAnimalSpawn++;
        if (movedToAnimalSpawn > world.animalSpawns.size - 1) {
            movedToAnimalSpawn = 0;
        }
    }

    private static boolean canCameraMoveVert() {
        float cameraHeight = defaultClientGraphic.getCameraController().getCameraHeight();
        return cameraHeight > 2 && cameraHeight < 10;
    }

    public static void init(DefaultClientGraphic defaultClientGraphic) {
        ContinuousInput.defaultClientGraphic = defaultClientGraphic;
    }
}
