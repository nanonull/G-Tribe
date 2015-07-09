package conversion7.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class UiLogLabel extends Label {

    private boolean error;

    public UiLogLabel(String text, LabelStyle labelStyle) {
        this(text, labelStyle, false);
    }

    public UiLogLabel(String text, LabelStyle labelStyle, boolean error) {
        super(error ? "[ERROR] " + text : text, labelStyle);
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getY() == 0) {
            remove();
            UiLogger.labels.removeValue(this, false);
        }
    }

}
