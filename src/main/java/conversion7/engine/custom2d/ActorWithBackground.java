package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class ActorWithBackground extends WidgetGroup {

    private Actor face;
    private Image background;
    private boolean useFaceSizeForBackgroundLayout;

    public ActorWithBackground(Actor face, Color backgroundColor) {
        this(face);
        setBackgroundColor(backgroundColor);
    }

    public ActorWithBackground(Actor face) {
        this.face = face;
        background = new Image(Assets.pixelWhite);
        addActor(background);
        addActor(face);
    }

    public void setBackgroundColor(Color backgroundColor) {
        background.setColor(backgroundColor);
    }

    @Override
    public void layout() {
        face.setPosition(ClientUi.SPACING, ClientUi.SPACING);
        if (useFaceSizeForBackgroundLayout) {
            background.setWidth(face.getWidth() + ClientUi.DOUBLE_SPACING);
            background.setHeight(face.getHeight() + ClientUi.DOUBLE_SPACING);
        } else {
            background.setWidth(getWidth());
            background.setHeight(getHeight());
            face.setWidth(getWidth() - ClientUi.DOUBLE_SPACING);
            face.setHeight(getHeight() - ClientUi.DOUBLE_SPACING);
        }
    }

    public void setUseFaceSizeForBackgroundLayout(boolean useFaceSizeForBackgroundLayout) {
        this.useFaceSizeForBackgroundLayout = useFaceSizeForBackgroundLayout;
    }
}
