package conversion7.game.stages.world.unit.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Predicate;
import conversion7.engine.Gdxg;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.*;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.items.*;
import conversion7.game.stages.world.objects.buildings.*;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.DisableHealingEffect;
import conversion7.game.unit_classes.ufo.Archon;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;

public enum ActionEvaluation implements Comparable<ActionEvaluation> {

    BUILD_CAMP(BuildCampAction.class, 100,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuildCamp()
                    && Camp.couldBeBuiltOnCell(squad.getLastCell()),
            null),

    BUILD_IRON_FACTORY(BuildIronFactoryAction.class, 100,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuild()
                    && squad.team.getInventory().hasEnoughResources(ResourceCosts.getCost(IronFactory.class))
                    && squad.getLastCell().getObject(IronResourceObject.class) != null
                    && squad.getLastCell().getObject(IronFactory.class) == null,
            null),

    BUILD_URAN_FACTORY(BuildUranusFactoryAction.class, 100,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuild()
                    && squad.team.getInventory().hasEnoughResources(ResourceCosts.getCost(UranusFactory.class))
                    && squad.getLastCell().getObject(UranusResourceObject.class) != null
                    && squad.getLastCell().getObject(UranusFactory.class) == null,
            null),

    BUILD_SATELLITE(BuildSatelliteAction.class, 100,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuild()
                    && squad.team.getInventory().hasEnoughResources(ResourceCosts.getCost(CommunicationSatellite.class))
                    && squad.getLastCell().getObject(CommunicationSatellite.class) == null,
            null),

    BUILD_SCORPION(BuildScorpionAction.class, 50,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuild()
                    && squad.team.getInventory().hasEnoughResources(ResourceCosts.getCost(ScorpionObject.class))
                    && BuildingObject.canHaveBallistaBuiltOn(squad.getLastCell()),
            null),

    BUILD_BALLISTA(BuildBallistaAction.class, 50,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBuild()
                    && squad.team.getInventory().hasEnoughResources(ResourceCosts.getCost(BallistaObject.class))
                    && BuildingObject.canHaveBallistaBuiltOn(squad.getLastCell()),
            null),

    SELECT_SKILL(SelectSkillAction.class, 0,
            false, null, true, 0, 0,
            UsageScope.STRATEGY,
            squad -> squad.canSelectSkill(),
            null),
    SELECT_HERO_CLASS(SelectHeroClassAction.class, 0,
            false, null, true, 0, 0,
            UsageScope.STRATEGY,
            squad -> squad.canBecomeHero(),
            null),
    SELECT_UNIT_SPEC(SelectUnitSpecAction.class, 0,
            false, null, true, 0, 0,
            UsageScope.STRATEGY,
            squad -> squad.canSelectSpec(),
            null),

    CALL_TREE_DAD(CallTreeDadAction.class, 50,
            true, 2, true, 1, 0,
            UsageScope.BOTH,
            squad -> squad.isDruid(),
            (squad, cell) -> cell.canBeSeized()
                    && cell.getLandscape().hasForest()
    ),

    CHARGE(ChargeAction.class, 75,
            false, null, true, 1, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseCharge(),
            (squad, cell) -> !cell.isSeizedBy(squad)
                    && cell.isNeighborOf(squad.getLastCell())
                    && cell.hasSquad()
    ),

    CREATE_MOUNTAIN_DEBRIS(CreateMountainDebrisAction.class, 90,
            false, null, true, 1, 0,
            UsageScope.BOTH,
            squad -> squad.canBuildMountainDebris(),
            (squad, cell) -> cell.canBeSeized()
    ),

    MELEE_SWING(MeleeSwingAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseSwing(),
            null
    ),

    SPRINT(SprintAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BOTH,
            squad -> squad.canSprint(),
            null
    ),

    SLEEP(SleepAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseSleep(),
            (squad, cell) -> cell.isNeighborOf(squad.getLastCell())
                    && cell.hasSquad()
    ),

