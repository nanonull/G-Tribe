package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class Childbearing extends AbstractUnitEffect {


    public static final int PREGNANCY_DURATION = 10;
    private Unit child;

    public Childbearing(Unit mother, Unit child) {
        super(Childbearing.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
        this.child = child;
        child.setMother(mother);
    }

    public Unit getChild() {
        return child;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == PREGNANCY_DURATION) {
            child.birth();
            getOwner().getEffectManager().removeEffect(this);
        }
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + PREGNANCY_DURATION;
    }
}
