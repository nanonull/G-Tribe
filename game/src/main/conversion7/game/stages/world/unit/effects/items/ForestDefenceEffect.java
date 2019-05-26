package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

@Deprecated
public class ForestDefenceEffect extends AbstractUnitEffect {

    public static final int PARAMS_ADD_PERCENT = 25;
    public static final float PARAMS_MULTIPLIER = 1 + PARAMS_ADD_PERCENT / 100f;

    public ForestDefenceEffect() {
        super(ForestDefenceEffect.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
//        effectParameters.put(UnitParameterType.STRENGTH_PERCENT_ADD, PARAMS_ADD_PERCENT);
//        effectParameters.put(UnitParameterType.AGILITY_PERCENT_ADD, PARAMS_ADD_PERCENT);
//        effectParameters.put(UnitParameterType.VITALITY_PERCENT_ADD, PARAMS_ADD_PERCENT);
    }
}
