package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;

public class WorldHintPanel extends Panel {

    Label actionNameLabel;
    Label actionHintLabel;

    public WorldHintPanel(Stage stageGUI) {
        stageGUI.addActor(this);

        defaults().pad(2);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        actionNameLabel = new Label("-", Assets.labelStyle48white);
        add(actionNameLabel);
        actionNameLabel.setFontScale(0.8f);
        row();

        actionHintLabel = new Label("-", Assets.labelStyle48white);
        add(actionHintLabel);
        actionHintLabel.setFontScale(0.4f);
    }

    public void showHint(String actionWorldHint) {
        showHint(actionWorldHint, "");
    }

    public void showHint(String name, String hint) {
        actionNameLabel.setText(name);
        actionHintLabel.setText(hint);

        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX / 2 - getWidth() / 2,
                ClientUi.DOUBLE_SPACING + WorldMainWindow.HEIGHT);
        show();
    }
}
