package conversion7.engine.customscene.input;

import com.badlogic.gdx.scenes.scene2d.Stage;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.SceneNode3d;

import static com.badlogic.gdx.utils.Pool.Poolable;

public class CustomEvent implements Poolable {

    public CustomStage stage;
    /** The actor that the event originated from. */
    public SceneNode3d targetActor;
    /** the actor that this listener is attached to */
    public SceneNode3d listenerActor;
    public boolean capture; // true means event occurred during the capture phase
    public boolean bubbles = true; // true means propagate to target's parents
    public boolean handled; // true means the event was handled (the stage will eat the input)
    public boolean stopped; // true means event propagation was stopped
    public boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen

    @Override
    public void reset() {
        stage = null;
        targetActor = null;
        listenerActor = null;
        capture = false;
        bubbles = true;
        handled = false;
        stopped = false;
        cancelled = false;
    }

    /**
     * Marks this event as handled. This does not affect event propagation inside scene2d, but causes the {@link Stage} event
     * methods to return false, which will eat the event so it is not passed on to the application under the stage.
     */
    public void handle() {
        handled = true;
    }

    /**
     * Marks this event cancelled. This {@link #handle() handles} the event and {@link #stop() stops} the event propagation. It
     * also cancels any default action that would have been taken by the code that fired the event. Eg, if the event is for a
     * checkbox being checked, cancelling the event could uncheck the checkbox.
     */
    public void cancel() {
        cancelled = true;
        stopped = true;
        handled = true;
    }

    /**
     * Marks this event has being stopped. This halts event propagation. Any other listeners on the {@link #} are notified, but after that no other listeners are notified.
     */
    public void stop() {
        stopped = true;
    }

}
