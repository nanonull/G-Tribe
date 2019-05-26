package conversion7.game.stages.world.unit.effects.items.spec;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class FastGuyEffect extends AbstractUnitEffect {
    public static final String BOOST_HINT = "+1 move, -25% base damage";
    public static final float BOOST_MLT = 0.75f;

    public FastGuyEffect() {
        super(FastGuyEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + BOOST_HINT;
    }


}
