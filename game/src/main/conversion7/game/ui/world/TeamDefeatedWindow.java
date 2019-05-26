package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;

public class TeamDefeatedWindow extends AnimatedWindow {

    private static final Logger LOG = Utils.getLoggerForClass();

    public TeamDefeatedWindow(final Stage stage) {
        super(stage, "Team Defeated!", Assets.uiSkin, Direction.down);
        defaults().pad(ClientUi.DOUBLE_SPACING);

        row();
        TextButton closeButton = new TextButton("Continue", Assets.uiSkin);
        add(closeButton);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

    }

    @Override
    public void show() {
        pack();
        setPosition(ClientUi.SPACING, GdxgConstants.SCREEN_HEIGHT_IN_PX - getHeight() - ClientUi.SPACING);
        updateAnimationBounds();
        super.show();
    }
}
