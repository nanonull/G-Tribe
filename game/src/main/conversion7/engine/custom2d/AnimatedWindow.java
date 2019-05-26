package conversion7.engine.custom2d;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.Gdxg;
import conversion7.engine.tween.ActorAccessor;
import conversion7.engine.utils.TimeFI;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public abstract class AnimatedWindow extends CustomWindow {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final int START_SIZE = 20;

    private int direction;
    private boolean displayed = false;
    public boolean inProgress = false;
    private TimeFI animationLength;

    // target dimensions for hide:
    private float minX;
    private float minY;
    private float minWidth;
    private float minHeight;

    // target dimensions for show:
    private float finalX;
    private float finalY;
    private float finalWidth;
    private float finalHeight;

    boolean showAnimationEnabled = true;
    private boolean boundsUpdated = false;
    private int scheduledAction = Scheduled.none;
    public Tween tween;

    public AnimatedWindow(Stage stage, String title, Skin skin, int direction) {
        super(stage, title, skin);
        this.direction = direction;
        animationLength = new TimeFI(250);
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public float getFinalX() {
        return finalX;
    }

    public float getFinalY() {
        return finalY;
    }

    public float getFinalWidth() {
        return finalWidth;
    }

    public float getFinalHeight() {
        return finalHeight;
    }

    public boolean isShowing() {
        return inProgress && !displayed;
    }

    public boolean isHiding() {
        return inProgress && displayed;
    }

    public void setAnimationLength(long ms) {
        animationLength = new TimeFI(ms);
    }

    public void setShowAnimationEnabled(boolean showAnimationEnabled) {
        this.showAnimationEnabled = showAnimationEnabled;
    }

    /** Must be called after window position and size update */
    public void updateAnimationBounds() {
        finalX = getX();
        finalY = getY();
        finalWidth = getWidth();
        finalHeight = getHeight();

        switch (direction) {
            case Direction.up:
                minX = finalX;
                minY = finalY + finalHeight - START_SIZE;
                minWidth = finalWidth;
                minHeight = START_SIZE;
                break;
            case Direction.down:
                minX = finalX;
                minY = finalY;
                minWidth = finalWidth;
                minHeight = START_SIZE;
                break;
            case Direction.left:
                minX = finalX;
                minY = finalY;
                minWidth = START_SIZE;
                minHeight = finalHeight;
                break;
            case Direction.right:
                minX = finalX + finalWidth - START_SIZE;
                minY = finalY;
                minWidth = START_SIZE;
                minHeight = finalHeight;
                break;
            default:
                throw new GdxRuntimeException("not implemented yet");
        }

        boundsUpdated = true;
    }

    /** Reset position after hide, because updateAnimationBounds could be called on next show */
    private void resetPositionAfterHide() {
        switch (direction) {
            case Direction.up:
                setY(finalY);
                break;
            case Direction.down:
                setY(finalY);
                break;
            case Direction.left:
                setX(finalX);
            case Direction.right:
                setX(finalX);
                break;
            default:
                throw new GdxRuntimeException("not implemented yet");
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void turn() {
        if (inProgress) return;

        if (displayed) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public void hide() {
        if (inProgress) {
            scheduledAction = Scheduled.hide;
            return;
        }
        inProgress = true;
        onHide();

        tween = Tween.to(this, ActorAccessor.POSITION_SIZE, animationLength.getFloatSeconds())
                .target(minX, minY, minWidth, minHeight)
                .setCallback(new AnimationCompletedCallback(this, false))
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(Gdxg.tweenManager);
    }

    @Override
    public void show() {
        if (inProgress) {
            scheduledAction = Scheduled.show;
            return;
        }

        inProgress = true;
        getLinkedStage().addActor(this);
        onShow();

        if (!boundsUpdated) {
            throw new GdxRuntimeException("Window should have initialized animation bounds!\n" +
                    "Call #updateAnimationBounds method after object dimensions calculated.");
        }
        setBeforeShowPositionAndSize();

        if (showAnimationEnabled) {
            tween = Tween.to(this, ActorAccessor.POSITION_SIZE, animationLength.getFloatSeconds())
                    .target(finalX, finalY, finalWidth, finalHeight)
                    .setCallback(new AnimationCompletedCallback(this, true))
                    .setCallbackTriggers(TweenCallback.COMPLETE)
                    .start(Gdxg.tweenManager);

        } else {
            throw new GdxRuntimeException("TODO implement");
//            setBounds(finalX, finalY, finalWidth, finalHeight);
//            completeAnimation(this, true);
        }

    }

    public void onHide() {

    }

    public void onShow() {

    }

    private void setBeforeShowPositionAndSize() {
        switch (direction) {
            case Direction.up:
                setHeight(START_SIZE);
                setY(finalY);
                break;
            case Direction.down:
                setHeight(START_SIZE);
                setY(finalY + finalHeight - START_SIZE);
                break;
            case Direction.left:
                setWidth(START_SIZE);
                setX(finalX + finalWidth - START_SIZE);
                break;
            case Direction.right:
                setWidth(START_SIZE);
                setX(finalX);
                break;
            default:
                throw new GdxRuntimeException("not implemented yet");
        }
    }

    public void onShowCompleted() {

    }

    public void onHideCompleted() {

    }

    public void onAnimationCompleted() {

    }

    /** Direction in which Window will be expanded */
    public class Direction {
        static public final int up = 1;
        static public final int down = 2;
        static public final int left = 3;
        static public final int right = 4;
    }

    class Scheduled {
        static public final int none = 1;
        static public final int show = 2;
        static public final int hide = 3;
    }

    class AnimationCompletedCallback implements TweenCallback {

        private AnimatedWindow window;
        private boolean willBeDisplayed;

        public AnimationCompletedCallback(AnimatedWindow window, boolean willBeDisplayed) {
            this.window = window;
            this.willBeDisplayed = willBeDisplayed;
        }

        @Override
        public void onEvent(int i, BaseTween<?> baseTween) {
            if (LOG.isDebugEnabled()) LOG.debug("AnimationCompletedCallback, willBeDisplayed:" + willBeDisplayed);
            window.displayed = willBeDisplayed;
            if (!window.displayed) {
                remove();
                resetPositionAfterHide();
            }
            window.onAnimationCompleted();
            if (window.displayed) {
                window.onShowCompleted();
            } else {
                window.onHideCompleted();
            }

            switch (window.scheduledAction) {
                case Scheduled.show:
                    window.scheduledAction = Scheduled.none;
                    window.inProgress = false;
                    if (!window.displayed) {
                        window.show();
                    }
                    return;
                case Scheduled.hide:
                    window.scheduledAction = Scheduled.none;
                    window.inProgress = false;
                    if (window.displayed) {
                        window.hide();
                    }
                    return;
            }
            window.inProgress = false;
        }
    }


}
