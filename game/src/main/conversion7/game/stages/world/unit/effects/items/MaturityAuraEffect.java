package conversion7.game.stages.world.unit.effects.items;

import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

@Deprecated
public class MaturityAuraEffect extends AbstractUnitEffect {

    public static final int DAMAGE_PENALTY_PERCENT = 25;
    public static final int STARTS_FROM_LEVEL = UnitAge.ADULT.getLevel();
    public static final int ENDS_FROM_LEVEL = UnitAge.OLD.getLevel();
    public static final int CRIT_BOOST_PERCENT = 25;

    public MaturityAuraEffect() {
        super(MaturityAuraEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \nAttacker gets damage penalty -" + DAMAGE_PENALTY_PERCENT +
                "% if he has lower level than me.\n" +
                "Also adds +" + CRIT_BOOST_PERCENT + "% to chance of critical damage (not cumulative) for allies around.";
    }

    public static int applyToDamage(int baseDamage, Unit attacker, Unit targetUnit) {
        if (targetUnit.squad.getEffectManager().containsEffect(MaturityAuraEffect.class) &&
                attacker.squad.getAge().getLevel() < targetUnit.squad.getAge().getLevel()) {
            return MathUtils.getPercentValue(100 - DAMAGE_PENALTY_PERCENT,
                    baseDamage);
        } else {
            return baseDamage;
        }
    }
}
