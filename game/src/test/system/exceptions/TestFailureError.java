package system.exceptions;

public class TestFailureError extends Error {

    public TestFailureError(String message) {
        super(message);
    }

    public TestFailureError(Throwable cause) {
        super(cause);
    }

    public TestFailureError(String message, Throwable cause) {
        super(message, cause);
    }

}
