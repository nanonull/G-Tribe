package conversion7.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import org.slf4j.Logger;

public class UiLogger {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static Stage stage;
    static Array<UiLogLabel> labels = new Array<>();

    public static void init(Stage s) {
        stage = s;
    }


    public static void addInfoLabel(String text) {
        startLogLabel(new UiLogLabel(text, Assets.labelStyle14orange));
    }

    public static void addErrorLabel(String text) {
        startLogLabel(new UiLogLabel(text, Assets.labelStyle14red, true));
    }

    private static void startLogLabel(UiLogLabel label) {
        LOG.info("startLogLabel: " + label.getText());
        shiftExisting();
        stage.addActor(label);
        labels.add(label);
        if (label.isError()) {
            label.toFront();
        } else {
            label.toBack();
        }
        label.setPosition(5, Gdx.graphics.getWidth() - label.getHeight() - 5);
        label.setColor(1, 1, 0, 1);
        label.addAction(Actions.alpha(0f, 9));
        label.addAction(Actions.moveBy(0, -Gdx.graphics.getHeight(), 10));
    }

    private static void shiftExisting() {
        float shift;
        for (UiLogLabel label : labels) {
            shift = label.getHeight();
            label.setY(label.getY() - shift);
        }
    }

}
