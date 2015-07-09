package conversion7.game.stages.world.team.skills.statics;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.FireSkill;
import conversion7.game.stages.world.team.skills.FlayingSkill;
import conversion7.game.stages.world.team.skills.HandsAsAToolSkill;
import conversion7.game.stages.world.team.skills.HoldWeaponSkill;
import conversion7.game.stages.world.team.skills.HuntingWeaponsSkill;
import conversion7.game.stages.world.team.skills.LocomotionSkill;
import conversion7.game.stages.world.team.skills.PrimitiveClothingSkill;
import conversion7.game.stages.world.team.skills.PrimitiveWeaponsSkill;
import conversion7.game.stages.world.team.skills.StoneWorkSkill;
import conversion7.game.stages.world.team.skills.TameBeastSkill;
import conversion7.game.stages.world.team.skills.TotemsSkill;
import conversion7.game.stages.world.team.skills.WeaponMasterySkill;
import conversion7.game.ui.world.team_skills.TeamSkillTreeNode;

import java.util.HashMap;
import java.util.Map;

public class SkillStaticParams {


    public static Map<Class<? extends AbstractSkill>, SkillStaticParams> SKILL_STATIC_PARAMS = new HashMap<>();

    static {
        SKILL_STATIC_PARAMS.put(HandsAsAToolSkill.class, new SkillStaticParams(HandsAsAToolSkill.class, "Hands as a Tool", HandsAsAToolSkill.effectPerLevel.length - 1, true,
                "Use your hands to be more effective in battle and life\n+%d%% damage in battle",
                new TeamSkillTreeNode(2, 0)));
        SKILL_STATIC_PARAMS.put(StoneWorkSkill.class, new SkillStaticParams(StoneWorkSkill.class, "Stone Work", 1, true,
                "It's more easier to get meat from prey using sharp-edged stones\nx2 food after hunting",
                new TeamSkillTreeNode(1, 1)));
        SKILL_STATIC_PARAMS.put(HoldWeaponSkill.class, new SkillStaticParams(HoldWeaponSkill.class, "Hold a Weapon", 1, true,
                "Throw stone or hit enemy with a stick or cudgel!",
                new TeamSkillTreeNode(3, 1)));
        SKILL_STATIC_PARAMS.put(FlayingSkill.class, new SkillStaticParams(FlayingSkill.class, "Flaying", 1, true,
                "Allows to get skins during hunting\nSkins give a little heat",
                new TeamSkillTreeNode(0, 2)));
        SKILL_STATIC_PARAMS.put(PrimitiveWeaponsSkill.class, new SkillStaticParams(PrimitiveWeaponsSkill.class, "Primitive Weapons", 1, true,
                "Allows you to make and use hammers, javelins and spears",
                new TeamSkillTreeNode(2, 2)));
        SKILL_STATIC_PARAMS.put(WeaponMasterySkill.class, new SkillStaticParams(WeaponMasterySkill.class, "Weapon Mastery", WeaponMasterySkill.effectPerLevel.length - 1, true,
                "Characters deal damage with weapon more effectively\n+%d%% damage",
                new TeamSkillTreeNode(4, 2)));
        SKILL_STATIC_PARAMS.put(PrimitiveClothingSkill.class, new SkillStaticParams(PrimitiveClothingSkill.class, "Primitive Clothing", 1, true,
                "You can make durable and quite warm clothes using animal skins",
                new TeamSkillTreeNode(0, 3)));
        SKILL_STATIC_PARAMS.put(HuntingWeaponsSkill.class, new SkillStaticParams(HuntingWeaponsSkill.class, "Hunting Weapons", 1, true,
                "Allows you to make and use bow, arrows and atlatl",
                new TeamSkillTreeNode(2, 3)));

        // new branch
        SKILL_STATIC_PARAMS.put(LocomotionSkill.class, new SkillStaticParams(LocomotionSkill.class, "Locomotion", LocomotionSkill.effectPerLevel.length - 1, true,
                "+%d%% damage and defence in battle",
                new TeamSkillTreeNode(4, 0)));
        SKILL_STATIC_PARAMS.put(FireSkill.class, new SkillStaticParams(FireSkill.class, "Fire", 1, false,
                "It's possible to use fire",
                new TeamSkillTreeNode(5, 0)));
        SKILL_STATIC_PARAMS.put(TameBeastSkill.class, new SkillStaticParams(TameBeastSkill.class, "Tame Beast", 1, false,
                "It's possible to tame beasts",
                new TeamSkillTreeNode(5, 1)));
        SKILL_STATIC_PARAMS.put(TotemsSkill.class, new SkillStaticParams(TotemsSkill.class, "Totems", 1, false,
                "It's possible to create and use totems",
                new TeamSkillTreeNode(6, 0)));

        buildTreeRelations();
    }

