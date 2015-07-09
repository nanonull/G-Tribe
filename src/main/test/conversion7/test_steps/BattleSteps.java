package conversion7.test_steps;

import conversion7.engine.ClientCore;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle.Battle;

import static org.fest.assertions.api.Assertions.assertThat;

public class BattleSteps {

    public static Battle getActiveBattle() {
        assertThat(ClientCore.core.isBattleActiveStage()).isTrue();
        return (Battle) ClientCore.core.getActiveStage();
    }

    public static void setAutoBattle(boolean b) {
        GdxgConstants.AUTO_BATTLE_FOR_PLAYER = b;
    }
}
