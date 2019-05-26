package conversion7.game.stages.battle_deprecated2;

import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.items.InjuryEffect;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import org.slf4j.Logger;
import org.testng.Assert;

public class BattleUnit {
    private static final Logger LOG = Utils.getLoggerForClass();
    private Unit unit;
    private BattleCell cell;
    public int experience;

    public BattleUnit(Unit unit) {
        this.unit = unit;
        unit.squad.calculateResistanceToDeathChanceInActiveBattle();
    }

    public Unit getUnit() {
        return unit;
    }

    public BattleCell getCell() {
        return cell;
    }

    public void setCell(BattleCell cell) {
        this.cell = cell;
    }

    public int getSpeed() {
        return unit.squad.getTotalParam(UnitParameterType.AGILITY);
    }

    /** Used during round progress */
    public boolean isAlive() {
        return unit.getSquad().getMainParams().get(UnitParameterType.HEALTH) > 0;
    }

    public boolean validateUnitDeathDuringRound() {
        if (!isAlive()) {
            cell.getActiveUnitsMap().remove(unit);
            return true;
        }
        return false;
    }

    public void validateUnitDeathAfterBattle() {
        if (!isAlive()) {

            if (!GdxgConstants.isAlwaysDontResurrectUnitsInBattle() &&
                    (GdxgConstants.isResurrectUnitInBattleIfResistFailed()
                            || MathUtils.RANDOM.nextFloat() < unit.squad.getResistanceToDeathChance())) {
                unit.squad.getMainParams().put(UnitParameterType.HEALTH, Unit.HEALTH_AFTER_BACK_TO_LIFE);
                unit.squad.getEffectManager().addEffect(new InjuryEffect());
                LOG.info("Unit back to life after battle: {}", unit);
            } else {

                LOG.info("Unit was killed in battle: {}", unit);
                if (unit instanceof BaseAnimalClass) {
                    cell.getBattleField().updateKilledAnimals(+1);
                }
                Assert.assertTrue(unit.squad.isDeathValidationForNextHurtActive());
                WorldSquad.killUnit(unit.squad);
                return;
            }
        }

        // still alive
        unit.squad.updateExperience(experience);
    }
}
