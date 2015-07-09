package conversion7.engine.custom2d.table;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class TableWithScrollPane extends DefaultTable {

    private ScrollPane scrollPane = new ScrollPane(this, Assets.uiSkin);

    public TableWithScrollPane() {
        super();
        scrollPane = new ScrollPane(this, Assets.uiSkin);
        scrollPane.setFadeScrollBars(false);
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public boolean willBeScrolledHorizontally() {
        return getPrefWidth() > scrollPane.getWidth();
    }

    public void addSpaceForHorizontalScroll() {
        if (willBeScrolledHorizontally()) {
            row().height(ClientUi.SCROLL_LINE_SIZE);
            add();
        }
    }
}
