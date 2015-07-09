package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class Cold extends AbstractUnitEffect {
    public Cold() {
        super(Cold.class.getSimpleName(), Type.NEGATIVE, new UnitParameters().setHealth(-1));
    }
}
