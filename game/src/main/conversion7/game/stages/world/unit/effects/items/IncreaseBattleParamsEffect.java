package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class IncreaseBattleParamsEffect extends AbstractUnitEffect {

    public static final int PARAMS_ADD_PERCENT = 20;
    public static final float PARAMS_MULTIPLIER = 1 + PARAMS_ADD_PERCENT / 100f;

    public IncreaseBattleParamsEffect() {
        super(IncreaseBattleParamsEffect.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
        setEnabled(false);
        effectParameters.put(UnitParameterType.STRENGTH_PERCENT_ADD, PARAMS_ADD_PERCENT);
        effectParameters.put(UnitParameterType.AGILITY_PERCENT_ADD, PARAMS_ADD_PERCENT);
        effectParameters.put(UnitParameterType.VITALITY_PERCENT_ADD, PARAMS_ADD_PERCENT);
    }
}
