package conversion7.game.ui.inputlisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

/**
 * Handles everything after Ui stage and before game stages
 */
public class GlobalInputListener extends InputListener {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (LOG.isDebugEnabled()) LOG.debug("touchDown in " + this);
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        if (LOG.isDebugEnabled()) LOG.debug(keycode + " keyUp in " + this);
        if (handleKeyUpLocally(event, keycode)) {
            return true;
        }
        // lift down on other listeners (dynamic):
        return super.keyUp(event, keycode);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private boolean handleKeyUpLocally(InputEvent event, int keycode) {

        switch (keycode) {
            case Input.Keys.U:
                Gdxg.graphic.getCameraController().switchCameraToFront();
                return true;

            case Input.Keys.I:
                Gdxg.graphic.getCameraController().switchCameraTo3d();
                return true;

            case Input.Keys.O:
                Gdxg.graphic.getCameraController().switchCameraToOrtho();
                return true;

            case Input.Keys.C:
                Gdxg.graphic.getCameraController().moveCameraToLookAtSelectedAreaObject();
                return true;

            default:
                return false;
        }
    }
}
