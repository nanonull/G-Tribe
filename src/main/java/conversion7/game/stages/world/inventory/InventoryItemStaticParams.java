package conversion7.game.stages.world.inventory;

import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.AtlatlItem;
import conversion7.game.stages.world.inventory.items.BowItem;
import conversion7.game.stages.world.inventory.items.CudgelItem;
import conversion7.game.stages.world.inventory.items.HammerItem;
import conversion7.game.stages.world.inventory.items.JavelinItem;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.inventory.items.SkinRobeItem;
import conversion7.game.stages.world.inventory.items.SpearItem;
import conversion7.game.stages.world.inventory.items.StickItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.inventory.items.StringItem;
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.FlayingSkill;
import conversion7.game.stages.world.team.skills.HoldWeaponSkill;
import conversion7.game.stages.world.team.skills.HuntingWeaponsSkill;
import conversion7.game.stages.world.team.skills.PrimitiveClothingSkill;
import conversion7.game.stages.world.team.skills.PrimitiveWeaponsSkill;

public class InventoryItemStaticParams {

    public static final int MAX_ATTACK_CHANCE = 10;

    // ITEMS
    public static final InventoryItemStaticParams STICK = new InventoryItemStaticParams("Stick");
    public static final InventoryItemStaticParams CUDGEL = new InventoryItemStaticParams("Cudgel");
    public static final InventoryItemStaticParams HAMMER = new InventoryItemStaticParams("Hammer");
    public static final InventoryItemStaticParams SPEAR = new InventoryItemStaticParams("Spear");

    public static final InventoryItemStaticParams STONE = new InventoryItemStaticParams("Stone");
    public static final InventoryItemStaticParams JAVELIN = new InventoryItemStaticParams("Javelin");
    public static final InventoryItemStaticParams ARROW = new InventoryItemStaticParams("Arrow");

    public static final InventoryItemStaticParams ATLATL = new InventoryItemStaticParams("Atlatl");
    public static final InventoryItemStaticParams BOW = new InventoryItemStaticParams("Bow");

    public static final InventoryItemStaticParams SKIN = new InventoryItemStaticParams("Skin");
    public static final InventoryItemStaticParams SKIN_ROBE = new InventoryItemStaticParams("Skin robe");

    public static final InventoryItemStaticParams MAMMOTH_TUSK = new InventoryItemStaticParams("Mammoth Tusk");
    public static final InventoryItemStaticParams STRING = new InventoryItemStaticParams("String");
    public static final InventoryItemStaticParams PIKE = new InventoryItemStaticParams("Pike");
    public static final InventoryItemStaticParams PIKE_FROM_MAMMOTH_TUSK = new InventoryItemStaticParams("Pike from mammoth tusk");

    public static void init() {
        // melee:

        STICK.meleeDamage = 2;
        STICK.attackChance = 9;
        STICK.calculateValue();
        STICK.requiredSkillLearned = HoldWeaponSkill.class;
        STICK.craftRecipe = new CraftRecipe(2, StickItem.class,
                new CraftRecipe.Consumable(CudgelItem.class, 1)
        );

        CUDGEL.meleeDamage = 3;
        CUDGEL.attackChance = 9;
        CUDGEL.calculateValue();
        CUDGEL.requiredSkillLearned = HoldWeaponSkill.class;

        HAMMER.meleeDamage = 4;
        HAMMER.attackChance = 8;
        HAMMER.calculateValue();
        HAMMER.requiredSkillLearned = PrimitiveWeaponsSkill.class;
        HAMMER.craftRecipe = new CraftRecipe(HammerItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StoneItem.class, 1)
        );

