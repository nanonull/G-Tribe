package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class HeroUnitEffect extends AbstractUnitEffect {
    public static final int DMG_INCREASE_PERCENT = 20;

    public HeroUnitEffect() {
        super(HeroUnitEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public String getShortIconName() {
        return "Hero";
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n " +
                "\n" + getOwner().getHeroClass().getNameRoleDescription() +
                "\n" + getOwner().getHeroClass().getDescription()
                ;
    }

    @Deprecated
    public float getEffectMultiplier() {
        return 1 + DMG_INCREASE_PERCENT / 100f;
    }

}
