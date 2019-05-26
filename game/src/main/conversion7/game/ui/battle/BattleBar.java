package conversion7.game.ui.battle;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.ui.ClientUi;

@Deprecated
public class BattleBar extends AnimatedWindow {
    private Battle battle;

    public BattleBar(Stage stage, Skin skin) {
        super(stage, "BattleBar", skin, Direction.right);
        defaults().pad(ClientUi.SPACING);

        registerButtons();
    }

    private void registerButtons() {
        TextButton button;

        button = new TextButton("Manage Round", Assets.uiSkin);
        add(button).expandY().fill();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BattleWindowManageArmyForRound manageArmyForRound = Gdxg.clientUi.getBattleWindowManageArmyForRound();
                if (manageArmyForRound.isShown()) {
                    manageArmyForRound.hide();
                } else {
                    manageArmyForRound.show();
                }
            }
        });

        button = new TextButton("Start Round", Assets.uiSkin);
        add(button).expandY().fill();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // FIXME core.battle removed
//                ClientCore.core.battle.calculateRound();
//                ClientCore.core.battle.playRound();
            }
        });

    }

    @Override
    public void show() {
        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX - getWidth() - ClientUi.SPACING,
                GdxgConstants.SCREEN_HEIGHT_IN_PX - getHeight() - ClientUi.SPACING);
        updateAnimationBounds();
        super.show();
    }

}
