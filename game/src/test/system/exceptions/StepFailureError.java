package system.exceptions;

public class StepFailureError extends Error {

    public StepFailureError(String message) {
        super(message);
    }

    public StepFailureError(Throwable cause) {
        super(cause);
    }

    public StepFailureError(String message, Throwable cause) {
        super(message, cause);
    }

}
