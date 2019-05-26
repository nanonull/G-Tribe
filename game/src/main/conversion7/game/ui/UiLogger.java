package conversion7.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import org.slf4j.Logger;

public class UiLogger {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static Stage stage;
    static Array<UiLogLabel> labels = new Array<>();
    private static final LabelStyle GAME_LABEL_STYLE1;
    private static final LabelStyle GAME_LABEL_STYLE2;

    static {
        GAME_LABEL_STYLE1 = new LabelStyle(Assets.font28, Color.BLACK);
        GAME_LABEL_STYLE1.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixel);
        GAME_LABEL_STYLE2 = new LabelStyle(Assets.font18, Color.BLACK);
        GAME_LABEL_STYLE2.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixel);
    }

    public static void init(Stage s) {
        stage = s;
    }


    public static void addImportantGameInfoLabel(String text) {
        startLogLabel(new UiLogLabel(" ! " + text.toUpperCase(), GAME_LABEL_STYLE1));
    }

    public static void addGameInfoLabel(String text) {
        startLogLabel(new UiLogLabel(text.toUpperCase(), GAME_LABEL_STYLE2));
    }

    public static void addInfoLabel(String text) {
        startLogLabel(new UiLogLabel(text, Assets.labelStyle14orange));
    }

    public static void addErrorLabel(String text) {
        startLogLabel(new UiLogLabel(text, Assets.labelStyle14red, true));
    }

    private static void startLogLabel(Label label) {
        Gdxg.clientUi.getWorldMainWindow().uiLoggerPanel.addLabel(label.getText());
    }

    private static void shiftExisting() {
        float shift;
        for (UiLogLabel label : labels) {
            shift = label.getHeight();
            label.setY(label.getY() - shift);
        }
    }

}
