package system.exceptions;

public class PossibleNotImportantTestFailure extends RuntimeException {

    public PossibleNotImportantTestFailure(String message) {
        super(message);
    }

    public PossibleNotImportantTestFailure(Throwable cause) {
        super(cause);
    }

    public PossibleNotImportantTestFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
