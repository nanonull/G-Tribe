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
    private float padding;

    public ActorWithBackground(Actor face, Color backgroundColor) {
        this(face);
        setBackgroundColor(backgroundColor);
    }

    public ActorWithBackground(Actor face) {
        this.face = face;
        background = new Image(Assets.pixel);
        addActor(background);
        addActor(face);
    }

    public void setBackgroundColor(Color backgroundColor) {
        background.setColor(backgroundColor);
    }

    public void setUseFaceSizeForBackgroundLayout(boolean useFaceSizeForBackgroundLayout) {
        this.useFaceSizeForBackgroundLayout = useFaceSizeForBackgroundLayout;
    }

    public ActorWithBackground setPadding(float padding) {
        this.padding = padding;
        return this;
    }

    @Override
    public void layout() {
        face.setPosition(padding, padding);
        float doublePad = padding * 2;
        if (useFaceSizeForBackgroundLayout) {
            background.setWidth(face.getWidth() + doublePad);
            background.setHeight(face.getHeight() + doublePad);
        } else {
            background.setWidth(getWidth());
            background.setHeight(getHeight());
            face.setWidth(getWidth() - doublePad);
            face.setHeight(getHeight() - doublePad);
        }
    }
}
