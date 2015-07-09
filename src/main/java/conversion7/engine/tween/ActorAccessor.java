package conversion7.engine.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorAccessor implements TweenAccessor<Actor> {
    public static final int POSITION_XY = 1;
    public static final int SCALE_XY = 2;
    public static final int WIDTH_HEIGHT = 3;
    public static final int POSITION_SIZE = 4;

    @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;

            case SCALE_XY:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;

            case WIDTH_HEIGHT:
                returnValues[0] = target.getWidth();
                returnValues[1] = target.getHeight();
                return 2;

            case POSITION_SIZE:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                returnValues[2] = target.getWidth();
                returnValues[3] = target.getHeight();
                return 4;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_XY:
                target.setPosition(newValues[0], newValues[1]);
                break;

            case SCALE_XY:
                target.setScale(newValues[0], newValues[1]);
                break;

            case WIDTH_HEIGHT:
                target.setSize(newValues[0], newValues[1]);
                break;

            case POSITION_SIZE:
                target.setBounds(newValues[0],
                        newValues[1],
                        newValues[2],
                        newValues[3]);
                break;

            default:
                assert false;
        }
    }
}
