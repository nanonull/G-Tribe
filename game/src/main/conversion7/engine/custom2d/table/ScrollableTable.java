package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class ScrollableTable<T extends Table> extends ScrollPaneExt<T> {

    public T table;

    public ScrollableTable(T widget, Skin skin) {
        super(widget, skin);
        table = widget;
    }

    public void addSpaceForHorizontalScroll() {
        if (willBeScrolledHorizontally()) {
            table.row().height(ClientUi.SCROLL_LINE_SIZE);
            table.add();
        }
    }
}
