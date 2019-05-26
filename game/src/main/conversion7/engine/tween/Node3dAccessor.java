package conversion7.engine.tween;

import aurelienribon.tweenengine.TweenAccessor;
import conversion7.engine.customscene.SceneNode3d;

public class Node3dAccessor implements TweenAccessor<SceneNode3d> {

    public static final int POSITION_XYZ = 1;

    @Override
    public int getValues(SceneNode3d target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XYZ:
                returnValues[0] = target.localPosition.x;
                returnValues[1] = target.localPosition.y;
                returnValues[2] = target.localPosition.z;
                return 3;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(SceneNode3d target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_XYZ:
                target.setPosition(newValues[0], newValues[1], newValues[2]);
                break;

            default:
                assert false;
        }
    }
}
