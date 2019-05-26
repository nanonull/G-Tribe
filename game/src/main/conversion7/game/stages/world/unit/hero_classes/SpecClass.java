package conversion7.game.stages.world.unit.hero_classes;

import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.items.spec.AgileGuyEffect;
import conversion7.game.stages.world.unit.effects.items.spec.BigGuyEffect;
import conversion7.game.stages.world.unit.effects.items.spec.FastGuyEffect;

public enum SpecClass {
    BIG("shield_48px", BigGuyEffect.BOOST_HINT, BigGuyEffect.class),
    AGILE("shadow_48px", AgileGuyEffect.BOOST_HINT, AgileGuyEffect.class),
    FAST("heal_48px", FastGuyEffect.BOOST_HINT, FastGuyEffect.class),;

    private String iconName;
    private String boostHint;
    private Class<? extends AbstractUnitEffect> effectClass;

    SpecClass(String iconName, String boostHint, Class<? extends AbstractUnitEffect> effectClass) {
        this.iconName = iconName;
        this.boostHint = boostHint;
        this.effectClass = effectClass;
    }

    public static SpecClass getRandom() {
        SpecClass[] heroClasses = values();
        return heroClasses[MathUtils.random(0, heroClasses.length - 1)];
    }

    public String get1stSymbol() {
        return toString().substring(0, 1);
    }

    public String getIconName() {
        return iconName;
    }

    public String getFullDescription() {
        return effectClass.getSimpleName() + ": " + boostHint;
    }

    public Class<? extends AbstractUnitEffect> getEffectClass() {
        return effectClass;
    }
}
