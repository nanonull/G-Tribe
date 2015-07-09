package conversion7.engine.customscene.input;


/**
 * Low level interface for receiving events. Typically there is a listener class for each specific event class.
 *
 * @see InputListener
 * @see InputEvent
 */
public interface CustomInputListenerAbstract {
    /**
     * Try to handle the given event, if it is applicable.
     *
     * @return true if the event should be considered {@link ClickedListener#handle() handled} by scene2d.
     */
    public boolean handle(CustomEvent event);
}