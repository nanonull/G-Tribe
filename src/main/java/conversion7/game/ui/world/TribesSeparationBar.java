package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import conversion7.engine.custom2d.CustomWindow;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.ui.ClientUi;

public class TribesSeparationBar extends CustomWindow {
    Label tribesSeparationLabel;

    public TribesSeparationBar(Stage stage, Skin skin) {
        super(stage, TribesSeparationBar.class.getSimpleName(), skin);
        tribesSeparationLabel = new Label("", Assets.labelStyle14green);
        add(tribesSeparationLabel);
    }

    public void updateTribeSeparation(int totalTribesSeparationValue) {
        tribesSeparationLabel.setText("Tribes separation value: " + totalTribesSeparationValue + "/" + World.TRIBE_SEPARATION_VALUE_MAX);
        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX / 2 - getWidth() / 2,
                GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.DOUBLE_SPACING * 2 - getHeight());
    }
}
