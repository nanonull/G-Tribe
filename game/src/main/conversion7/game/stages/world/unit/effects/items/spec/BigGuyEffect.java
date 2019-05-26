package conversion7.game.stages.world.unit.effects.items.spec;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class BigGuyEffect extends AbstractUnitEffect {
    public static final String BOOST_HINT = "-1 move, +25% power";
    public static final float BOOST_MLT = 0.25f;

    public BigGuyEffect() {
        super(BigGuyEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + BOOST_HINT;
    }


}
