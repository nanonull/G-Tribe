package conversion7.engine.custom2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class ButtonWithActor extends WidgetGroup {

    private boolean keepActorSize;
    public Actor face;
    private TextButton textButton;

    public ButtonWithActor(Actor face) {
        this(face, false);
    }

    public ButtonWithActor(Actor face, boolean keepActorSize) {
        this.face = face;
        this.keepActorSize = keepActorSize;

        textButton = new TextButton("", Assets.uiSkin);
        addActor(textButton);
        addActor(face);
        face.setTouchable(Touchable.disabled);
    }

    @Override
    public void layout() {
        textButton.setWidth(getWidth());
        textButton.setHeight(getHeight());

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
