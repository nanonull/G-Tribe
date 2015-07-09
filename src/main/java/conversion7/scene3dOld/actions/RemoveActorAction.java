package conversion7.scene3dOld.actions;

import conversion7.scene3dOld.Action3d;
import conversion7.scene3dOld.Actor3d;


/**
 * Removes an actor from the stage.
 *
 * @author Nathan Sweet
 */
public class RemoveActorAction extends Action3d {
    private Actor3d removeActor;
    private boolean removed;

    @Override
    public boolean act(float delta) {
        if (!removed) {
            removed = true;
            (removeActor != null ? removeActor : actor3d).remove();
        }
        return true;
    }

    @Override
    public void restart() {
        removed = false;
    }

    @Override
    public void reset() {
        super.reset();
        removeActor = null;
    }

    public Actor3d getRemoveActor() {
        return removeActor;
    }

    /** Sets the actor to remove. If null (the default), the {@link #getActor() actor} will be used. */
    public void setRemoveActor(Actor3d removeActor) {
        this.removeActor = removeActor;
    }
}