    HOOK(HookAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseHook(),
            (squad, cell) -> HookAction.canHookFrom(squad, cell)
    ),

//    RIDE(ChargeAction.class,
//            false, true, 0, 1,
//            squad -> squad.canUseRide(),
//            (squad, cell) -> cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),

//    MENTAL_WAVE(.class,
//            false, true, 0, 1,
//            squad -> squad.canMeleeAttack(),
//            (squad, cell) -> !cell.isSeizedBy(squad)
//                    && cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),
//
//    ANIMAL_TRANSFORMATION(ChargeAction.class,
//            false, true, 0, 1,
//            squad -> squad.canMeleeAttack(),
//            (squad, cell) -> !cell.isSeizedBy(squad)
//                    && cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),
//
//    STORM(ChargeAction.class,
//            false, true, 0, 1,
//            squad -> squad.canMeleeAttack(),
//            (squad, cell) -> !cell.isSeizedBy(squad)
//                    && cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),
//
//    RESURRECT(ChargeAction.class,
//            false, true, 0, 1,
//            squad -> squad.canMeleeAttack(),
//            (squad, cell) -> !cell.isSeizedBy(squad)
//                    && cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),
//
//    DISCORD(ChargeAction.class,
//            false, true, 0, 1,
//            squad -> squad.canMeleeAttack(),
//            (squad, cell) -> !cell.isSeizedBy(squad)
//                    && cell.isNeighborOf(squad.getCell())
//                    && cell.hasSquad()
//    ),

    CONCEALMENT(ConcealmentAction.class, 50,
            false, null, true, 1, 0,
            UsageScope.BOTH,
            squad -> squad.canUseConcealment(),
            null),

    FIRE(FireAction.class, 90,
            false, null, true, 0, 1,
            UsageScope.BOTH,
            squad -> squad.canUseFire(),
            (squad, cell) -> cell.canBeFired()),

    FIREBALL(FireballAction.class, 50,
            true, 2, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseFireball(),
            (squad, cell) -> {
                return true;
            }),

    LIGHTNING(LightningAction.class, 50,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseLightning(),
            (squad, cell) -> {
                return true;
            }),

    HOLY_RAIN(HolyRainAction.class, 50,
            true, 2, true, 1, 0,
            UsageScope.BOTH,
            squad -> squad.canUseHolyRain(),
            (squad, cell) -> {
                return true;
            }),

    EARTH_SHAKE(EarthShakeAction.class, 50,
            true, 2, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseEarthShake(),
            (squad, cell) -> true),

    APPLE_URANUS_BOMB(AppleUranusBombAction.class, 50,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canUseBomb(),
            (squad, cell) -> true),

    THROW_STONE(ThrowStoneAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canThrowStone(),
            (squad, cell) -> true),

    RANGE_ATTACK(RangeAttackAction.class, 47,
            false, null, false, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canRangeAttack(),
            (squad, cell) -> squad.canAttack(cell)),

    SCORPION_SHOT(ScorpionShotAction.class, 50,
            false, null, true, 0, 1,
            UsageScope.STRATEGY,
            squad -> squad.canScorpionShot(),
            (squad, cell) -> true),

    BALLISTA_SHOT(BallistaShotAction.class, 46,
            false, null, true, 0, 1,
            UsageScope.STRATEGY,
            squad -> squad.canBallistaShot(),
            (squad, cell) -> true),

    BALLISTA_JAVELIN_SHOT(BallistaJavelinShotAction.class, 45,
            false, null, true, 0, 1,
            UsageScope.STRATEGY,
            squad -> squad.canBallistaJavelinShot(),
            (squad, cell) -> true),

    BALLISTA_HOOK(BallistaHookAction.class, 45,
            false, null, true, 0, 1,
            UsageScope.STRATEGY,
            squad -> squad.canBallistaHook(),
            (squad, cell) -> HookAction.canHookFrom(squad, cell)),

    BALLISTA_SELF_SHOT(BallistaSelfShotAction.class, 45,
            false, null, true, 1, 1,
            UsageScope.STRATEGY,
            squad -> squad.canBallistaSelfShot(),
            (squad, cell) -> true),

    BALLISTA_VOLLEY_SHOT(BallistaVolleyShotAction.class, 45,
            false, null, true, 2, 1,
            UsageScope.STRATEGY,
            squad -> squad.canBallistaVolleyShot(),
            (squad, cell) -> true),

