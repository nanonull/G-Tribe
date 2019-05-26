package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.humans.theOldest.Propliopithecus;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class TestIncreaseParamsEffect extends AbstractUnitEffect {
    public TestIncreaseParamsEffect() {
        super(TestIncreaseParamsEffect.class.getSimpleName(), Type.POSITIVE);
        Integer incVal = UnitClassConstants.CLASS_STANDARDS
                .get(Propliopithecus.class).getParams().get(UnitParameterType.STRENGTH);
        effectParameters.put(UnitParameterType.STRENGTH, +incVal);
        effectParameters.put(UnitParameterType.AGILITY, +incVal);
        effectParameters.put(UnitParameterType.VITALITY_PERCENT_ADD, +incVal);
    }
}
