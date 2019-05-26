package conversion7.game.stages.world.actions;

public abstract class AbstractSubaction {

    protected AbstractAction parentAbstractAction;

    public AbstractSubaction(AbstractAction action) {
        this.parentAbstractAction = action;
    }

    public AbstractAction getParentAction() {
        return parentAbstractAction;
    }

    public abstract void execute();

    public void cancel() {
        parentAbstractAction.cancel();
    }

}
