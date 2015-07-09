package conversion7.scene3dOld;


/**
 * Low level interface for receiving events. Typically there is a listener class for each specific event class.
 *
 * @see InputListener
 * @see InputEvent
 */
public interface Event3dListener {
    /**
     * Try to handle the given event, if it is applicable.
     *
     * @return true if the event should be considered {@link ClickedListener#handle() handled} by scene2d.
     */
    public boolean handle(Event3d event);
}