    HEALING(HealingAction.class, 60,
            false, null, true, 2, 1,
            UsageScope.BOTH,
            squad -> squad.canHeal(),
            (squad, cell) -> cell.hasSquad()
                    && cell.isNeighborOf(squad.getLastCell())
                    && squad.team.isAllyOf(cell.squad.team)
                    && !cell.squad.getEffectManager().containsEffect(DisableHealingEffect.class)),
    HYPNOTIC_BEAM(HypnoticBeamAction.class, 50,
            true, null, true, 2, 1,
            UsageScope.STRATEGY,
            squad -> squad.isArchon(),
            (squad, cell) -> cell.hasSquad()
                    && squad.team != cell.squad.team
                    && cell.squad.team.isHumanAiTribe()),
    DELIVER_SATELLITE_TO_ORBIT(DeliverSatelliteToOrbitAction.class, 50,
            false, null, true, 2, 0,
            UsageScope.STRATEGY,
            squad -> /*squad.isArchon()*/false,
            null),

    DISCORD(DiscordAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            (squad) -> true,
            (squad, cell) -> cell.hasSquad()
                    && !squad.team.isAllyOf(cell.squad.team)
                    && cell.isNeighborOf(squad.getLastCell())),

    MELEE_ATTACK(MeleeAttackAction.class, 25,
            false, null, false, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canMeleeAttack(),
            (squad, cell) -> squad.canAttack(cell)
                    && cell.isNeighborOf(squad.getLastCell())
    ),

    MOVE(MoveAction.class, 25,
            false, null, false, 1, 0,
            UsageScope.BOTH,
            squad -> squad.canMove(),
            (squad, cell) -> {
                if (!cell.isNeighborOf(squad.getLastCell())) {
                    return false;
                }
                return !cell.hasSquad()
                        && cell.hasFreeMainSlot()
                        && Gdxg.getAreaViewer().getSelectedCell() != cell;
            }),
    RESURRECT(ResurrectAction.class, 50,
            true, 2, true, 1, 0,
            UsageScope.STRATEGY,
            squad -> squad.canResurrect(),
            (squad, cell) -> {
                return cell.hasFreeMainSlot()
                        && ResurrectAction.hasSquadForResurrect(cell, squad)
                        ;
            }),

    TELEPORT(TeleportAction.class, 50,
            false, null, true, AbstractSquad.START_MOVE_AP, 0,
            UsageScope.BOTH,
            squad -> squad.canTeleport(),
            (squad, cell) -> {
                return cell.hasFreeMainSlot()
                        && Gdxg.getAreaViewer().getSelectedCell() != cell;
            }),
    PROVOKE(ProvokeAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canProvoke(),
            (squad, cell) -> cell.hasSquad()
                    && cell.isNeighborOf(squad.getLastCell())
                    && squad.isEnemyWith(cell.squad.unit)),
    PLACE_TRAP(PlaceTrapAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.STRATEGY,
            squad -> squad.canPlaceTrap(),
            (squad, cell) -> cell.isNeighborOf(squad.getLastCell())
                    && TrapObject.canSeize(cell)),
//    CONCENTRATION(ConcentrationAction.class, 90,
//            false, null, true, 0, 1,
//            squad -> squad.canConcentrate(),
//            null),

    RELAX(RelaxAction.class, 0,
            false, null, true, 0, 0,
            UsageScope.STRATEGY,
            squad -> squad.canSkipTurn(),
            null),

    RITUAL(RitualAction2.class, 50,
            false, null, true, 1, 0,
            UsageScope.STRATEGY,
            squad -> squad.canDoRitual2(),
            null),

    STUNNING(StunningAction.class, 75,
            false, null, true, 0, 1,
            UsageScope.BATTLE,
            squad -> squad.canStun(),
            (squad, cell) -> cell.hasSquad()
                    && cell.isNeighborOf(squad.getLastCell())
                    && squad.isEnemyWith(cell.squad.unit)),

