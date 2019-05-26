package conversion7.game;

public class GameError extends RuntimeException {

    private Object objectWithError;

    public GameError (String message) {
        super(message);
    }

    public GameError (Throwable t) {
        super(t);
    }

    public GameError (String message, Throwable t) {
        super(message, t);
    }

    public Object getObjectWithError() {
        return objectWithError;
    }

    /**This object additionally logged in conversion7.engine.ClientCore#flushErrors()*/
    public GameError setObjectWithError(Object objectWithError) {
        this.objectWithError = objectWithError;
        return this;
    }
}