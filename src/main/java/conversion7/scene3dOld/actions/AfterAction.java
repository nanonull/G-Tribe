package conversion7.scene3dOld.actions;

import com.badlogic.gdx.utils.Array;
import conversion7.scene3dOld.Action3d;
import conversion7.scene3dOld.Actor3d;

/**
 * Executes an action only after all other actions on the actor at the time this action was added have finished.
 *
 * @author Nathan Sweet
 */
public class AfterAction extends DelegateAction {
    private Array<Action3d> waitForActions = new Array<Action3d>(false, 4);

    @Override
    public void setActor3d(Actor3d actor3d) {
        if (actor3d != null) waitForActions.addAll(actor3d.getActions3d());
        super.setActor3d(actor3d);
    }

    @Override
    public void restart() {
        super.restart();
        waitForActions.clear();
    }

    @Override
    protected boolean delegate(float delta) {
        Array<Action3d> currentActions = actor3d.getActions3d();
        if (currentActions.size == 1) waitForActions.clear();
        for (int i = waitForActions.size - 1; i >= 0; i--) {
            Action3d action = waitForActions.get(i);
            int index = currentActions.indexOf(action, true);
            if (index == -1) waitForActions.removeIndex(i);
        }
        if (waitForActions.size > 0) return false;
        return action.act(delta);
    }
}