    SCARE_ANIMAL(ScareAnimalAction.class, 60,
            false, null, false, 0, 1,
            UsageScope.BOTH,
            squad -> squad.canScareAnimal(),
            (squad, cell) -> cell.hasSquad()
                    && cell.isNeighborOf(squad.getLastCell())
                    && cell.squad.isAnimal()),

//    INCREASE_ANIMAL_SPAWN_CHANCE(IncreaseAnimalSpawnChanceAction.class, 100,
//            false, null, true, 1, 0,
//            squad -> squad.canChangeAnimalSpawn()
//            ,
//            null),

    CAPTURE_UNIT(CaptureUnitAction.class, 60,
            true, null, true, 0, 1,
            UsageScope.BOTH,
            squad -> squad.canCaptureUnit(),
            (squad, cell) -> cell.hasSquad()
                    && cell.squad.team != null && cell.squad.team != squad.team),

//    CONTROL_UNIT(ControlUnitAction.class, 65,
//            true, 2, true, 0, 1,
//            squad -> squad.canControlUnit(),
//            (squad, cell) -> cell.hasSquad()
////                    && cell.isNeighborOf(squad.getCell())
//                    && cell.squad.team != squad.team
//                    && !ControlUnitAction.doesControl(squad.unit, cell.squad.unit)
////                    && squad.hasEnoughPowerToControl(cell.squad.unit)
//    ),

//    PATHFINDER(PathfinderAction.class,
//            Unit::canUsePathfinder,
//            null),

//    CRITICAL_DAMAGE_CHANCE_BOOST(CriticalDamageChanceBoostAction.class, 90,
//            false, null, true, 0, 1,
//            squad -> squad.canDoCriticalDamageChanceBoost(),
//            (squad, cell) -> cell.hasSquad()
//                    && cell.squad.team != null
//                    && cell.squad.team == squad.team),

    CREATE_TOTEM(CreateTotemAction.class, 75,
            false, null, true, 1, 0,
            UsageScope.STRATEGY,
            squad -> squad.canCreateTotem()
            , (squad, cell) -> squad.canCreateTotemOn(cell));

    public static ActionEvaluation[] valuesOrdered = ActionEvalComparator.sortEnum();

    static {
        UnitSkills.init();
    }

    private final int moveApCost;
    private final int attackApCost;
    public BiFunction<AbstractSquad, Cell, Boolean> testOwnerVsTargetCell;
    public Class<? extends AbstractAreaObjectAction> classImpl;
    public UnitSkills.Skill learnable;
    private UsageScope usageScope;
    private Predicate<AbstractSquad> testOwner;
    private Integer reqLevel;
    private int order;
    private boolean superAbility;
    private boolean standardConsumingFromExecutor;

    /**
     * @param reqLevel
     * @param standardConsumingFromExecutor use false if AP is consumed within action logic
     * @param testOwner                     can unit execute action?
     * @param testOwnerVsCellTarget         can unit execute action on that cell?
     *                                      It's used in AbstractWorldTargetableAction#calculateAcceptableCells() to highlight acceptable cells for player action.
     */
    ActionEvaluation(Class<? extends AbstractAreaObjectAction> classImpl
            , int order
            , boolean superAbility
            , Integer reqLevel
            , boolean standardConsumingFromExecutor
            , int moveApCost
            , int attackApCost
            , UsageScope usageScope
            , Predicate<AbstractSquad> testOwner
            , BiFunction<AbstractSquad, Cell, Boolean> testOwnerVsCellTarget) {
        this.reqLevel = reqLevel;
        this.classImpl = classImpl;
        this.order = order;
        this.superAbility = superAbility;
        this.standardConsumingFromExecutor = standardConsumingFromExecutor;
        this.moveApCost = moveApCost;
        this.attackApCost = attackApCost;
        this.usageScope = usageScope;
        this.testOwner = testOwner;
        this.testOwnerVsTargetCell = testOwnerVsCellTarget;
        AbstractAreaObjectAction.ACTION_EVAL_BY_CLASS.put(classImpl, this);
    }

    private static boolean hasAllyUnitOnCell(AbstractSquad squad, Cell cell) {
        return cell.hasSquad() && cell.squad.team.isAllyOf(squad.team);
    }

