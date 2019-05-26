package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class ScrollPaneExt<T extends Actor> extends ScrollPane {

    public T widget;

    public ScrollPaneExt(T widget, Skin skin) {
        super(widget, skin);
        widget = (T) getWidget();
        setFadeScrollBars(false);
    }

    public boolean willBeScrolledHorizontally() {
        return getPrefWidth() > getWidth();
    }
}
