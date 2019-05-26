package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.main_panel.WorldMainWindow;

// TODO complete
public class BaseDescriptionPanel extends VBox {

    Label bodyLabel;

    public BaseDescriptionPanel() {
        defaults().pad(2);
        setBackground(new TextureRegionColoredDrawable(new Color(0, 0.24f, 0, 0.75f), Assets.pixel));

        bodyLabel = new Label("null", Assets.labelStyle14orange);
        add(bodyLabel);

        invalidate();
    }

    public void setBody(String text) {
        bodyLabel.setText(text);

        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX / 2 - getWidth() / 2,
                ClientUi.DOUBLE_SPACING + WorldMainWindow.HEIGHT);

        show();
    }
}
