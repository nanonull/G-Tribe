package conversion7.game.ui.battle;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.ClientCore;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle.Battle;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.dialogs.AbstractInfoDialog;

public class BattleEndWindow extends AbstractInfoDialog {

    private Battle battle;

    public BattleEndWindow(Stage stageGUI) {
        super(stageGUI, "Battle finished!", Assets.uiSkin, Direction.down);
        defaults().pad(ClientUi.DOUBLE_SPACING);

        setPosition(0, GdxgConstants.SCREEN_HEIGHT_IN_PX);
        setWidth(256);
        setHeight(150);

        row().height(50);

        TextButton closeButton = new TextButton("Go back to world!", Assets.uiSkin);
        add(closeButton);
        closeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                ClientCore.core.returnToWorld();
            }
        });

    }

    public void show(Battle battle) {
        this.battle = battle;
        setDescription("Winner: " + battle.aliveTeamSides.get(0).getTeam().getName());
        show();
    }

}
