package conversion7.game.ui.inputlisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

/** This listener has 1st priority in key handling. */
public class UiInputListener extends InputListener {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        return super.mouseMoved(event, x, y);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (LOG.isDebugEnabled()) LOG.debug("\ntouchDown in " + this);
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        LOG.debug(keycode + " keyUp in " + this);
        if (handleKeyUpLocally(event, keycode)) {
            return true;
        }
        // lift down on GlobalInputListener:
        return super.keyUp(event, keycode);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public boolean handleKeyUpLocally(InputEvent event, int keycode) {
        switch (keycode) {
            case Input.Keys.F1:
                Gdxg.clientUi.getConsole().turn();
                return true;
            case Input.Keys.F2:
                Gdxg.clientUi.getTestBar().turn();
                return true;
            default:
                return false;
        }
    }
}
