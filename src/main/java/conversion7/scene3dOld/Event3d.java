package conversion7.scene3dOld;


import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * The base class for all events.
 * <p/>
 * By default an event will "bubble" up through an actor's parent's handlers (see {@link #setBubbles(boolean)}).
 * <p/>
 * An actor's capture listeners can {@link #stop()} an event to prevent child actors from seeing it.
 * <p/>
 * An Event may be marked as "handled" which will end its propagation outside of the Stage (see {@link #handle()}).
 * The default {@link Actor#fire(Event)} will mark events handled if an {@link EventListener} returns true.
 * <p/>
 * A cancelled event will be stopped and handled.  Additionally, many actors will undo the side-effects of a canceled event.  (See {@link #cancel()}.)
 *
 * @see InputEvent
 * @see Actor#fire(Event)
 */


public class Event3d implements Poolable {
    private Stage3d stage;
    private Actor3d targetActor;
    private Actor3d listenerActor;
    private boolean capture; // true means event occurred during the capture phase
    private boolean bubbles = true; // true means propagate to target's parents
    private boolean handled; // true means the event was handled (the stage will eat the input)
    private boolean stopped; // true means event propagation was stopped
    private boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen

    /**
     * Marks this event as handled. This does not affect event propagation inside scene2d, but causes the {@link Stage}
     * event methods to return false, which will eat the event so it is not passed on to the application under the stage.
     */
    public void handle() {
        handled = true;
    }

    /**
     * Marks this event cancelled. This {@link #handle() handles} the event and {@link #stop() stops} the event
     * propagation. It also cancels any default action that would have been taken by the code that fired the event. Eg, if the
     * event is for a checkbox being checked, cancelling the event could uncheck the checkbox.
     */
    public void cancel() {
        cancelled = true;
        stopped = true;
        handled = true;
    }

    /**
     * Marks this event has being stopped. This halts event propagation. Any other listeners on the {@link #getListenerActor()
     * listener actor} are notified, but after that no other listeners are notified.
     */
    public void stop() {
        stopped = true;
    }

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

    /** Returns the actor that the event originated from. */
    public Actor3d getTarget() {
        return targetActor;
    }

    public void setTarget(Actor3d targetActor) {
        this.targetActor = targetActor;
    }

    /** Returns the actor that this listener is attached to. */
    public Actor3d getListenerActor() {
        return listenerActor;
    }

    public void setListenerActor(Actor3d listenerActor) {
        this.listenerActor = listenerActor;
    }

    public boolean getBubbles() {
        return bubbles;
    }

    /**
     * If true, after the event is fired on the target actor, it will also be fired on each of the parent actors, all the way to
     * the root.
     */
    public void setBubbles(boolean bubbles) {
        this.bubbles = bubbles;
    }

    /** {@link #handle()} */
    public boolean isHandled() {
        return handled;
    }

    /** @see #stop() */
    public boolean isStopped() {
        return stopped;
    }

    /** @see #cancel() */
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    /**
     * If true, the event was fired during the capture phase.
     *
     * @see Actor#fire(Event)
     */
    public boolean isCapture() {
        return capture;
    }

    public void setStage(Stage3d stage) {
        this.stage = stage;
    }

    /** The stage for the actor the event was fired on. */
    public Stage3d getStage() {
        return stage;
    }
}