package conversion7.engine.customscene.input;

import com.badlogic.gdx.math.Vector3;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class CustomInputEvent extends CustomEvent {

    private static final Logger LOG = Utils.getLoggerForClass();

    public Type type;

    /**
     * The pointer index for the event. The first touch is index 0, second touch is index 1, etc. Always -1 on desktop. Valid for:
     * touchDown, touchDragged, touchUp, enter, and exit.
     */
    public int pointer;
    /** The index for the mouse button pressed. Always 0 on Android. Valid for: touchDown and touchUp. */
    public int button;
    /** The key code of the key that was pressed. Valid for: keyDown and keyUp. */
    public int keyCode;
    /** The character for the key that was type. Valid for: keyTyped. */
    public char character;
    /** The amount the mouse was scrolled. Valid for: scrolled. */
    public int scrollAmount;
    /**
     * The actor related to the event. Valid for: enter and exit. For enter, this is the actor being exited, or null. For exit,
     * this is the actor being entered, or null.
     */
    public SceneNode3d relatedActor;
    public Vector3 whereIntersected;

    @Override
    public void reset() {
        super.reset();
        relatedActor = null;
        button = -1;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + type.toString();
    }

    /**
     * Types of low-level input events supported by stage2d.
     */
    static public enum Type {
        /**
         * A new touch for a pointer on the stage was detected
         */
        touchDown,
        /**
         * A pointer has stopped touching the stage.
         */
        touchUp,
        /**
         * A pointer that is touching the stage has moved.
         */
        touchDragged,
        /**
         * The mouse pointer has moved (without a mouse button being active).
         */
        mouseMoved,
        /**
         * The mouse pointer or an active touch have entered an actor.
         */
        enter,
        /**
         * The mouse pointer or an active touch have exited an actor.
         */
        exit,
        /**
         * The mouse scroll wheel has changed.
         */
        scrolled,
        /**
         * A keyboard key has been pressed.
         */
        keyDown,
        /**
         * A keyboard key has been released.
         */
        keyUp,
        /**
         * A keyboard key has been pressed and released.
         */
        keyTyped
    }
}

