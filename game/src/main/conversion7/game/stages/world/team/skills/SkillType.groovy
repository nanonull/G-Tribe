package conversion7.game.stages.world.team.skills

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.team.skills.items.*
import conversion7.game.stages.world.unit.hero_classes.HeroClass
import conversion7.game.ui.world.team_skills.TeamSkillTreeNode

enum SkillType {

//    , HANDS_AS_A_TOOL(HandsAsAToolSkill, "Hands as a Tool"
//            , HandsAsAToolSkill.effectPerLevel.length - 1, true,
//            "Use your hands to be more effective\n+%d%% damage in battle",
//            new TeamSkillTreeNode(2, 0, "HAND")
//            , { [] })

    ARMS(ArmsAsToolSkill, "Arms as tool", 1, true,
            "Here human begins... Use your arms to do wonderful things! \n" +
                    "Like throw objects or hit enemy with a stick!",
            new TeamSkillTreeNode(2, 0, "Arms as tool")
            , { [] })

    , PRIMITIVE_WEAPONS(PrimitiveWeaponsSkill.class, "Primitive Weapons", 1, true,
            "Allows humans of your tribe to make and use hammers, javelins and spears",
            new TeamSkillTreeNode(2, 2, "Prim Weapon")
            , { [STONE_WORK] })

    , HUNTING_WEAPONS(HuntingWeaponsSkill, "Hunting Weapons", 1, true,
            "Allows humans of your tribe to make and use bow, arrows and atlatl",
            new TeamSkillTreeNode(2, 3, "Hunt Weapon")
            , { [PRIMITIVE_WEAPONS] })

    , UFO_WEAPON(UfoWeaponsSkill, "Ufo Weapons", 1, true,
            "Allows humans of your tribe to use ufo weapon",
            new TeamSkillTreeNode(2, 4, "Ufo Weapon")
            , { [HUNTING_WEAPONS] })

//    , STUNNING(StunningSkill, "Stunning", 1, false
//            , "Target unit will be stunned for ${StunnedEffect.DURATION} steps.\n" +
//            "Male units get this action on 2nd level and female get it on 3rd.",
//            new TeamSkillTreeNode(4, 3, "Stunning")
//            , { [WEAPON_MASTERY] })

//    , PROVOCATION(ProvocationSkill, "Provoke for attack", 1, false
//            , ProvokeAction.DESC,
//            new TeamSkillTreeNode(7, 1, "Provoke")
//            , { [BRAIN] })

    , BRAIN(FireSkill, "Brain", 1, false,
            "It's possible to talk and do very smart things" +
                    "\nBrain here means 'human' brain, which has to be learned by extraterrestrial civilizations as well...",
            new TeamSkillTreeNode(6, 0, "Brain")
            , { [] })

    , FIRE(FireSkill, "Fire", 1, false,
            "It's possible to use fire",
            new TeamSkillTreeNode(6, 1, "Fire")
            , { [ARMS, BRAIN] })

    , TAME_BEAST(TameBeastSkill, "Tame Beast", 1, false,
            "It's possible to tame beasts",
            new TeamSkillTreeNode(6, 2, "Tame")
            , { [FIRE] })

    , BUILD_CAMP(BuildCampSkill, "Build Camp", 1, false,
            "It's possible to build a camp.\n \n$Camp.HINT"
            , new TeamSkillTreeNode(7, 2, "Camp")
            , { [FIRE] }),

    STONE_WORK(StoneWorkSkill, "Stone Work", 1, true,
            "\nUnlocks basic craft",
            new TeamSkillTreeNode(1, 1, "Stone work")
            , { [ARMS] })

    , HUNTING(FlayingSkill, "Hunting", 1, true,
            "Allows to hunt resources from animals in melee combat" +
                    "\nUnlocks " + HeroClass.SHADOW + " hero class",
            new TeamSkillTreeNode(3, 1, "Hunting")
            , { [ARMS] })

