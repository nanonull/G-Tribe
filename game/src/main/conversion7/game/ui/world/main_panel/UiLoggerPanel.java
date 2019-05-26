package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;
import conversion7.engine.AudioPlayer;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.DefaultTable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.engine.custom2d.table.ScrollableTable;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;

public class UiLoggerPanel extends Panel {

    public static final int HEIGHT = GdxgConstants.SCREEN_HEIGHT_IN_PX / 2;
    public static final float POS_X = 0;
    public static final float POS_Y = GdxgConstants.SCREEN_HEIGHT_IN_PX - 50;
    private static UiLoggerPanel instance;
    private static final float WIDTH = 200;
    private final ScrollableTable<Panel> scrollableTable;

    public UiLoggerPanel() {
        DefaultTable.applyDefaults(this);
        scrollableTable = new ScrollableTable<>(new VBox(), Assets.uiSkin);
        add(scrollableTable).grow().left();
        scrollableTable.setScrollingDisabled(false, false);
    }

    public static UiLoggerPanel get() {
        if (instance == null) {
            instance = new UiLoggerPanel();
        }
        return instance;
    }

    public void addLabel(StringBuilder label) {
        Label lbl = new Label(label, Assets.labelStyle12_i_lightGreen);
        lbl.setWrap(true);
        lbl.setWidth(WIDTH);
        if (scrollableTable.table.getChildren().size > 1000) {
            scrollableTable.table.clearChildren();
        }
        scrollableTable.table.add(lbl).padRight(ClientUi.SMALL_PROGRESS_BAR_WIDTH)
                .padTop(2).padBottom(2);
        scrollableTable.layout();
        scrollableTable.setScrollPercentY(100);

        AudioPlayer.play("fx\\CLAV10.mp3").setVolume(0.1f);
    }
}
