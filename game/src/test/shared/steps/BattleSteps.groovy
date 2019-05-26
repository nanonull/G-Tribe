package shared.steps

import conversion7.aop.TestSteps
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.objects.unit.AbstractSquad
import org.slf4j.Logger

@TestSteps
public class BattleSteps extends BaseSteps {
    private static final Logger LOG = Utils.getLoggerForClass();

    public void setResurrectUnitInBattleIfResistFailed(boolean b) {
        GdxgConstants.setResurrectUnitInBattleIfResistFailed(b);
    }

    public void setAlwaysDontResurrectUnitsInBattle(boolean b) {
        GdxgConstants.setAlwaysDontResurrectUnitsInBattle(b);
    }

    public void startBattle(AbstractSquad attacker, AbstractSquad defender) {
        attacker.unit.executeMeleeAttack(defender)
    }

}
