package conversion7.engine.custom2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class ButtonWithActor extends WidgetGroup {

    private boolean keepActorSize;
    private Actor face;
    private TextButton background;

    public ButtonWithActor() {
        this(null);
    }

    public ButtonWithActor(Actor face) {
        this(face, false);
    }

    public ButtonWithActor(Actor face, boolean keepActorSize) {
        this.keepActorSize = keepActorSize;
        background = new TextButton("", Assets.uiSkin);
        addActor(background);

        setFace(face);
    }

    public TextButton getBackground() {
        return background;
    }

    public void setFace(Actor newFace) {
        if (newFace != null) {
            if (this.face != null) {
                this.face.remove();
            }
            this.face = newFace;
            addActor(newFace);
            newFace.setTouchable(Touchable.disabled);
            invalidate();
        }
    }

    public void setKeepActorSize(boolean keepActorSize) {
        this.keepActorSize = keepActorSize;
    }

    @Override
    public void layout() {
        background.setWidth(getWidth());
        background.setHeight(getHeight());

        if (face != null) {
            if (!keepActorSize) {
                face.setPosition(ClientUi.SPACING, ClientUi.SPACING);
                face.setWidth(getWidth() - ClientUi.DOUBLE_SPACING);
                face.setHeight(getHeight() - ClientUi.DOUBLE_SPACING);
            } else {
                face.setPosition(getWidth() / 2 - face.getWidth() / 2,
                        getHeight() / 2 - face.getHeight() / 2);
            }
        }
    }
}
