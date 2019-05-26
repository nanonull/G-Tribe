package conversion7.game.stages.world.unit.hero_classes;

import conversion7.engine.utils.MathUtils;

public enum HeroClass {
    HODOR("shield_48px", "Tank", "+50% power, +2 power every new lvl"),
    SHADOW("shadow_48px", "Assassin", "Agile and speed"),
    WITCH("spiral_48px", "Witch", "Controlling enemies"),
    DRUID("heal_48px", "Druid", "Support. Controlling animals");

    private String iconName;
    private String role;
    private String description;

    HeroClass(String iconName, String role, String description) {
        this.iconName = iconName;
        this.role = role;
        this.description = description;
    }

    public static HeroClass getRandom() {
        HeroClass[] heroClasses = values();
        return heroClasses[MathUtils.random(0, heroClasses.length - 1)];
    }

    public String get1stSymbol() {
        return toString().substring(0, 1);
    }

    public String getIconName() {
        return iconName;
    }

    public String getRole() {
        return role;
    }

    public String getNameRoleDescription() {
        return name() + " - " + role;
    }

    public String getDescription() {
        return description;
    }
}
