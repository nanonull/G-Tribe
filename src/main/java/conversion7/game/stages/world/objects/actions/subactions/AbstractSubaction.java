package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.interfaces.Cancelable;
import conversion7.game.interfaces.ExecutableVoid;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;

public abstract class AbstractSubaction implements ExecutableVoid, Cancelable {

    protected AbstractAreaObjectAction parentAction;

    public AbstractSubaction(AbstractAreaObjectAction action) {
        this.parentAction = action;
    }

    public AbstractAreaObjectAction getParentAction() {
        return parentAction;
    }

    @Override
    public void cancel() {
        parentAction.cancel();
    }

}
