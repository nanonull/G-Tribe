package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.unit_classes.UnitClassConstants;

public class VengeancePoisonEffect extends AbstractUnitEffect {
    private static final int BASE_DMG = (int) (UnitClassConstants.BASE_POWER * 0.1f);

    public VengeancePoisonEffect() {
        super(VengeancePoisonEffect.class.getSimpleName(), Type.NEGATIVE);
        effectParameters.put(UnitParameterType.HEALTH_DAMAGE_PER_STEP, -BASE_DMG);
        setTickLogicEvery(3);
    }

    @Override
    public String getShortIconName() {
        return "VenPois";
    }

    @Override
    protected void tickLogic() {
        remove();
    }

}
