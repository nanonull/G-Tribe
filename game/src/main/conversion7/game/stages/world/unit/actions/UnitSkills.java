package conversion7.game.stages.world.unit.actions;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Predicate;
import conversion7.game.stages.world.objects.actions.items.*;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.items.*;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;

public class UnitSkills {
    public static final ObjectSet<Skill> LEARNABLE_SKILLS = new ObjectSet<>();
    public static final ObjectMap<Object, Skill> SKILLS_BY_ACTION_OR_EFFECT = new ObjectMap<>();

    static {
        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.MELEE_SWING, 1,
                MeleeSwingAction.DESC,
                squad -> true));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.SPRINT, 1,
                SprintAction.DESC,
                squad -> true));

        LEARNABLE_SKILLS.add(new Skill(GrinderEffect.class, 1,
                GrinderEffect.DESC, squad -> true));

        LEARNABLE_SKILLS.add(new Skill(EvadeEffect.class, 1,
                EvadeEffect.HINT, squad -> true));

        LEARNABLE_SKILLS.add(new Skill(IncreaseViewRadiusEffect.class, 1,
                IncreaseViewRadiusEffect.DESC, squad -> true));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.HOOK, 1,
                HookAction.DESC, squad -> squad.team.teamSkillsManager.getSkill(SkillType.BRAIN).isLearnStarted()
                && squad.team.teamSkillsManager.getSkill(SkillType.ARMS).isLearnStarted()));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.FIRE, 1,
                FireAction.DESC, squad -> squad.team.teamSkillsManager.getSkill(SkillType.FIRE).isLearnStarted()));


        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.CHARGE, 1,
                ChargeAction.DESC, squad -> squad.getHeroClass() == HeroClass.HODOR));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.PROVOKE, 1,
                ProvokeAction.DESC, squad -> squad.getHeroClass() == HeroClass.HODOR));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.STUNNING, 1,
                StunningAction.DESC, squad -> squad.getHeroClass() == HeroClass.HODOR));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.HEALING, 1,
                HealingAction.DESC, squad -> squad.getHeroClass() == HeroClass.DRUID));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.CREATE_MOUNTAIN_DEBRIS, 1,
                CreateMountainDebrisAction.DESC, squad -> squad.getHeroClass() == HeroClass.DRUID));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.SCARE_ANIMAL, 1,
                ScaredEffect.SCARE_FULL_HINT, squad -> squad.getHeroClass() == HeroClass.DRUID
                || squad.getHeroClass() == HeroClass.WITCH));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.DISCORD, 1,
                DiscordAction.DESC, squad -> squad.getHeroClass() == HeroClass.WITCH));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.SLEEP, 1,
                SleepAction.DESC, squad -> squad.getHeroClass() == HeroClass.WITCH
                || squad.getHeroClass() == HeroClass.SHADOW));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.PLACE_TRAP, 1,
                PlaceTrapAction.DESC, squad -> squad.getHeroClass() == HeroClass.SHADOW));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.CONCEALMENT, 1,
                ConcealmentAction.DESC, squad -> squad.getHeroClass() == HeroClass.SHADOW));

        LEARNABLE_SKILLS.add(new Skill(VengeanceHitEffect.class, 1,
                VengeanceHitEffect.DESC, squad -> squad.getHeroClass() == HeroClass.SHADOW));


        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.EARTH_SHAKE, 3,
                EarthShakeAction.DESC, squad -> squad.getHeroClass() == HeroClass.HODOR));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.CALL_TREE_DAD, 3,
                CallTreeDadAction.DESC, squad -> squad.getHeroClass() == HeroClass.DRUID));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.HOLY_RAIN, 3,
                HolyRainAction.DESC, squad -> squad.getHeroClass() == HeroClass.DRUID));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.RESURRECT, 3,
                ResurrectAction.DESC, squad -> squad.getHeroClass() == HeroClass.DRUID));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.FIREBALL, 3,
                FireballAction.DESC, squad -> squad.getHeroClass() == HeroClass.WITCH));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.LIGHTNING, 3,
                LightningAction.DESC, squad -> squad.getHeroClass() == HeroClass.WITCH));

        LEARNABLE_SKILLS.add(new Skill(ActionEvaluation.TELEPORT, 3,
                TeleportAction.DESC, squad -> squad.getHeroClass() == HeroClass.SHADOW));


        for (Skill learnableSkill : LEARNABLE_SKILLS) {
            SKILLS_BY_ACTION_OR_EFFECT.put(learnableSkill.actionOrEffect, learnableSkill);
        }
    }

    public static void init() {
    }


    public static class Skill {
        private final Object actionOrEffect;
        private final int fromLevel;
        private String description;
        private Predicate<AbstractSquad> learnPredicate;
        private String name;
        private boolean passive;


        public Skill(Object actionOrEffect, int fromLevel, String description, Predicate<AbstractSquad> learnPredicate) {
            this.actionOrEffect = actionOrEffect;
            this.fromLevel = fromLevel;
            this.description = description;
            this.learnPredicate = learnPredicate;
            if (actionOrEffect instanceof ActionEvaluation) {
                ActionEvaluation actionEvaluation = (ActionEvaluation) actionOrEffect;
                name = actionEvaluation.name();
                actionEvaluation.learnable = this;
            } else if (actionOrEffect instanceof Class) {
                name = ((Class) actionOrEffect).getSimpleName();
                passive = true;
            } else {
                name =  actionOrEffect.getClass().getSimpleName();
            }

        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Object getActionOrEffect() {
            return actionOrEffect;
        }

        public boolean isPassive() {
            return passive;
        }

        public boolean canBeLearnedBy(AbstractSquad squad) {
            return !squad.learnedSkills.contains(this)
                    && squad.getExpLevel() >= fromLevel
                    && learnPredicate.evaluate(squad);
        }

        public void learn(AbstractSquad bySquad) {
            bySquad.learnedSkills.add(this);
            if (actionOrEffect instanceof Class) {
                Class effectClass = (Class) this.actionOrEffect;
                if (AbstractUnitEffect.class.isAssignableFrom(effectClass)) {
                    bySquad.effectManager.getOrCreate(effectClass).resetTickCounter();
                }
            }
            bySquad.getActionsController().forceTreeValidationFromThisNode();
        }

    }
}
