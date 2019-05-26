package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

@Deprecated
public class IncreaseFertilizingChanceEffect extends AbstractUnitEffect {

    public static final int CHANCE = 100;

    public IncreaseFertilizingChanceEffect() {
        super(IncreaseFertilizingChanceEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \nIncreases fertilization chance: " + CHANCE + "%";
    }
}
