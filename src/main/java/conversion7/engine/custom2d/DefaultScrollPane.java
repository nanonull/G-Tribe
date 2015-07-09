package conversion7.engine.custom2d;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DefaultScrollPane extends ScrollPane {


    public DefaultScrollPane(Actor widget) {
        super(widget);
        applyDefaults(this);

    }

    public DefaultScrollPane(Actor widget, Skin skin) {
        super(widget, skin);
        applyDefaults(this);
    }

    private static void applyDefaults(ScrollPane scrollPane) {
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
    }
}