    public boolean isSuperAbility() {
        return superAbility;
    }

    public int getMoveApCost() {
        return moveApCost;
    }

    public int getAttackApCost() {
        return attackApCost;
    }

    public String getApCostHint() {
        if (moveApCost > 0 && attackApCost > 0) {
            return "AP Cost: " + moveApCost + " Move, " + attackApCost + " Attack";
        } else if (moveApCost > 0) {
            return "AP Cost: " + moveApCost + " Move";
        } else if (attackApCost > 0) {
            return "AP Cost: " + attackApCost + " Attack";
        } else {
            return "No AP Cost";
        }
    }

    public boolean isStandardConsumingFromExecutor() {
        return standardConsumingFromExecutor;
    }

    public int getOrder() {
        return order;
    }

    public String getLevelReqHint() {
        return reqLevel == null ? "" : "Required exp level: " + (reqLevel + 1);
    }

    public boolean isImportant() {
        return UnitActionMappings.IMPORTANT_ACTIONS.contains(this);
    }

    public boolean testSquadAp(AbstractSquad squad) {
        boolean attackBlocked = attackApCost > 0 && squad.isScared();
        return !attackBlocked && squad.getMoveAp() >= moveApCost && squad.getAttackAp() >= attackApCost;
    }

    public boolean evaluateOwner(AbstractSquad squad) {
        if (ActionEvaluation.UnitActionMappings.isEnabledForUnit(squad.unit)) {
            try {
                boolean act = testSquadAp(squad)
                        && testOwner.evaluate(squad)
                        && (!superAbility || squad.hasSuperAbilityReady())
                        && (learnable == null || squad.learnedSkills.contains(learnable))
                        /*&& testReqLvl(squad)*/
                        && testUsageScope(squad);
                return act;
            } catch (Throwable e) {
                Gdxg.core.addError(e);
            }
        }
        return false;
    }

    private boolean testUsageScope(AbstractSquad squad) {
        if (squad.team.world.isBattleActive()) {
            return usageScope == UsageScope.BOTH || usageScope == UsageScope.BATTLE;
        } else {
            return usageScope == UsageScope.BOTH || usageScope == UsageScope.STRATEGY;
        }
    }

    private boolean testReqLvl(AbstractSquad squad) {
        return reqLevel == null || squad.getExpLevel() >= reqLevel;
    }

    public boolean testMeVsTargetFull(AbstractSquad squad, Cell cell) {
        if (!evaluateOwner(squad)) {
            return false;
        }
        if (testOwnerVsTargetCell != null) {
            return testOwnerVsTargetCell.apply(squad, cell);
        }
        return true;
    }

    public enum UsageScope {
        STRATEGY, BATTLE, BOTH
    }

    public static class UnitActionMappings {
        public static final ObjectSet<ActionEvaluation> IMPORTANT_ACTIONS = new ObjectSet<>();
        public static final ObjectMap<Class, Array<ActionEvaluation>> ACTIONS_BY_UNIT_CLASS = new ObjectMap<>();

