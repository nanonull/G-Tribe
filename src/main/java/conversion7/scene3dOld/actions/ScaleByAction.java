package conversion7.scene3dOld.actions;


/**
 * Scales an actor's scale to a relative size.
 *
 * @author Nathan Sweet
 */
public class ScaleByAction extends RelativeTemporalAction {
    private float amountX, amountY, amountZ;

    @Override
    protected void updateRelative(float percentDelta) {
        actor3d.scale(amountX * percentDelta, amountY * percentDelta, amountZ * percentDelta);
    }

    public void setAmount(float x, float y, float z) {
        amountX = x;
        amountY = y;
        amountZ = z;
    }

    public void setAmount(float scale) {
        amountX = scale;
        amountY = scale;
        amountZ = scale;
    }

    public float getAmountX() {
        return amountX;
    }

    public void setAmountX(float x) {
        this.amountX = x;
    }

    public float getAmountY() {
        return amountY;
    }

    public void setAmountY(float y) {
        this.amountY = y;
    }

    public float getAmountZ() {
        return amountZ;
    }

    public void setAmountZ(float z) {
        this.amountZ = z;
    }

}