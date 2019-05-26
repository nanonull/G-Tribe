package conversion7.game.stages.world.actions;

public abstract class AbstractAction {
    protected boolean cancelled;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getShortName() {
        return getClass().getSimpleName().substring(0, 3);
    }

    public void cancel() {
        cancelled = true;
    }
}
