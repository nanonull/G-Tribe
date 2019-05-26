package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class CriticalDamageChanceBoostEffect extends AbstractUnitEffect {

    public static final int PERCENT_BOOST_1 = 75;
    public static final int EXPIRES_IN_1 = 5;
    public int chanceBoostPercent;
    public int expiresIn;

    public CriticalDamageChanceBoostEffect(int chanceBoostPercent, int expiresIn) {
        super(CriticalDamageChanceBoostEffect.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
        this.chanceBoostPercent = chanceBoostPercent;
        this.expiresIn = expiresIn;
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + expiresIn
                + "\n \nEffect is removed when effect counter expires" +
                "\nCrit chance +" + chanceBoostPercent + "%";
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == expiresIn) {
            remove();
        }
    }
}
