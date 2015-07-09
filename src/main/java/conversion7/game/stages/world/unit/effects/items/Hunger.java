package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class Hunger extends AbstractUnitEffect {
    public Hunger() {
        super(Hunger.class.getSimpleName(), Type.NEGATIVE, new UnitParameters().setHealth(-1));
    }
}
