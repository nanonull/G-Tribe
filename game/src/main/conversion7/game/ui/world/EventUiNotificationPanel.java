package conversion7.game.ui.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class EventUiNotificationPanel extends VBox {

    private static final Color BCOLOR = ClientUi.PANEL_COLOR;

    static {
    }


    public EventUiNotificationPanel() {
        setBackground(new TextureRegionColoredDrawable(BCOLOR, Assets.pixel));
        pad(ClientUi.DOUBLE_SPACING);
    }

    public void showFor(String msg) {
        clear();
//        addSmallCloseButton();

        addLabel(".", Assets.labelStyle14orange2).center().grow().pad(4);
        Label actor = addLabel(msg.toUpperCase(), Assets.labelStyle48white)
                .center().pad(ClientUi.DOUBLE_SPACING).getActor();
        addLabel(".", Assets.labelStyle14orange2).center().grow().pad(4);

        pack();
        setX(Gdx.graphics.getWidth() / 2 - getWidth() / 2);
        setY(Gdx.graphics.getHeight() * 0.75f + getHeight() / 2);
        show();
    }


}
