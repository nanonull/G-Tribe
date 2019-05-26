package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

@Deprecated
public class InjuryEffect extends AbstractUnitEffect {

    public static final int DECREASE_PARAMS_BY_VALUE = 1;

    public InjuryEffect() {
        super(InjuryEffect.class.getSimpleName(), Type.NEGATIVE);
        effectParameters.put(UnitParameterType.STRENGTH, -DECREASE_PARAMS_BY_VALUE);
        effectParameters.put(UnitParameterType.AGILITY, -DECREASE_PARAMS_BY_VALUE);
    }

    @Override
    public void tick() {
        if (getOwner().power.getCurrentValue() == getOwner().power.getMaxValue()) {
            completed = true;
        }
    }
}
