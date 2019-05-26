package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class GrinderEffect extends AbstractUnitEffect {

    public static final String DESC = "Attack enemies which come on adjacent cell";

    public GrinderEffect() {
        super(GrinderEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getShortIconName() {
        return "Grind";
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + DESC;
    }
}
