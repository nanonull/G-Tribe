package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ConcentrationEffect extends AbstractUnitEffect {
    public static final int DODGE_PERC = 50;
    public static final float HIT_CHANCE_MLT_FOR_COUNTER_HIT = 0.75f;
    public static final String HINT = "Dodge for next attack +" + DODGE_PERC +
            "\nEffect is removed after activation or when unit starts turn";

    public ConcentrationEffect() {
        super(ConcentrationEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + HINT;
    }
}
