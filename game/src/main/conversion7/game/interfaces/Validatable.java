package conversion7.game.interfaces;

public interface Validatable {

    /** Mark object's state as 'work-in-progress' - NOT VALID for integration with another systems */
    public void invalidate();

    /** Complete all works on object, make it VALID for integration with another systems */
    public void validate();

    default void refresh() {
        invalidate();
        validate();
    }
}
