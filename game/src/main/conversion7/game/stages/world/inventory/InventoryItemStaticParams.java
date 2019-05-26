package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.game.stages.world.inventory.items.*;
import conversion7.game.stages.world.inventory.items.weapons.*;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.unit_classes.UnitClassConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class InventoryItemStaticParams {

    // ITEMS
    public static final InventoryItemStaticParams STICK = new InventoryItemStaticParams("Stick");
    public static final InventoryItemStaticParams CUDGEL = new InventoryItemStaticParams("Cudgel");
    public static final InventoryItemStaticParams HAMMER = new InventoryItemStaticParams("Hammer");
    public static final InventoryItemStaticParams SPEAR = new InventoryItemStaticParams("Spear");
    public static final InventoryItemStaticParams MACE = new InventoryItemStaticParams("Mace");
    public static final InventoryItemStaticParams MAMMOTH_MACE = new InventoryItemStaticParams("Mammoth Mace");
    public static final InventoryItemStaticParams POWER_FIST = new InventoryItemStaticParams("Power fist");

    public static final InventoryItemStaticParams STONE = new InventoryItemStaticParams("Stone");
    public static final InventoryItemStaticParams RADIOACTIVE_ISOTOPE = new InventoryItemStaticParams("Radio Isotope");
    public static final InventoryItemStaticParams FUSION_CELL = new InventoryItemStaticParams("Fusion Cell");
    public static final InventoryItemStaticParams JAVELIN = new InventoryItemStaticParams("Javelin");
    public static final InventoryItemStaticParams ARROW = new InventoryItemStaticParams("Arrow");

    public static final InventoryItemStaticParams ATLATL = new InventoryItemStaticParams("Atlatl");
    public static final InventoryItemStaticParams ATOMIC_BLASTER = new InventoryItemStaticParams("Atomic Blaster");
    public static final InventoryItemStaticParams FUSION_BLASTER = new InventoryItemStaticParams("Fusion Blaster");
    public static final InventoryItemStaticParams BOW = new InventoryItemStaticParams("Bow");

    public static final InventoryItemStaticParams SKIN = new InventoryItemStaticParams("Skin");
    public static final InventoryItemStaticParams SKIN_ROBE = new InventoryItemStaticParams("Skin robe");

    // RES
    public static final InventoryItemStaticParams MENTAL_GENERATOR = new InventoryItemStaticParams("Mental Generator")
            .setDescription("Generates mental, psy and null waves. " +
                    "Sometimes can have effect on unknown or nonexistent areas of universe.\n \n" /*+
                    "Provides skills:\n" +
                    " * " + ControlUnitAction.class.getSimpleName()*/);
    public static final InventoryItemStaticParams TOOTH = new InventoryItemStaticParams("Tooth");
    public static final InventoryItemStaticParams TUSK = new InventoryItemStaticParams("Tusk");
    public static final InventoryItemStaticParams FANG = new InventoryItemStaticParams("Fang");
    public static final InventoryItemStaticParams MAMMOTH_TUSK = new InventoryItemStaticParams("Mammoth Tusk");
    public static final InventoryItemStaticParams MAMMOTH_FANG = new InventoryItemStaticParams("Mammoth Fang");
    public static final InventoryItemStaticParams STRING = new InventoryItemStaticParams("String");
    public static final InventoryItemStaticParams PIKE = new InventoryItemStaticParams("Pike");
    public static final InventoryItemStaticParams PIKE_FROM_MAMMOTH_TUSK = new InventoryItemStaticParams("Pike from mammoth tusk");
    public static final InventoryItemStaticParams CAMP_BUILDING_KIT = new InventoryItemStaticParams("Camp Building Kit")
            .setDescription("Kit contains schemas, supplies to place base camp.");
    public static final InventoryItemStaticParams SCHEMAS_BOOK = new InventoryItemStaticParams("Book: Survive or Die")
            .setDescription("Contains schemas for building and useful guides about survival on any planet");

    public static final InventoryItemStaticParams APPLE = new InventoryItemStaticParams("Apple");
    public static final InventoryItemStaticParams IRON_ORE = new InventoryItemStaticParams("Iron ore");
    public static final InventoryItemStaticParams URANUS = new InventoryItemStaticParams("Uranus");
    public static final InventoryItemStaticParams APPLE_URANUS_BOMB = new InventoryItemStaticParams("Apple-Uranus Bomb");
    /** Set less to get bigger chance in game */
    public static final double HIT_CHANCE_PARM_LOW_LIMIT =
            UnitClassConstants.BASE_POWER - UnitClassConstants.MAX_PARAM_VALUE * 2.2f;
    public static final float DEFAULT_MELEE_HIT_CHANCE_MLT = 0.95f;
    public static final float MAX_HIT_CHANCE_PERC = 100;
    public static final int BASE_HIT_CHANCE_PERC = (int) (MAX_HIT_CHANCE_PERC * DEFAULT_MELEE_HIT_CHANCE_MLT);
    public SkillType requiredSkill;
    private String name;
    private int meleeDamage;
    private int rangedDamage;
    private int hitChancePerc;
    private int heat;
    private int armor;
    private int value;
    private CraftRecipe craftRecipe;
    private String description;

    public InventoryItemStaticParams(String name) {
        this.name = name;
    }

    public static void init() {
        STICK.meleeDamage = (int) (UnitClassConstants.BASE_POWER * 0.2f);
        CUDGEL.meleeDamage = STICK.meleeDamage + 1;
        HAMMER.meleeDamage = STICK.meleeDamage + 2;
        SPEAR.meleeDamage = STICK.meleeDamage + 2;
        MACE.meleeDamage = STICK.meleeDamage + 3;
        MAMMOTH_MACE.meleeDamage = STICK.meleeDamage + 4;
        POWER_FIST.meleeDamage = STICK.meleeDamage + 5;

        BOW.rangedDamage = InventoryItemStaticParams.CUDGEL.meleeDamage;
        ATLATL.rangedDamage = (int) (BOW.rangedDamage * 1.5f);
        ATOMIC_BLASTER.rangedDamage = (int) (BOW.rangedDamage * 5f);
        FUSION_BLASTER.rangedDamage = (int) (BOW.rangedDamage * 3f);

        SPEAR.hitChancePerc = BASE_HIT_CHANCE_PERC;
        STICK.hitChancePerc = SPEAR.hitChancePerc - 1;
        HAMMER.hitChancePerc = STICK.hitChancePerc - 3;
        CUDGEL.hitChancePerc = STICK.hitChancePerc - 5;
        MACE.hitChancePerc = STICK.hitChancePerc - 4;
        MAMMOTH_MACE.hitChancePerc = STICK.hitChancePerc - 5;
        POWER_FIST.hitChancePerc = BASE_HIT_CHANCE_PERC;

        STICK.requiredSkill = SkillType.ARMS;
        STICK.craftRecipe = new CraftRecipe(2, StickItem.class,
                new CraftRecipe.Consumable(CudgelItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        CUDGEL.requiredSkill = SkillType.ARMS;


        HAMMER.requiredSkill = SkillType.PRIMITIVE_WEAPONS;
        HAMMER.craftRecipe = new CraftRecipe(HammerItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StoneItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        SPEAR.requiredSkill = SkillType.PRIMITIVE_WEAPONS;
        SPEAR.craftRecipe = new CraftRecipe(SpearItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(ToothItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        MACE.requiredSkill = SkillType.PRIMITIVE_WEAPONS;
        MACE.craftRecipe = new CraftRecipe(MaceItem.class,
                new CraftRecipe.Consumable(CudgelItem.class, 1),
                new CraftRecipe.Consumable(TuskItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        MAMMOTH_MACE.requiredSkill = SkillType.PRIMITIVE_WEAPONS;
        MAMMOTH_MACE.craftRecipe = new CraftRecipe(MammothMaceItem.class,
                new CraftRecipe.Consumable(CudgelItem.class, 1),
                new CraftRecipe.Consumable(MammothTuskItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        POWER_FIST.requiredSkill = SkillType.UFO_WEAPON;

        ATLATL.hitChancePerc = CUDGEL.hitChancePerc - 5;
        BOW.hitChancePerc = CUDGEL.hitChancePerc;
        FUSION_BLASTER.hitChancePerc = 100;
        ATOMIC_BLASTER.hitChancePerc = 100;

        ATLATL.requiredSkill = SkillType.HUNTING_WEAPONS;
        ATLATL.craftRecipe = new CraftRecipe(AtlatlItem.class,
                new CraftRecipe.Consumable(StickItem.class, 2),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );

        BOW.requiredSkill = SkillType.HUNTING_WEAPONS;
        BOW.craftRecipe = new CraftRecipe(BowItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1)
        );


        ATOMIC_BLASTER.requiredSkill = SkillType.UFO_WEAPON;
        FUSION_BLASTER.requiredSkill = SkillType.UFO_WEAPON;

        // bullets:
//        ARROW.rangedDamage = 1;
//        STONE.rangedDamage = 1;
//        JAVELIN.rangedDamage = 1;
//        RADIOACTIVE_ISOTOPE.rangedDamage = 1;
//
//        STONE.hitChance = MAX_HIT_CHANCE_PERC;
//        ARROW.hitChance = MAX_HIT_CHANCE_PERC;
//        JAVELIN.hitChance = MAX_HIT_CHANCE_PERC;
//        RADIOACTIVE_ISOTOPE.hitChance = MAX_HIT_CHANCE_PERC;

        RADIOACTIVE_ISOTOPE.calculateValue();
        FUSION_CELL.calculateValue();

        STONE.requiredSkill = SkillType.ARMS;

        JAVELIN.requiredSkill = SkillType.PRIMITIVE_WEAPONS;
        JAVELIN.craftRecipe = new CraftRecipe(JavelinItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1),
                new CraftRecipe.Consumable(ToothItem.class, 1)
        );

        ARROW.requiredSkill = SkillType.HUNTING_WEAPONS;
        ARROW.craftRecipe = new CraftRecipe(ArrowItem.class,
                new CraftRecipe.Consumable(StickItem.class, 1),
                new CraftRecipe.Consumable(StringItem.class, 1),
                new CraftRecipe.Consumable(ToothItem.class, 1)
        );

        // clothes:

        SKIN.heat = 5;
        SKIN.armor = 1;
        SKIN.requiredSkill = SkillType.HUNTING;

        SKIN_ROBE.heat = 10;
        SKIN_ROBE.armor = 2;
        SKIN_ROBE.requiredSkill = SkillType.PRIMITIVE_CLOTHING;
        SKIN_ROBE.craftRecipe = new CraftRecipe(SkinRobeItem.class,
                new CraftRecipe.Consumable(SkinItem.class, 3)
        );

        calculateValues();
//        SPEAR.calculateValue();
//        HAMMER.calculateValue();
//        CUDGEL.calculateValue();
//        STICK.calculateValue();
//        SKIN_ROBE.calculateValue();
//        SKIN.calculateValue();
//        ARROW.calculateValue();
//        JAVELIN.calculateValue();
//        STONE.calculateValue();
//        ATOMIC_BLASTER.calculateValue();
//        FUSION_BLASTER.calculateValue();
//        BOW.calculateValue();
//        ATLATL.calculateValue();
//        MAMMOTH_MACE.calculateValue();
//        MACE.calculateValue();
//        POWER_FIST.calculateValue();

    }

    private static void calculateValues() {
        Field[] fields = InventoryItemStaticParams.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(InventoryItemStaticParams.class) && Modifier.isStatic(field.getModifiers())) {
                    InventoryItemStaticParams params = (InventoryItemStaticParams) field.get(null);
                    params.calculateValue();
                }
            } catch (IllegalAccessException e) {
                throw new GdxRuntimeException(e.getMessage(), e);
            }
        }
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

    public int getHitChancePerc() {
        return hitChancePerc;
    }

    public int getHeat() {
        return heat;
    }

    public int getArmor() {
        return armor;
    }

    public int getValue() {
        return value;
    }

    public CraftRecipe getCraftRecipe() {
        return craftRecipe;
    }

    public String getDescription() {
        return description;
    }

    public InventoryItemStaticParams setDescription(String description) {
        this.description = description;
        return this;
    }

    private void calculateValue() {
        value = meleeDamage + rangedDamage + hitChancePerc + heat + armor;
    }
}
