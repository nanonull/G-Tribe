package conversion7.engine.customscene.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import conversion7.engine.customscene.SceneNode3d;

/**
 * Created by MP on 29.07.2014.
 */
public class CustomInputListener implements CustomInputListenerAbstract {

    @Override
    public boolean handle(CustomEvent e) {
        if (!(e instanceof CustomInputEvent)) return false;
        CustomInputEvent event = (CustomInputEvent) e;

        switch (event.type) {
            case keyDown:
                return keyDown(event, event.keyCode);
            case keyUp:
                return keyUp(event, event.keyCode);
            case keyTyped:
                return keyTyped(event, event.character);
        }

        switch (event.type) {
            case touchDown:
                return touchDown(event, event.whereIntersected, event.pointer, event.button);
            case touchUp:
                touchUp(event, event.whereIntersected, event.pointer, event.button);
                return true;
            case touchDragged:
                touchDragged(event, event.whereIntersected, event.pointer);
                return true;
            case mouseMoved:
                return mouseMoved(event, event.whereIntersected);
            case scrolled:
                return scrolled(event, event.whereIntersected, event.scrollAmount);
            case enter:
                enter(event, event.whereIntersected, event.pointer, event.relatedActor);
                return false;
            case exit:
                exit(event, event.whereIntersected, event.pointer, event.relatedActor);
                return false;
        }
        return false;
    }

    /**
     * Called when a mouse button or a finger touch goes down on the actor.
     * If true is returned, this listener will receive all
     * touchDragged and touchUp events, even those not over this actor, until touchUp is received.
     * Also when true is returned, the event is {@link Event#handle() handled}.
     *
     * @see InputEvent
     */
    public boolean touchDown(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
        return false;
    }

    /**
     * Called when a mouse button or a finger touch goes up anywhere, but only if touchDown previously returned true for the mouse
     * button or touch. The touchUp event is always {@link Event#handle() handled}.
     *
     * @see InputEvent
     */
    public void touchUp(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
    }

    /**
     * Called when a mouse button or a finger touch is moved anywhere, but only if touchDown previously returned true for the mouse
     * button or touch. The touchDragged event is always {@link Event#handle() handled}.
     *
     * @see InputEvent
     */
    public void touchDragged(CustomInputEvent event, Vector3 touchPoint, int pointer) {
    }

    /**
     * Called any time the mouse is moved when a button is not down. This event only occurs on the desktop. When true is returned,
     * the event is {@link Event#handle() handled}.
     *
     * @see InputEvent
     */
    public boolean mouseMoved(CustomInputEvent event, Vector3 touchPoint) {
        return false;
    }

    /**
     * Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this event occurs even when no
     * mouse buttons are pressed (pointer will be -1).
     *
     * @see InputEvent
     */
    public void enter(CustomInputEvent event, Vector3 touchPoint, int pointer, SceneNode3d fromActor) {
    }

    /**
     * Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this event occurs even when no
     * mouse buttons are pressed (pointer will be -1).
     *
     * @see InputEvent
     */
    public void exit(CustomInputEvent event, Vector3 touchPoint, int pointer, SceneNode3d toActor) {
    }

    /** Called when the mouse wheel has been scrolled. When true is returned, the event is {@link Event#handle() handled}. */
    public boolean scrolled(CustomInputEvent event, Vector3 touchPoint, int amount) {
        return false;
    }

    /** Called when a key goes down. When true is returned, the event is {@link Event#handle() handled}. */
    public boolean keyDown(CustomInputEvent event, int keycode) {
        return false;
    }

    /** Called when a key goes up. When true is returned, the event is {@link Event#handle() handled}. */
    public boolean keyUp(CustomInputEvent event, int keycode) {
        return false;
    }

    /** Called when a key is typed. When true is returned, the event is {@link Event#handle() handled}. */
    public boolean keyTyped(CustomInputEvent event, char character) {
        return false;
    }
}
