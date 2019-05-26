package system.break_point_steps_core;

/** Use when polling should write to target thread OR async read is not applicable. */
public abstract class PollingBreakPointStep {

    private boolean completed;

    public boolean isCompleted() {
        return completed;
    }

    public void stepCompleted() {
        completed = true;
    }

    public abstract void run();
}
