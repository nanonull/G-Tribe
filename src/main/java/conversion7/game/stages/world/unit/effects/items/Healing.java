package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class Healing extends AbstractUnitEffect {


    public static final int HEALING_LENGTH_STEPS = 5;

    public Healing() {
        super(Healing.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
    }


    @Override
    public void tick() {
        super.tick();
        if (tickCounter == HEALING_LENGTH_STEPS) {
            getOwner().heal();
            tickCounter = 0;
        }
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + HEALING_LENGTH_STEPS;
    }

}