    private static void buildTreeRelations() {
        Array<SkillStaticParams> wipArray;

        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(StoneWorkSkill.class), SKILL_STATIC_PARAMS.get(HoldWeaponSkill.class));
        SKILL_STATIC_PARAMS.get(HandsAsAToolSkill.class).setChildSkillParams(wipArray);

        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(FlayingSkill.class), SKILL_STATIC_PARAMS.get(PrimitiveWeaponsSkill.class));
        SKILL_STATIC_PARAMS.get(StoneWorkSkill.class).setChildSkillParams(wipArray);

        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(PrimitiveWeaponsSkill.class), SKILL_STATIC_PARAMS.get(WeaponMasterySkill.class));
        SKILL_STATIC_PARAMS.get(HoldWeaponSkill.class).setChildSkillParams(wipArray);

        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(PrimitiveClothingSkill.class));
        SKILL_STATIC_PARAMS.get(FlayingSkill.class).setChildSkillParams(wipArray);

        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(HuntingWeaponsSkill.class));
        SKILL_STATIC_PARAMS.get(PrimitiveWeaponsSkill.class).setChildSkillParams(wipArray);

        //
        wipArray = new Array<>();
        wipArray.addAll(SKILL_STATIC_PARAMS.get(TameBeastSkill.class));
        SKILL_STATIC_PARAMS.get(FireSkill.class).setChildSkillParams(wipArray);
    }


    private ObjectSet<SkillStaticParams> parentSkillParams = new ObjectSet<>();
    private Class<? extends AbstractSkill> skillClass;
    private final String name;
    private final int levels;
    private final boolean passive;
    private final String description;
    private TeamSkillTreeNode teamSkillTreeNode;
    private Array<SkillStaticParams> childSkillParams;


    public SkillStaticParams(Class<? extends AbstractSkill> skillClass, String name, int levels, boolean passive, String description, TeamSkillTreeNode teamSkillTreeNode) {
        this.skillClass = skillClass;
        this.name = name;
        this.levels = levels;
        this.passive = passive;
        this.description = description;
        this.teamSkillTreeNode = teamSkillTreeNode;
        teamSkillTreeNode.setSkillParams(this);
    }

    public Array<SkillStaticParams> getChildSkillParams() {
        return childSkillParams;
    }

    public TeamSkillTreeNode getTeamSkillTreeNode() {
        return teamSkillTreeNode;
    }

    public Class<? extends AbstractSkill> getSkillClass() {
        return skillClass;
    }

    public String getName() {
        return name;
    }

    public int getLevels() {
        return levels;
    }

    public boolean isPassive() {
        return passive;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMultiLevel() {
        return levels > 1;
    }

    public ObjectSet<SkillStaticParams> getParentSkillParams() {
        return parentSkillParams;
    }

    public void setChildSkillParams(Array<SkillStaticParams> childSkillParams) {
        this.childSkillParams = childSkillParams;
        if (childSkillParams.size > 0) {
            for (SkillStaticParams childSkill : childSkillParams) {
                childSkill.getParentSkillParams().add(this);
                getTeamSkillTreeNode().addChildNode(childSkill.getTeamSkillTreeNode());
            }
        }
    }

}