    , WEAPON_MASTERY(WeaponMasterySkill, "Weapon Mastery", WeaponMasterySkill.MAX_LEVEL, true,
            "All attacks with weapon get +" + WeaponMasterySkill.ADD_DMG + " damage per skill level",
            new TeamSkillTreeNode(3, 3, "Weapon Master")
            , { [PRIMITIVE_WEAPONS] })

    , PRIMITIVE_CLOTHING(PrimitiveClothingSkill.class, "Primitive Clothing", 1, true,
            "You can make weak but quite warm clothes using animal skins.\n",
            new TeamSkillTreeNode(0, 2, "Prim Clothes")
            , { [STONE_WORK] })

    , LOCOMOTION(LocomotionSkill, "Locomotion", LocomotionSkill.MAX_LEVEL, true,
            "+" + LocomotionSkill.DMG_PER_LVL + " damage per skill level" +
                    "\n+" + LocomotionSkill.DODGE_PERC_PER_LVL + " dodge per skill level",
            new TeamSkillTreeNode(4, 1, "Locomotion")
            , { [HUNTING] })

    , TOTEMS(TotemsSkill, "Totem", 1, false,
            "It's possible to create totems",
            new TeamSkillTreeNode(9, 1, "Totem")
            , { [RITUAL] })

    , RITUAL(RitualSkill, "Ritual", 1, false,
            "Shaman is able to start ritual\n" +
                    "(after at least 1 Primal experience has been collected by tribe)",
            new TeamSkillTreeNode(9, 0, "Ritual")
            , { [] })

    , DRUID(HealingSkill, "Druid hero", 1, true,
            "\n \nUnlocks " + HeroClass.DRUID.getNameRoleDescription() + " hero\n ",
            new TeamSkillTreeNode(8, 1, "Druid")
            , { [RITUAL] })

    , WITCHER(WitcherHeroSkill, "Witcher hero", 1, true,
            "Unlocks " + HeroClass.WITCH.getNameRoleDescription() + " hero",
            new TeamSkillTreeNode(10, 1, "Witcher")
            , { [RITUAL] })

    public static final Array<SkillType> skillTypes = new Array<>();

    public Class<? extends AbstractSkill> skillClass;
    public final String name;
    public final int levels;
    public final boolean passive;
    public final String description;
    public TeamSkillTreeNode teamSkillTreeNode;

    public List<SkillType> childSkillTypes = new ArrayList<>()
    public List<SkillType> parentSkillTypes
    private Closure<List<SkillType>> parentSkillTypesClosure

    SkillType(Class<? extends AbstractSkill> skillClass, String name, int levels
              , boolean passive, String description, TeamSkillTreeNode teamSkillTreeNode
              , Closure<List<SkillType>> parentSkillTypesClosure) {
        this.parentSkillTypesClosure = parentSkillTypesClosure
        this.skillClass = skillClass;
        this.name = name;
        this.levels = levels;
        this.passive = passive;
        this.description = description;
        this.teamSkillTreeNode = teamSkillTreeNode;
        teamSkillTreeNode.setSkillType(this);
    }

    @Deprecated
    static SkillType fromClass(Class<? extends AbstractSkill> aClass) {
        for (SkillType skillType : values()) {
            if (skillType.skillClass == aClass) {
                return skillType
            }
        }
        throw new GdxRuntimeException("not found type: $aClass");
    }

    public boolean isMultiLevel() {
        return levels > 1;
    }

    static void postInit() {
        for (SkillType skill : values()) {
            skillTypes.add(skill);

            skill.parentSkillTypes = skill.parentSkillTypesClosure.call()
            for (SkillType parentSkill : skill.parentSkillTypes) {
                parentSkill.childSkillTypes.add(skill)
                parentSkill.teamSkillTreeNode.addChildNode(skill.teamSkillTreeNode)
            }
        }
    }
}
