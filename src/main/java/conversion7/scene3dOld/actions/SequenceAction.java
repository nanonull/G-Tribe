package conversion7.scene3dOld.actions;

import com.badlogic.gdx.utils.Pool;
import conversion7.scene3dOld.Action3d;

/**
 * Executes a number of actions one at a time.
 *
 * @author Nathan Sweet
 */
public class SequenceAction extends ParallelAction {
    private int index;

    public SequenceAction() {
    }

    public SequenceAction(Action3d action1) {
        addAction(action1);
    }

    public SequenceAction(Action3d action1, Action3d action2) {
        addAction(action1);
        addAction(action2);
    }

    public SequenceAction(Action3d action1, Action3d action2, Action3d action3) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
    }

    public SequenceAction(Action3d action1, Action3d action2, Action3d action3, Action3d action4) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
    }

    public SequenceAction(Action3d action1, Action3d action2, Action3d action3, Action3d action4, Action3d action5) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
        addAction(action5);
    }

    @Override
    public boolean act(float delta) {
        if (index >= actions.size) return true;
        Pool<Action3d> pool = getPool();
        setPool(null); // Ensure this action can't be returned to the pool while executings.
        try {
            if (actions.get(index).act(delta)) {
                if (actor3d == null) return true; // This action was removed.
                index++;
                if (index >= actions.size) return true;
            }
            return false;
        } finally {
            setPool(pool);
        }
    }

    @Override
    public void restart() {
        super.restart();
        index = 0;
    }
}