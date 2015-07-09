package conversion7.scene3dOld.actions;

/**
 * Multiplies the delta of an action.
 *
 * @author Nathan Sweet
 */
public class TimeScaleAction extends DelegateAction {
    private float scale;

    @Override
    protected boolean delegate(float delta) {
        if (action == null) return true;
        return action.act(delta * scale);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}