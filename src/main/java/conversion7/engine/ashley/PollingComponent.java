package conversion7.engine.ashley;

import com.badlogic.ashley.core.Component;

import java.util.concurrent.Callable;

/**
 * Non-blocking polling
 */
public class PollingComponent extends Component {

    private final float pollingIntervalSeconds;
    private Callable<Status> callable;
    public float timePassed;
    private Status status;

    public PollingComponent(int pollingIntervalMillis, Callable<Status> callable) {
        this.pollingIntervalSeconds = (float) pollingIntervalMillis / 1000.0F;
        this.callable = callable;
        status = Status.ACTIVE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + status;
    }

    public float getPollingIntervalSeconds() {
        return pollingIntervalSeconds;
    }


    public Callable<Status> getCallable() {
        return callable;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }


    public enum Status {
        ACTIVE, COMPLETED, ERROR
    }

}
