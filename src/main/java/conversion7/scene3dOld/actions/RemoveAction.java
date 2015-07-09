package conversion7.scene3dOld.actions;

import conversion7.scene3dOld.Action3d;
import conversion7.scene3dOld.Actor3d;

/**
 * Removes an action from an actor.
 *
 * @author Nathan Sweet
 */
public class RemoveAction extends Action3d {
    private Actor3d targetActor;
    private Action3d action;

    @Override
    public boolean act(float delta) {
        (targetActor != null ? targetActor : actor3d).removeAction3d(action);
        return true;
    }

    public Actor3d getTargetActor() {
        return targetActor;
    }

    /** Sets the actor to remove an action from. If null (the default), the {@link #getActor() actor} will be used. */
    public void setTargetActor(Actor3d actor) {
        this.targetActor = actor;
    }

    public Action3d getAction() {
        return action;
    }

    public void setAction(Action3d action) {
        this.action = action;
    }

    @Override
    public void reset() {
        super.reset();
        targetActor = null;
        action = null;
    }
}