        SPEAR.meleeDamage = 4;
        SPEAR.attackChance = 8;
        SPEAR.calculateValue();
        SPEAR.requiredSkillLearned = PrimitiveWeaponsSkill.class;
        SPEAR.craftRecipe = new CraftRecipe(SpearItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1)
        );

        // bullets:

        STONE.rangedDamage = 1;
        STONE.attackChance = 6;
        STONE.equipQuantityLimit = 10;
        STONE.calculateValue();
        STONE.requiredSkillLearned = HoldWeaponSkill.class;

        JAVELIN.rangedDamage = 3;
        JAVELIN.attackChance = 7;
        JAVELIN.equipQuantityLimit = 5;
        JAVELIN.calculateValue();
        JAVELIN.requiredSkillLearned = PrimitiveWeaponsSkill.class;
        JAVELIN.craftRecipe = new CraftRecipe(JavelinItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1)
        );

        ARROW.rangedDamage = 1;
        ARROW.attackChance = 6;
        ARROW.equipQuantityLimit = 20;
        ARROW.calculateValue();
        ARROW.requiredSkillLearned = HuntingWeaponsSkill.class;
        ARROW.craftRecipe = new CraftRecipe(ArrowItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1)
        );

        // range:

        ATLATL.rangedDamageModifier = 2;
        ATLATL.attackChanceModifier = -1;
        ATLATL.bulletClass = JavelinItem.class;
        ATLATL.calculateValue();
        ATLATL.requiredSkillLearned = HuntingWeaponsSkill.class;
        ATLATL.craftRecipe = new CraftRecipe(AtlatlItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1)
        );

        BOW.rangedDamageModifier = 3;
        BOW.attackChanceModifier = 2;
        BOW.bulletClass = ArrowItem.class;
        BOW.calculateValue();
        BOW.requiredSkillLearned = HuntingWeaponsSkill.class;
        BOW.craftRecipe = new CraftRecipe(BowItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        // clothes:

        SKIN.heat = 5;
        SKIN.armor = 1;
        SKIN.calculateValue();
        SKIN.requiredSkillLearned = FlayingSkill.class;

        SKIN_ROBE.heat = 10;
        SKIN_ROBE.armor = 2;
        SKIN_ROBE.calculateValue();
        SKIN_ROBE.requiredSkillLearned = PrimitiveClothingSkill.class;
        SKIN_ROBE.craftRecipe = new CraftRecipe(SkinRobeItem.class,
                new CraftRecipe.Consumable(SkinItem.class, 3)
        );

    }

    private String name;
    private int meleeDamage;
    private int rangedDamage;
    private int attackChance;
    private int equipQuantityLimit;
    private int heat;
    private int armor;
    private int attackChanceModifier;
    private int rangedDamageModifier;
    private int value;
    private Class<? extends RangeBulletItem> bulletClass;
    private Class<? extends AbstractSkill> requiredSkillLearned;
    private CraftRecipe craftRecipe;

    public InventoryItemStaticParams(String name) {
        this.name = name;
    }

    public Class<? extends AbstractSkill> getRequiredSkillLearned() {
        return requiredSkillLearned;
    }

    public String getName() {
        return name;
    }

    public int getMeleeDamage() {
        return meleeDamage;
    }

    public int getRangedDamage() {
        return rangedDamage;
    }

    public int getAttackChance() {
        return attackChance;
    }

    public int getEquipQuantityLimit() {
        return equipQuantityLimit;
    }

    public int getHeat() {
        return heat;
    }

    public int getArmor() {
        return armor;
    }

    public int getAttackChanceModifier() {
        return attackChanceModifier;
    }

    public int getRangedDamageModifier() {
        return rangedDamageModifier;
    }

    public Class<? extends RangeBulletItem> getBulletClass() {
        return bulletClass;
    }

    public int getValue() {
        return value;
    }

    private void calculateValue() {
        value = meleeDamage + rangedDamage + attackChance + heat + armor + attackChanceModifier + rangedDamageModifier;
        if (equipQuantityLimit > 0) {
            value += equipQuantityLimit / 10;
        }
    }

    public CraftRecipe getCraftRecipe() {
        return craftRecipe;
    }
}