        static {
            IMPORTANT_ACTIONS.add(SELECT_HERO_CLASS);
            IMPORTANT_ACTIONS.add(SELECT_UNIT_SPEC);
            IMPORTANT_ACTIONS.add(SELECT_SKILL);
            IMPORTANT_ACTIONS.add(DELIVER_SATELLITE_TO_ORBIT);

            Assert.assertTrue(PackageReflectedConstants.ALL_UNIT_CLASSES.size > 0);
            for (Class<? extends Unit> unitClass : PackageReflectedConstants.ALL_UNIT_CLASSES) {
                ACTIONS_BY_UNIT_CLASS.put(unitClass, new Array<>());
            }

            for (Class<? extends Unit> unitClass :
                    PackageReflectedConstants.ALL_UNIT_CLASSES) {
                Array<ActionEvaluation> actions = ACTIONS_BY_UNIT_CLASS.get(unitClass);
                actions.add(MOVE);
                actions.add(MELEE_ATTACK);
                actions.add(RANGE_ATTACK);
//                actions.add(CONCENTRATION);
                actions.add(APPLE_URANUS_BOMB);
                actions.add(THROW_STONE);
                actions.add(BUILD_SCORPION);
                actions.add(BUILD_BALLISTA);
                actions.add(SCORPION_SHOT);
                actions.add(BALLISTA_SHOT);
                actions.add(BALLISTA_JAVELIN_SHOT);
                actions.add(BALLISTA_HOOK);
                actions.add(BALLISTA_SELF_SHOT);
                actions.add(BALLISTA_VOLLEY_SHOT);
                actions.add(DELIVER_SATELLITE_TO_ORBIT);
            }

            for (Class<? extends Unit> unitClass :
                    PackageReflectedConstants.HUMAN_UNIT_CLASSES) {
                Array<ActionEvaluation> actions = ACTIONS_BY_UNIT_CLASS.get(unitClass);
                actions.add(CREATE_TOTEM);
                actions.add(CREATE_MOUNTAIN_DEBRIS);
                actions.add(CAPTURE_UNIT);
                actions.add(CALL_TREE_DAD);
//                actions.add(INCREASE_ANIMAL_SPAWN_CHANCE);
                actions.add(SCARE_ANIMAL);
//                actions.add(CONTROL_UNIT);
                actions.add(STUNNING);
                actions.add(RITUAL);
                actions.add(PROVOKE);
                actions.add(DISCORD);
                actions.add(HEALING);
                actions.add(RESURRECT);
                actions.add(TELEPORT);
//                actions.add(EARTH_SHAKE);
                actions.add(HOLY_RAIN);
//                actions.add(LIGHTNING);
                actions.add(FIREBALL);
                actions.add(FIRE);
                actions.add(CONCEALMENT);
                actions.add(CHARGE);
                actions.add(MELEE_SWING);
                actions.add(SPRINT);
                actions.add(SLEEP);
                actions.add(HOOK);
                //            actions.add(ActionEvaluation.RIDE);
                //            actions.add(ActionEvaluation.MENTAL_WAVE);
                //            actions.add(ActionEvaluation.ANIMAL_TRANSFORMATION);
                //            actions.add(ActionEvaluation.STORM);
                //            actions.add(ActionEvaluation.RESURRECT);
                //            actions.add(ActionEvaluation.DISCORD);
                actions.add(SELECT_HERO_CLASS);
                actions.add(BUILD_CAMP);
            }

            Array<ActionEvaluation> actions = ACTIONS_BY_UNIT_CLASS.get(Archon.class);
            actions.add(CREATE_TOTEM);
            actions.add(CAPTURE_UNIT);
//            actions.add(CONTROL_UNIT);
            actions.add(BUILD_CAMP);
            actions.add(BUILD_IRON_FACTORY);
            actions.add(BUILD_URAN_FACTORY);
            actions.add(BUILD_SATELLITE);
        }

        // bad design
        @Deprecated
        public static boolean isEnabledForUnit(Unit unit) {
            if (unit.isAnimal()) {
                return ACTIONS_BY_UNIT_CLASS.containsKey(unit.getGameClass());
            }
            return true;
        }
    }

    public static class ActionEvalComparator implements Comparator<ActionEvaluation> {
        public static final ActionEvalComparator instance = new ActionEvalComparator();

        public static ActionEvaluation[] sortEnum() {
            ActionEvaluation[] evaluations = values();
            evaluations = Arrays.copyOf(evaluations, evaluations.length);
            Arrays.sort(evaluations, instance);
            return evaluations;
        }

        public int compare(ActionEvaluation o1, ActionEvaluation o2) {
            return Integer.compare(o1.getOrder(), o2.getOrder());
        }
    }

    public static class AreaObjectActionComparator implements Comparator<AbstractAreaObjectAction> {
        public static final AreaObjectActionComparator instance = new AreaObjectActionComparator();

        @Override
        public int compare(AbstractAreaObjectAction o1, AbstractAreaObjectAction o2) {
            return Integer.compare(o1.actionEvaluation.getOrder(), o2.actionEvaluation.getOrder());
        }
    }
}
