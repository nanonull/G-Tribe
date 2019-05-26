package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ShamanUnitEffect extends AbstractUnitEffect {

    public static final int INSPIRATION_BOOST = 4;
    public static final float SCARE_BOOST = 3f;
    public static final float CONTROL_UNIT_MLT = 2f;
    public static final int GOD_EXP_PER_STEP = 1;

    public ShamanUnitEffect() {
        super(ShamanUnitEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \nShaman inspiration points are multiplied on " + INSPIRATION_BOOST +
                "\nShaman power is multiplied on " + SCARE_BOOST + " during Scare Action check." +
                "\nShaman power is multiplied on " + CONTROL_UNIT_MLT + " during Control Animal Action check."
                ;
    }

}
