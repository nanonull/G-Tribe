package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class EvadeEffect extends AbstractUnitEffect {
    public static final int DODGE_PERC = ConcentrationEffect.DODGE_PERC;
    public static final String HINT = "Dodge +" + DODGE_PERC;

    public EvadeEffect() {
        super(EvadeEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getShortIconName() {
        return "Evade";
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + HINT;
    }
}
