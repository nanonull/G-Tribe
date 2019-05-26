package conversion7.engine.customscene.input;


/**
 * Low level interface for receiving events. Typically there is a listener class for each specific event class.
 */
public interface CustomInputListenerAbstract {
    /**
     * Try to handle the given event, if it is applicable.
     *
     * @return true if the event should be considered by scene.
     */
    public boolean handle(CustomEvent event);
}