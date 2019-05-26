package shared.asserts;

import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.stages.battle_deprecated.BattleFigure;

import static org.fest.assertions.api.Assertions.assertThat;

public class BattleAsserts {

    public static void assertFigureLifeIs(BattleFigure figure, int life) {
        assertThat(figure.params.life).isEqualTo(life);
    }

    public static void assertBattleCompleted(Battle battle) {
        assertThat(battle.isCompleted()).isTrue();
    }
}
