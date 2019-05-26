package conversion7.game.stages.world.objects.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.engine.ai_new.base.AiEntity;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.BattleSystem;
import conversion7.engine.artemis.ui.UnitUltIndicatorSystem;
import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem;
import conversion7.engine.artemis.ui.float_lbl.UnitFloatingStatusBatch;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3dWith2dActor;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.engine.validators.NodeValidator;
import conversion7.game.Assets;
import conversion7.game.GameError;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.*;
import conversion7.game.stages.world.ai_deprecated.tasks.single.AbstractSquadTaskSingle;
import conversion7.game.stages.world.elements.ElementType;
import conversion7.game.stages.world.elements.Soul;
import conversion7.game.stages.world.elements.SoulType;
import conversion7.game.stages.world.elements.SpiritType;
import conversion7.game.stages.world.gods.AbstractGod;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.TeamCraftInventory;
import conversion7.game.stages.world.inventory.items.MentalGeneratorItem;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.ClothesItem;
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.landscape.BrezenhamLine;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.*;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.UnitActionsValidator;
import conversion7.game.stages.world.objects.actions.items.*;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.composite.SandWorm;
import conversion7.game.stages.world.objects.controllers.InventoryValidator;
import conversion7.game.stages.world.objects.controllers.UnitCellValidator;
import conversion7.game.stages.world.objects.controllers.UnitEndStepValidator;
import conversion7.game.stages.world.objects.controllers.UnitsController;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.stages.world.objects.totem.HealingTotem;
import conversion7.game.stages.world.objects.totem.ViewDistanceTotem;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.events.ReproductionHintEvent;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.unit.*;
import conversion7.game.stages.world.unit.actions.UnitSkills;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;
import conversion7.game.stages.world.unit.effects.UnitParamTypeEffectTotal;
import conversion7.game.stages.world.unit.effects.items.*;
import conversion7.game.stages.world.unit.effects.items.spec.AgileGuyEffect;
import conversion7.game.stages.world.unit.effects.items.spec.BigGuyEffect;
import conversion7.game.stages.world.unit.effects.items.spec.FastGuyEffect;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;
import conversion7.game.stages.world.unit.hero_classes.SpecClass;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.ui.world.UnitInWorldHintPanel;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.animals.TreeDad;
import conversion7.game.unit_classes.ufo.AbstractUfoUnit;
import conversion7.game.unit_classes.ufo.Archon;
import conversion7.game.unit_classes.ufo.BaalBoss;
import conversion7.game.unit_classes.ufo.Illuminat;
import org.slf4j.Logger;
import org.testng.Assert;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static conversion7.game.stages.world.team.Team.getJoinChance;
import static org.testng.Assert.assertNotNull;

public abstract class AbstractSquad extends AreaObject implements AiEntity, AreaObjectDetailsButton {

    @Deprecated
    public static final int UNITS_AMOUNT_LIMIT = 8;
    public static final Vector3 IN_WORLD_PANEL_SHIFT = MathUtils.toEngineCoords(new Vector3(-0.25f, 0, 1.35f));
    public static final float ACTOR_SCALE = 0.07f;
    public static final float ACTOR_Z = 0.84f;
    public static final float ACTOR_BOX_HEIGHT = 0.5f * ACTOR_Z;
    public static final int START_MOVE_AP = 3;
    public static final int BOG_MOVE_AP = 1;
    public static final int START_ATTACK_AP = 1;
    public static final int USE_ITEM_AP = 1;
    public static final int SUPER_ABIL_REQ_LVL = 3;
    public static final int BASE_DODGE_PERC = 5;
    public static final int SURROUNDED_DODGE_PERC = 20;
    public static final int RELAX_DODGE_PERC = BASE_DODGE_PERC;
    public static final int MIN_POWER = UnitClassConstants.BASE_DMG * 2;
    private static final Logger LOG = Utils.getLoggerForClass();
    public Unit unit;
    public UnitEquipment equipment;
    public UnitEffectManager effectManager;
    public Soul soul;
    public AbstractGod myGod;
    public int experience;
    public int experienceOnStep;
    public boolean willDieOfAge;
    public SpiritType spiritType;
    public ElementType elementType;
    public boolean ignoreUpdateActionPointsAfterAttack;
    public boolean positiveSoulCondition;
    public UnitCellValidator unitCellValidator;
    public boolean skipTurn;
    public boolean nextExpToTeamExp = true;
    public int relax;
    public ObjectSet<UnitSkills.Skill> learnedSkills = new ObjectSet<>();
    public WorldBattle.SavedUnitStats savedStats;
    @Deprecated
    public int archonHypnoCharges;
    /** Bigger = more chance to be chosen as target */
    public float relativeTargetValue;
    protected NodeValidator stealthValidator;
    protected AbstractSquadTaskSingle activeTask = null;
    protected Array<AbstractSquadTaskSingle> tasksSet = new Array<>(false, 4);
    protected Array<AbstractSquadTaskSingle> _proceededTasksSet = new Array<>();
    protected UnitsController unitsController;
    SceneNode3dWith2dActor hintNode;
    private UnitEndStepValidator stepEndController;
    private AbstractSquadTaskSingle lastExecutedTask = null;
    private boolean chasingCancelled = false;
    private List<AiTask> aiTasks = new ArrayList<>();
    private boolean squadInitialized;
    private String hint;
    private boolean aiEnabled = true;
    private int hadAiActAtStep = -1;
    private int moveAp;
    private int attackAp;
    private UnitParametersObservable mainParams;
    private int expLevel;
    private int inspirationPoints;
    private int madeAttacks;
    private Unit mother;
    private boolean madeRitualOnStep;
    private boolean deathValidationForNextHurtActive = true;
    private float resistanceToDeathChance;
    private UnitAge age;
    private int ageStep;
    private int mana;
    private ObjectSet<String> bloodlines = new ObjectSet<>();
    private boolean sawUfo;
    private InventoryValidator inventoryController;
    private HeroClass heroClass;
    private Array<Predicate<AbstractSquad>> aiGoals = new Array<>();
    private SpecClass spec;
    private boolean boss;
    private ObjectSet<InventoryItemStaticParams> knowsAboutBetterItems = new ObjectSet<>();
    private boolean quest;

    public AbstractSquad(Cell cell, Team team) {
        super(cell, team);
    }

    /** @param onLevel starts from 0 as field expLevel */
    public static int getExpOnLevel(int onLevel) {
        int exp = 0;
        for (int currentLevel = 1; currentLevel <= onLevel; currentLevel++) {
            exp += currentLevel * Unit.BASE_EXP_FOR_LEVEL;
        }
        return exp;
    }

    public static float getObjectsPower(Array<? extends AbstractSquad> objects) {
        float totalPower = 0;
        for (AbstractSquad object : objects) {
            totalPower += object.getPowerValue();
        }
        return totalPower;
    }

    @Override
    public boolean isCellMainSlotObject() {
        return true;
    }

    public InventoryValidator getInventoryController() {
        return inventoryController;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public UnitEffectManager getEffectManager() {
        return effectManager;
    }

    public UnitEquipment getEquipment() {
        return equipment;
    }

    public int getMeleeWeaponHitChance() {
        return equipment.getMeleeHitChance();
    }

    public int getTemperatureWithEquipment() {
        return getLastCell().getTemperature() + equipment.getHeat();
    }

    public float getExperiencePerSteps() {
        return experience / (ageStep + 1);
    }

    public boolean isHero() {
        return effectManager.containsEffect(HeroUnitEffect.class);
    }

    public boolean isScared() {
        return effectManager.containsEffect(ScaredEffect.class);
    }

    public int getPowerPercent() {
        return (int) (((float) getCurrentPower()) / getMaxPower() * 100);
    }

    public int getInspirationPercent() {
        return Math.round(((float) inspirationPoints / Unit.INSPIRATION_POINTS_MAX) * 100);
    }

    public UnitParameters getMainParams() {
        return mainParams;
    }

    public String getExceptionalStatusHint() {
        StringJoiner joiner = new StringJoiner(",");
        if (isHero()) {
            joiner.add("Hero " + heroClass);
        }
        if (isShaman()) {
            joiner.add("Shaman");
        }
        if (boss) {
            joiner.add("Boss");

        }
        if (quest) {
            joiner.add("Quest");
        }
        return joiner.toString();
    }

    public int getRangeWeaponHitChance() {
        return equipment.getRangeHitChance();
    }

    public float getAdjacentSoulsMlt() {
        int positiveSouls = 0;
        int negativeSouls = 0;
        for (AbstractSquad adjSquad : getSquadsAround()) {
            if (adjSquad.team == team) {
                SoulType.Effect effect = adjSquad.soul.getType().getEffectOn(soul.getType());
                if (effect == SoulType.Effect.POSITIVE) {
                    positiveSouls++;
                } else if (effect == SoulType.Effect.NEGATIVE) {
                    negativeSouls++;
                } else {
                    positiveSouls++;
                    negativeSouls++;
                }
            }
        }

        return positiveSouls / (float) negativeSouls;
    }

    public boolean isPregnant() {
        return effectManager.getEffectRaw(ChildbearingEffect.class) != null;
    }

    /** Max = 8 adj cells */
    public int getAdjacentSoulsBalance() {
        int positiveSouls = 0;
        int negativeSouls = 0;
        for (AbstractSquad adjSquad : getSquadsAround()) {
            if (adjSquad.team == team) {
                SoulType.Effect effect = adjSquad.soul.getType().getEffectOn(this.soul.getType());
                if (effect == SoulType.Effect.POSITIVE) {
                    positiveSouls++;
                } else if (effect == SoulType.Effect.NEGATIVE) {
                    negativeSouls++;
                }
            }
        }
        return positiveSouls - negativeSouls;
    }

    public int getExpLevel() {
        return expLevel;
    }

    public int getExpLevelUi() {
        return expLevel + 1;
    }

    public int getCurrentLevelExperience() {
        return experience - getExpOnLevel(expLevel);
    }

    public int getExpLevelProgressPercent() {
        return (int) ((float) getCurrentLevelExperience() / getExperienceForNextLevel() * 100);
    }

    public int getExperienceForNextLevel() {
        int expForLevel1 = getExpOnLevel(expLevel);
        int expForLevel2 = getExpOnLevel(expLevel + 1);
        return expForLevel2 - expForLevel1;
    }

    public int getExperienceOnNextLevel() {
        return getExpOnLevel(expLevel + 1);
    }

    public int getExperienceForNextLevelLeft() {
        return getExpOnLevel(expLevel + 1) - experience;
    }

    public UnitInWorldHintPanel getUnitInWorldHintPanel() {
        return unitInWorldHintPanel;
    }

    public boolean isShaman() {
        return effectManager.containsEffect(ShamanUnitEffect.class);
    }

    public void setShaman(AbstractGod god) {
        becomeShaman();
        myGod = god;
    }

    public boolean isQuest() {
        return quest;
    }

    public ObjectSet<InventoryItemStaticParams> getKnowsAboutBetterItems() {
        return knowsAboutBetterItems;
    }

    public Array<Predicate<AbstractSquad>> getAiGoals() {
        return aiGoals;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(HeroClass newClass) {
        this.heroClass = newClass;
        if (newClass == HeroClass.HODOR) {
            power.updateMaxValue((int) (power.getMaxValue() * BigGuyEffect.BOOST_MLT));
        }
        if (newClass == HeroClass.SHADOW) {
//            effectManager.getOrCreate(GrinderEffect.class);
//            effectManager.getOrCreate(VengeanceHitEffect.class);
//            effectManager.getOrCreate(EvadeEffect.class);
        }
        actionsController.forceTreeValidationFromThisNode();
    }

    public int getMoveAp() {
        return moveAp;
    }

    public void setMoveAp(int value) {
        moveAp = value;
        getActionsController().invalidate();
        validate();
        refreshUiPanelInWorld();
    }

    public int getAttackAp() {
        return attackAp;
    }

    public int getTotalArmor() {
        return getFortArmor() + getEquipArmor();
    }

    public int getFortArmor() {
        FortificationEffect fortificationEffect = effectManager.getEffect(FortificationEffect.class);
        if (fortificationEffect == null) {
            return 0;
        }
        return fortificationEffect.getArmor();
    }

    public int getEquipArmor() {
        if (equipment.hasClothes()) {
            return equipment.getClothesItem().params.getArmor();
        }
        return 0;
    }

    public UnitAge getAge() {
        return age;
    }

    public void setAge(UnitAge age) {
        if (this.age != age) {
            this.age = age;
            if (age != UnitAge.YOUNG) {
                power.updateMaxValue(UnitClassConstants.AGE_UP_POWER_ADD);
            }
            refreshUiPanelInWorld();
            newAgeCalculations();
        }
    }

    public ObjectSet<String> getBloodlines() {
        return bloodlines;
    }

    public int getMana() {
        return mana;
    }

    public String getAgeName() {
        return getAge().name();
    }

    public int getMadeAttacks() {
        return madeAttacks;
    }

    public void setMadeAttacks(int madeAttacks) {
        this.madeAttacks = madeAttacks;
    }

    public int getAgeStep() {
        return ageStep;
    }

    public void setAgeStep(int step) {
        ageStep = step;
        if (ageStep < UnitAge.YOUNG.getEndsAtAgeStep()) {
            setAge(UnitAge.YOUNG);
        } else if (ageStep < UnitAge.ADULT.getEndsAtAgeStep()) {
            setAge(UnitAge.ADULT);
        } else if (ageStep < UnitAge.MATURE.getEndsAtAgeStep()) {
            setAge(UnitAge.MATURE);
        } else {
            setAge(UnitAge.OLD);
        }

        if (ageStep >= Unit.DIES_AT_AGE_STEP
                && MathUtils.testPercentChance(10)
                && !GdxgConstants.DEVELOPER_MODE) {
            willDieOfAge = true;
            batchFloatingStatusLines.addLine("Will die of age");
        }
    }

    public boolean isDeathValidationForNextHurtActive() {
        return deathValidationForNextHurtActive;
    }

    public void setDeathValidationForNextHurtActive(boolean deathValidationForNextHurtActive) {
        this.deathValidationForNextHurtActive = deathValidationForNextHurtActive;
    }

    public float getResistanceToDeathChance() {
        return resistanceToDeathChance;
    }

    public void setResistanceToDeathChance(float resistanceToDeathChance) {
        this.resistanceToDeathChance = resistanceToDeathChance;
    }

    public WorldSquad getHumanSquad() {
        return (WorldSquad) this;
    }

    public boolean isMale() {
        return getUnit().gender;
    }

    public boolean isFemale() {
        return !isMale();
    }

    public boolean getGender() {
        return getUnit().gender;
    }

    public void setGender(boolean gender) {
        getUnit().gender = gender;
    }

    public Unit getMother() {
        return mother;
    }

    public void setMother(Unit mother) {
        this.mother = mother;
    }

    public TextureRegion getClassIcon() {
        return Assets.getUnitClassIcon(getUnit().getClass());
    }

    public Image getClassIconImage() {
        return new Image(getClassIcon());
    }

    public float getManaPercent() {
        return ((float) mana / (float) Unit.MANA_MAX) * 100;
    }

    public int getAgePercent() {
        return (int) (((float) ageStep / Unit.DIES_AT_AGE_STEP) * 100);
    }

    public int getClassLevel() {
        return unit.classStandard.level;
    }

    public String getGenderUi() {
        return getGender() ? "Male" : "Female";
    }

    public Class<? extends Unit> getUnitClass() {
        return getUnit().getGameClass();
    }

    public String getGameClassName() {
        return getUnit().getGameClassName();
    }

    public String getBloodlineDescription() {
        StringBuilder builder = new StringBuilder()
                .append("Race root parents (race founders) in unit's bloodline: \n \n");
        for (String name : getBloodlines()) {
            builder.append(name).append("\n");
        }
        return builder.toString();
    }

    public boolean isArchon() {
        return getUnitClass() == Archon.class;
    }

    public TeamCraftInventory getCraftInventory() {
        return team.getCraftInventory();
    }

    public BasicInventory getInventory() {
        return team.getInventory();
    }

    public Unit getUnit() {
        return unit;
    }

    @Deprecated
    public void setUnit(Unit unit) {
        getUnitsController().setUnitAndValidate(unit);
    }

    public int getHadAiActAtStep() {
        return hadAiActAtStep;
    }

    public List<AiTask> getAiTasks() {
        return aiTasks;
    }

    @Override
    public String getHint() {
        if (unit == null) {
            return super.getHint();
        } else {
            if (hint == null) {
                hint = getName() + super.getHint();
            }
            return hint;
        }
    }

    // review usage
    @Deprecated
    public float getPowerWithNeighborSupport() {
        float totalPower = getPowerValue();
        for (Cell neighborCell : getLastCell().getCellsAround()) {
            if (neighborCell.isSeizedByTeam(team)) {
                totalPower += neighborCell.getSquad().getPowerValue();
            }
        }
        return totalPower;
    }

    public float getPowerValue() {
        return getCurrentPower();
    }

    public UnitEndStepValidator getStepEndController() {
        return stepEndController;
    }

    public AbstractSquadTaskSingle getActiveTask() {
        return activeTask;
    }

    public void setActiveTask(AbstractSquadTaskSingle currentObjectTask) {
        this.activeTask = currentObjectTask;
        if (LOG.isDebugEnabled()) LOG.debug("new task set: " + currentObjectTask);
    }

    public boolean isAiEnabled() {
        return aiEnabled && GdxgConstants.AREA_OBJECT_AI;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public boolean isSquadInitialized() {
        return squadInitialized;
    }

    public boolean isChasingCancelled() {
        return chasingCancelled;
    }

    public void setChasingCancelled(boolean b) {
        this.chasingCancelled = b;
    }

    public NodeValidator getStealthValidator() {
        return stealthValidator;
    }

    public AbstractSquadTaskSingle getLastExecutedTask() {
        return lastExecutedTask;
    }

    @Override
    public int getViewRadius(boolean dayTimeEffect) {
        int radius = super.getViewRadius(dayTimeEffect);
        if (unit != null && effectManager != null && effectManager.containsEffect(IncreaseViewRadiusEffect.class)) {
            radius += IncreaseViewRadiusEffect.INCREASE_RADIUS;
        }

        Cell lastCell = getLastCell();
        if (lastCell != null) {
            if (lastCell.getEffectiveTotem(ViewDistanceTotem.class, team) != null) {
                radius += ViewDistanceTotem.BOOST;
            }

//            if (lastCell.hasWinter()) {
//                radius = Math.round(radius / 2f);
//            }
        }

        if (radius > World.MAX_VIEW_RADIUS) {
            radius = World.MAX_VIEW_RADIUS;
        }
        if (radius < 1) {
            radius = 1;
        }

        return radius;
    }

    public Array<Cell> getCells(int fromR, int toR) {
        return getLastCell().getCellsAround(fromR, toR, new Array<>());
    }

    @Override
    public String getDetailsButtonLabel() {
        return "Unit & Inventory";
    }

    @Override
    public ClickListener getDetailsClickListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.clientUi.getInventoryWindow().show((AbstractSquad) thiz);
            }
        };
    }

    public Team getPrevTeam() {
        UnderControlEffect underControlEffect = getEffectManager().getEffect(UnderControlEffect.class);
        if (underControlEffect != null) {
            return underControlEffect.previousTeam;
        }
        return null;
    }

    public String getCurrentApHint() {
        return moveAp + " Move, " + attackAp + " Attack";
    }

    public boolean isDruid() {
        return heroClass == HeroClass.DRUID;
    }

    public boolean isIlluminat() {
        return unit.getGameClass() == Illuminat.class;
    }

    public boolean isUfo() {
        return AbstractUfoUnit.class.isAssignableFrom(unit.getGameClass());
    }

    public boolean isBaalBoss() {
        return unit.getGameClass() == BaalBoss.class;
    }

    @Override
    public String getFullName() {
        return unit.getGameClassName() + " " + getName();
    }

    public Array<UnitSkills.Skill> getAvailableSkillsToLearn() {
        Array<UnitSkills.Skill> skills = new Array<>();
        for (UnitSkills.Skill skill : UnitSkills.LEARNABLE_SKILLS) {
            if (skill.canBeLearnedBy(this)) {
                skills.add(skill);
            }
        }
        return skills;
    }

    public boolean isDisabled() {
        return effectManager.containsEffect(StunnedEffect.class)
                || effectManager.containsEffect(SleptEffect.class);
    }

    public void setInspirationPoints(int inspirationPoints) {
        this.inspirationPoints = inspirationPoints;
        getActionsController().invalidate();
        validate();
        validateInspirationStatus();
    }

    public void setMadeRitualOnStep(boolean madeRitualOnStep) {
        if (madeRitualOnStep) {
            Assert.assertFalse(this.madeRitualOnStep, "Ritual has been already executed on this step!");
        }
        this.madeRitualOnStep = madeRitualOnStep;
    }

    public void setSpec(SpecClass spec) {
        this.spec = spec;
        effectManager.getOrCreate(spec.getEffectClass());
        if (spec == SpecClass.BIG) {
            power.updateMaxValue((int) (power.getMaxValue() * BigGuyEffect.BOOST_MLT));
        }
        actionsController.forceTreeValidationFromThisNode();
        refreshUiPanelInWorld();
    }

    public void setBossUnit() {
        boss = true;
        power.updateMaxValue((int) (power.getMaxValue() * 1.4f));
        refreshUiPanelInWorld();
    }

    public void setQuestUnit() {
        quest = true;
        power.updateMaxValue((int) (power.getMaxValue() * 1.4f));
        refreshUiPanelInWorld();
    }

    @Override
    public void init() {
        unitCellValidator = new UnitCellValidator(this);
        equipment = new UnitEquipment(this);
        effectManager = new UnitEffectManager(this);
        power = new Power2(this);
        unitInWorldHintPanel = new UnitInWorldHintPanel(this);
        setAgeStep(0);
        soul = UnitFertilizer2.soulQueue.pop();
        soul.setUnit(this);
        spiritType = SpiritType.getRandom();
        elementType = ElementType.getRandom();
        direction = new Direction(this);
        direction.setValue(MathUtils.random(0, 7));
        super.init();
        resetActionPoints();

        inventoryController = new InventoryValidator(this);
        rootValidator.registerChildValidator(inventoryController);
        inventoryController.registerChildValidator(team.getInventory());
        stepEndController = new UnitEndStepValidator(this);

        unitsController = new UnitsController(this);
        rootValidator.registerChildValidator(unitsController);

        stealthValidator = new NodeValidator() {
            @Override
            public void validate() {
                refreshStealth();
            }
        };
        rootValidator.registerChildValidator(stealthValidator);
        rootValidator.registerChildValidator(unitCellValidator);

        actionsController.registerAppendedValidator(new UnitActionsValidator(actionsController, this));
        squadInitialized = true;
        getLastCell().setRefreshedInView(false);
    }

    /** @param healWip no heal if full HP */
    public void heal(int healWip) {
        if (effectManager.containsEffect(DisableHealingEffect.class)) {
            batchFloatingStatusLines.addLine("Healing disabled");
            return;
        }

        if (LOG.isDebugEnabled()) LOG.debug("heal: " + this);
        if (getLastCell().getEffectiveTotem(HealingTotem.class, team) != null) {
            healWip += HealingTotem.BOOST;
        }
        World world = team.world;
        if (!world.isDaytime() && (soul.getType().hasHealBonus())) {
            healWip += SoulType.NIGHT_HEAL_BONUS;
//            batchFloatingStatusLines.addLine("Night heal bonus");
        }

        int needToHeal = getMaxPower() - getCurrentPower();
        int finalHealOn = Math.min(needToHeal, healWip);
        if (finalHealOn > 0) {
            power.setCurrentValue(power.getCurrentValue() + finalHealOn);
            cell.addFloatLabel("Healing: " + finalHealOn, Color.GREEN, true);
            refreshUiPanelInWorld();
        }
    }

    /** Assume units were sorted */
    public boolean willEat() {
        return getLastCell().getFood() >= Unit.HEALTHY_CELL_FOOD_MIN;
    }

    /** Assume units were sorted */
    public boolean willDrink() {
        return getLastCell().getWater() > Unit.HEALTHY_CELL_WATER_MIN;
    }

    public boolean hasPerfectConditions() {
        return hasHealthyTemperature() && getLastCell().hasEnoughResourcesForUnit();
    }

    public boolean hasHealthyTemperature() {
        return getTemperatureWithEquipment() >= Unit.HEALTHY_TEMPERATURE_MIN;
    }

    public void updateMoveAp(int onValue) {
        if (onValue != 0) {
            setMoveAp(getMoveAp() + onValue);
        }
    }

    public void updateAttackAp(int onValue) {
        if (onValue != 0) {
            attackAp += onValue;
            getActionsController().invalidate();
            validate();
            refreshUiPanelInWorld();

            if (attackAp <= 0) {
                updateMoveAp(-moveAp);
                if (team.world.isBattleActive()) {
                    Gdxg.core.artemis.getSystem(BattleSystem.class).nextSquad();
                }
            }
        }
    }

    public void updateActionPointsAfterAttack(int onValue) {
        if (ignoreUpdateActionPointsAfterAttack) {
            ignoreUpdateActionPointsAfterAttack = false;
        } else {
            updateAttackAp(onValue);
        }
    }

    private void newAgeCalculations() {
        if (age.getLevel() > UnitAge.YOUNG.getLevel()) {
            batchFloatingStatusLines.addImportantLine(age.name());
        }
        // TODO enable MaturityAuraEffect after review
//        if (getAge().getLevel() >= MaturityAuraEffect.STARTS_FROM_LEVEL && gender) {
//            if (getAge().getLevel() >= MaturityAuraEffect.ENDS_FROM_LEVEL) {
//                effectManager.removeEffectIfExist(MaturityAuraEffect.class);
//            } else {
//                if (!effectManager.containsEffect(MaturityAuraEffect.class)) {
//                    effectManager.addEffect(new MaturityAuraEffect());
//                }
//            }
//        }
        if (getAge().getLevel() >= HealingAuraEffect.STARTS_FROM_LEVEL && !getGender()) {
            if (getAge().getLevel() >= HealingAuraEffect.ENDS_FROM_LEVEL) {
                effectManager.removeEffectIfExist(HealingAuraEffect.class);
            } else {
                effectManager.getOrCreate(HealingAuraEffect.class);
            }
        }
//        if (getAge().getLevel() >= TeachAuraEffect.STARTS_FROM_LEVEL && !getGender() &&
//                !effectManager.containsEffect(TeachAuraEffect.class)) {
//            effectManager.addEffect(new TeachAuraEffect());
//        }
        if (getAge().getLevel() >= CommanderAuraEffect.STARTS_FROM.getLevel() && getGender() &&
                !effectManager.containsEffect(CommanderAuraEffect.class)) {
            effectManager.getOrCreate(CommanderAuraEffect.class);
        }

        for (AbstractSquad neibSquad : getLastCell().getObjectsAround(AbstractSquad.class)) {
            neibSquad.getActionsController().invalidate();
            neibSquad.validate();
        }
        if (!team.scenario.sawFertilizeHint && team.isHumanPlayer()
                && hasFertilizationAge()) {
            team.addEventNotification(new ReproductionHintEvent(this));
            team.scenario.sawFertilizeHint = true;
        }
    }

    public boolean canBuildCamp() {
        return team.getTeamSkillsManager().getSkill(SkillType.BUILD_CAMP).isFullyLearned()
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(Camp.class));
    }

    public boolean canMove() {
        return moveAp > 0 && !isDisabled();
    }

    public boolean canMeleeAttack() {
        return attackAp > 0 && !isDisabled();
    }

    public boolean canUseFireball() {
        return true;
    }

    public boolean canUseHolyRain() {
        return true;
    }

    public boolean canUseEarthShake() {
        return heroClass == HeroClass.HODOR;
    }

    public boolean canUseLightning() {
        return true;
    }

    public boolean canRangeAttack() {
        return equipment.hasRangeWeap()
                && team.getInventory().hasResourcesToShot(equipment.getRangeWeaponItem());
    }

    public boolean canFertilize() {
        return isMale() && hasFertilizationAge() && isHuman();
    }

    public boolean canBeFertilized() {
        return isFemale() && hasFertilizationAge() && isHuman()
                && !effectManager.containsEffect(ChildbearingEffect.class);
    }

    public boolean canCreateTotem() {
        return isShaman()
                && team.getTeamSkillsManager().getSkill(SkillType.TOTEMS).isFullyLearned();
    }

    public boolean canCreateTotemOn(Cell cell) {
        return AbstractTotem.canBeCreatedOn(cell);

    }

    public boolean hasFertilizationAge() {
        return getAge().getLevel() == UnitFertilizer2.AGE_FROM ||
                getAge().getLevel() == UnitFertilizer2.AGE_FROM + 1;
    }

    public void updateExperience(int newExp) {
        updateExperience(newExp, null);
    }

    private boolean hasNeighborAllyWithCommanderEffect() {
        for (Cell cell : getLastCell().getCellsAround()) {
            if (cell.hasSquad() && cell.squad.team.isAllyOf(team) &&
                    effectManager.containsEffect(CommanderAuraEffect.class)) {
                return true;
            }
        }
        return false;
    }

    public void updateExperience(int newExp, String label) {
        FastAsserts.assertMoreThanOrEqual(newExp, 0);
        if (newExp < 0) {
            throw new GdxRuntimeException("Exp cant be less 0: " + newExp);
        }

        String expLabel = "";
        Integer addExp = null;
        if (hasNeighborAllyWithCommanderEffect()) {
            addExp = MathUtils.getPercentValue(CommanderAuraEffect.EXP_ADD_PERCENT, newExp);
            newExp += addExp;
        }

        newExp += getAdjacentSoulsBalance();
        if (newExp <= 0) {
            return;
        }

        if (label == null) {
            expLabel += "EXP: " + UiUtils.getNumberWithSign(newExp);
        } else {
            expLabel += label + ": " + UiUtils.getNumberWithSign(newExp);
        }
        if (addExp != null) {
            expLabel += " (bonus " + addExp + ")";
        }

        batchFloatingStatusLines.addLine(expLabel);

        experience += newExp;
        experienceOnStep += newExp;
        updateInspirationPoints(newExp);

        while (experience >= getExperienceOnNextLevel()) {
            newLevel();
        }

        if (nextExpToTeamExp) {
            getTeam().updateEvolutionExp(newExp * Unit.EVOLUTION_EXP_PER_UNIT_EXP);
        } else {
            nextExpToTeamExp = true;
        }
    }

    private void newLevel() {
        expLevel++;
        int powerAdd = HeroClass.HODOR == heroClass ? UnitClassConstants.LVL_UP_POWER_ADD_TANK :
                UnitClassConstants.LVL_UP_POWER_ADD;
        power.updateMaxValue(powerAdd);
        getActionsController().forceTreeValidationFromThisNode();

        refreshUiPanelInWorld();
        batchFloatingStatusLines.addImportantLine("New level: " + getExpLevelUi());
        if (team.isHumanPlayer()) {
            team.world.savePlayerUnitProgress(experience);
            AudioPlayer.playSingleSnare();
        }
    }

    public boolean hasSuperAbilityReady() {
        return inspirationPoints >= Unit.INSPIRATION_POINTS_MAX;
    }

    private void updateInspirationPoints(int newVal) {
        if (hasSuperAbilityReady()) {
            return;
        }
        if (isShaman()) {
            newVal *= ShamanUnitEffect.INSPIRATION_BOOST;
        }
        inspirationPoints += newVal;
        validateInspirationStatus();
    }

    public void validateInspirationStatus() {
        if (inspirationPoints > Unit.INSPIRATION_POINTS_MAX) {
            inspirationPoints = Unit.INSPIRATION_POINTS_MAX;
        }
        boolean visibleInUi = unitInWorldHintPanel.getUnitIndicatorIconsPanel().getUltIndicator().isVisible();
        if (hasSuperAbilityReady()) {
            inspirationPoints = Unit.INSPIRATION_POINTS_MAX;
            if (team.isHumanPlayer() && !visibleInUi) {
                UnitUltIndicatorSystem.components.create(entityId).squad = this;
            }
        } else {
            if (team.isHumanPlayer() && visibleInUi) {
                UnitUltIndicatorSystem.components.create(entityId).squad = this;
            }
        }
    }

    /** Returns true if unit was killed by given damage - death could be triggered for this unit. */
    public boolean hurtBy(int damage, AreaObject hurtBy) {
        int wipDmg = damage;

        if (team.world.isChaosPeriod()) {
//            wipDmg = MathUtils.random(1, wipDmg);
//            if (wipDmg != damage) {
//                batchFloatingStatusLines.addLine("Chaos!");
//            }
        }

        FastAsserts.assertMoreThan(wipDmg, 0);

        return super.hurtBy(wipDmg, hurtBy);
    }

    public void damageEquip(int dmgToArmor) {
        if (equipment.getClothesItem().updateHp(-dmgToArmor)) {
            equipment.destroyClothes();
        }
    }

    @Override
    public boolean canDodge() {
        return true;
    }

    public boolean checkDefeated() {
        if (willDieOfAge) {
            WorldSquad.killUnit(this);
            return true;
        } else {
            return super.checkDefeated();
        }
    }

    public void updateFortArmor(int onVal) {
        FortificationEffect fortificationEffect = effectManager.getOrCreate(FortificationEffect.class);
        fortificationEffect.updateArmor(onVal);
    }

    public void resetActionPoints() {

        boolean onBog = false;
        try {
            onBog = getLastCell().getLandscape().hasBog();
        } catch (NullPointerException e) {
            // not placed yet
        }
        int newMoveAp = 0;
        if (onBog) {
            newMoveAp = BOG_MOVE_AP;
            cell.addFloatLabel("In bog", Color.LIGHT_GRAY, true);
        } else {
            if (unit.getGameClass() == TreeDad.class) {
                newMoveAp = BOG_MOVE_AP;
            } else {
                newMoveAp = START_MOVE_AP;
            }
        }

        if (effectManager.containsEffect(WeakeningEffect.class)) {
            if (UnitFloatingStatusBatch.SHOW_EFFECT_LABELS) {
                cell.addFloatLabel(WeakeningEffect.class.getSimpleName(), Color.ORANGE);
            }
            newMoveAp -= WeakeningEffect.AP_MINUS;
        }
        if (effectManager.containsEffect(FastGuyEffect.class)) {
            newMoveAp++;
        }
        if (effectManager.containsEffect(BigGuyEffect.class)) {
            newMoveAp--;
        }
        if (newMoveAp < 0) {
            newMoveAp = 0;
        }
        moveAp = newMoveAp;

        int newAttackAp = START_ATTACK_AP;
        if (effectManager.containsEffect(AgileGuyEffect.class)) {
            newAttackAp++;
        }
        if (newAttackAp < 0) {
            newAttackAp = 0;
        }
        attackAp = newAttackAp;
        getLastCell().setRefreshedInView(false);
    }

    public void calculateResistanceToDeathChanceInActiveBattle() {
        resistanceToDeathChance = mainParams.get(UnitParameterType.HEALTH) / (float) getMaxHealth() / 3f;
        resistanceToDeathChance *= resistanceToDeathChance;
    }

    public void moveExtraApToArmor() {
        int extraAp = moveAp + attackAp;
        if (extraAp > 0) {
            FortificationEffect fortificationEffect = effectManager.getOrCreate(FortificationEffect.class);
            fortificationEffect.updateArmor(FortificationEffect.ARMOR_PER_STEP);
        }
    }

    public Unit initParams(UnitParameters parameters) {
        mainParams = new UnitParametersObservable(this.getUnit());
        mainParams.copyFrom(parameters);

        int unitStartStr = unit.classStandard.getBasePower();
        if (team.isHumanPlayer()) {
            int powerBalance = team.world.settings.powerBalance;
            if (powerBalance != 0) {
                unitStartStr = unitStartStr + (powerBalance * WorldSettings.POWER_BALANCE_MLT);
            }
        }
        if (isAnimal()) {
            unitStartStr = (int) (unitStartStr * Power2.ANIMAL_POWER_MLT);
            int powerRadius = (int) Math.ceil(unitStartStr * 0.2f);
            int add = MathUtils.random(-powerRadius * 2, powerRadius);
            if (add != 0) {
                unitStartStr += add;
            }
        }
        if (unitStartStr < MIN_POWER) {
            unitStartStr = MIN_POWER;
        }
        power.setMaxValue(unitStartStr);
        power.setCurrentValue(unitStartStr);

        return this.getUnit();
    }

    public void moveOn(Cell targetCell) {
        Cell oldCell = this.cell;
        if (oldCell == null) {
            LOG.error(String.valueOf(removedOnCell));
            throw new GameError("");
        }
        moveOn(oldCell, targetCell);
    }

    public void moveOn(Cell oldCell, Cell targetCell) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(getId() + "\nmove from: " + oldCell + "\nto:" + targetCell);
        }
        seizeCell(targetCell);

        getEffectManager().removeEffectIfExist(FortificationEffect.class);

        unitCellValidator.invalidate();
        inventoryController.invalidate();
        if (power.freeNextMove) {
            power.freeNextMove = false;
        } else {
            updateMoveAp(-1);
        }

        Gdxg.getAreaViewer().reselectionIfMoved(this);

        // counter attack old
//        for (Cell adjCell : targetCell.getCellsAround()) {
//            if (adjCell.hasSquad()
//                    && adjCell.getSquad().effectManager.containsEffect(ConcentrationEffect.class)) {
//                boolean willMakeCounterHit = false;
//                if (adjCell.getSquad().team.isEnemyOf(this.team)) {
//                    willMakeCounterHit = true;
//                }
//                if (MathUtils.testChance(1, 1000)) {
//                    willMakeCounterHit = true;
//                    batchFloatingStatusLines.addLine("Oh I am sorry!");
//                    batchFloatingStatusLines.flush(Color.ORANGE);
//                }
//                if (willMakeCounterHit && this.cell != null) {
//                    adjCell.getSquad().effectManager.removeEffectIfExist(ConcentrationEffect.class);
//                    adjCell.getSquad().batchFloatingStatusLines.addLine("Concentration: counter attack");
//                    adjCell.getSquad().meleeAttack(this);
//                }
//            }
//        }

        if (cell != null && team.world.doesPlayerSeeCell(cell)) {
            if (MathUtils.random()) {
                AudioPlayer.play("fx\\2\\footstep.mp3").setVolume(0.4f);
            } else {
                AudioPlayer.play("fx\\2\\tdin.mp3").setVolume(0.15f);
            }
        }
        direction.setValue(Direction.getDirBy(oldCell, targetCell));
        moveBody(oldCell, targetCell);

        if (cell != null) {
            SandWorm.wakeUpSandWormIfCan(cell);
            CellRandomEvents.apply(this);
        }

        checkDefeated();
    }

    // 1st squad is attacked
    // else all objects with hp on cell
    public AttackCalculation initAttack(Cell targCell) {
        direction.setValue(Direction.getDirBy(cell, targCell));
        return new AttackCalculation(this, targCell);
    }

    public void meleeAttack(AreaObject target) {
        AttackCalculation attackCalculation = getMeleeAttackCalc(target);
        attackCalculation.start();
    }

    public AttackCalculation getMeleeAttackCalc(AreaObject target) {
        return initAttack(target.cell).setMeleeAttack(true);
    }

    public void rangeAttack(AreaObject target) {
        initAttack(target.cell).setMeleeAttack(false).start();
    }

    public void attackByBestWeapon(AreaObject target) {
        if (canRangeAttack() && power.getRangeDamage() > power.getMeleeDamage()) {
            rangeAttack(target);
        } else {
            meleeAttack(target);
        }
    }

    public void switchBattleEffects(boolean act) {
        IncreaseBattleParamsEffect battleParamsUnitEffect =
                effectManager.getEffect(IncreaseBattleParamsEffect.class);
        if (battleParamsUnitEffect != null) {
            battleParamsUnitEffect.setEnabled(act);
        }
    }

    public void switchToDefendingMode(boolean active) {
        if (!destroyed) {
            return;
        }

        switchBattleEffects(active);

        FortificationEffect fortificationEffect = effectManager.getEffect(FortificationEffect.class);
        if (fortificationEffect != null) {
            fortificationEffect.setEnabled(active);
        }

        if (active) {
            boolean hasHill = getLastCell().getLandscape().hasHill();
            boolean hasForest = getLastCell().getLandscape().hasForest();
            if (hasHill) {
                effectManager.addEffect(new HillDefenceEffect());
            }
            if (hasForest) {
                effectManager.addEffect(new ForestDefenceEffect());
            }
        } else {
            effectManager.removeEffectIfExist(HillDefenceEffect.class);
            effectManager.removeEffectIfExist(ForestDefenceEffect.class);
        }
    }

    public void switchToAttackingMode(boolean act) {
        if (!destroyed) {
            return;
        }

        switchBattleEffects(act);
    }

    public boolean canStun() {
        return
//                team.getTeamSkillsManager().getSkill(SkillType.STUNNING).isFullyLearned()
                heroClass == HeroClass.HODOR
//                && isMale()
//                && StunningAction.testAge(getAge())
//                        && !effectManager.containsEffect(StunActionCooldownEffect.class)
                ;
    }

    public boolean canHeal() {
        return true;
//        return heroClass == HeroClass.DRUID
//                isFemale()
//                && HealingAction.testAge(getAge())
//                && !effectManager.containsEffect(HealActionCooldownEffect.class)
//                && team.getTeamSkillsManager().getSkill(SkillType.DRUID).isFullyLearned()
//                ;
    }

    public boolean canControlUnit() {
        return
                (heroClass == HeroClass.WITCH
//                isHumanlike()

//                && (isShaman()
                        || (isArchon() && getInventory().getItem(MentalGeneratorItem.class) != null))
                        && hasSuperAbilityReady()
                ;
    }

    public boolean isEnemyWith(Unit anotherUnit) {
        boolean controlEnabled = false;
        if (controlEnabled) {
            if (ControlUnitAction.doesControl(this.getUnit(), anotherUnit) ||
                    ControlUnitAction.doesControl(anotherUnit, this.getUnit())) {
                return false;
            }

            UnderControlEffect underControlEffect = effectManager.getEffect(UnderControlEffect.class);
            if (underControlEffect != null) {
                return underControlEffect.controller.squad.isEnemyWith(anotherUnit);
            }

            underControlEffect = anotherUnit.squad.effectManager.getEffect(UnderControlEffect.class);
            if (underControlEffect != null) {
                return underControlEffect.controller.squad.isEnemyWith(this.getUnit());
            }
        }

        return team.isEnemyOf(anotherUnit.squad.team);
    }

    public void releaseControlEffects() {
        UnderControlEffect underControlEffect;
        underControlEffect = getEffectManager().getEffect(UnderControlEffect.class);
        if (underControlEffect != null) {
            ControlUnitAction.releaseControlByAnotherUnit(underControlEffect);
        }

        ControlUnitsEffect controlUnitsEffect = getEffectManager().getEffect(ControlUnitsEffect.class);
        if (controlUnitsEffect != null) {
            for (Unit animalUnderControl : controlUnitsEffect.underControl) {
                underControlEffect = animalUnderControl.squad.getEffectManager().getEffect(UnderControlEffect.class);
                Assert.assertTrue(underControlEffect != null, "Unit is not underControl");
                Assert.assertTrue(underControlEffect.controller.squad == this, "Unit is underControl by another unit");
                ControlUnitAction.releaseControlByAnotherUnit(underControlEffect);
            }
        }
    }

    public boolean canCaptureUnit() {
        return (hasSuperAbilityReady() && heroClass == HeroClass.WITCH)
//                || isArchon()
                ;
    }

    public boolean canUsePathfinder() {
        return isHumanlike();
    }

    public boolean canDoCriticalDamageChanceBoost() {
        return hasNeighborAlly();
    }

    private boolean hasNeighborAlly() {
        return Stream.of(getLastCell().getObjectsAround(AbstractSquad.class).toArray())
                .filter((anotherSquad) -> anotherSquad.team == team)
                .findAny()
                .isPresent();
    }

    public void calculateTemperatureHungerThirstEffects() {
        UnitEffectManager effectManager = getEffectManager();
        if (getTemperatureWithEquipment() >= Unit.HEALTHY_TEMPERATURE_MIN || cell.hasCamp()) {
            effectManager.removeEffectIfExist(ColdEffect.class);
        } else {
            effectManager.getOrCreate(ColdEffect.class);
        }

        if (cell.hasEnoughUnitFood()) {
            effectManager.removeEffectIfExist(HungerEffect.class);
        } else {
            effectManager.getOrCreate(HungerEffect.class);
        }

        if (getLastCell().hasEnoughUnitWater()) {
            effectManager.removeEffectIfExist(ThirstEffect.class);
        } else {
            effectManager.getOrCreate(ThirstEffect.class);
        }

        if (hasPerfectConditions()
            /*&& team.teamSkillsManager.getSkill(SkillType.DRUID).isFullyLearned()*/) {
            effectManager.getOrCreate(SelfHealingEffect.class);
        } else {
            effectManager.removeEffectIfExist(SelfHealingEffect.class);
        }
    }

    public boolean hasMortalWound() {
        return power.getCurrentValue() <= 0;
    }

    public boolean hasEnoughPowerToScare(Unit target) {
        float myPower = getCurrentPower()
                * ScareAnimalAction.HUMAN_POWER_MOD_FOR_SUCCES_SCARING;
        if (isShaman()) {
            myPower *= ShamanUnitEffect.SCARE_BOOST;
        }
        return myPower >= target.squad.getCurrentPower();
    }

    public boolean canScareAnimal() {
        return true;
    }

    public boolean canUseFire() {
        return !isAnimal() && team.getTeamSkillsManager().getSkill(SkillType.FIRE).isLearnStarted();
    }

    public void fireForest(Cell input) {
        AreaObject.create(input, this, BurningForest.class);
        input.refreshViewer();
    }

    public void updateMana(int addMana) {
        mana += addMana;
        if (mana > Unit.MANA_MAX) {
            mana = Unit.MANA_MAX;
        }
        getActionsController().invalidate();
        validate();
    }

    public boolean canProvoke() {
        return heroClass == HeroClass.HODOR
//                || (isHuman() && team.teamSkillsManager.getSkill(SkillType.PROVOCATION).isLearnStarted())
//                ProvokeAction.testAge(getAge())
//                && isMale()
//                && team.getTeamSkillsManager().getSkill(SkillType.PROVOCATION).isFullyLearned()
                ;
    }

    public boolean canDisableHealing() {
        return true;
    }

    public boolean canUseConcealment() {
        return heroClass == HeroClass.SHADOW;
//                hasSuperAbilityReady()
//                && isHero()
//                && team.getTeamSkillsManager().getSkill(SkillType.PRIMITIVE_CLOTHING).isFullyLearned();
    }

    public boolean canDoRitual2() {
        return team.isHumanPlayer()
                && isShaman()
                && hasSuperAbilityReady()
                && team.teamSkillsManager.getSkill(SkillType.RITUAL).isFullyLearned()
                && PrimalExperienceJewel.canBeCreatedOn(getLastCell());
    }

    public void setBloodlineByParents(Unit mother, Unit father) {
        bloodlines.clear();
        bloodlines.addAll(mother.squad.getBloodlines());
        bloodlines.addAll(father.squad.getBloodlines());
    }

    public void initRaceFounderBloodline() {
        bloodlines.add(name);
    }

    public boolean canBecomeHero() {
        return !isHero()
                && !isAnimal()
                && expLevel >= 3
                && team.canHaveMoreHeroes();
    }

    public boolean canSelectSpec() {
        return !hasSpec()
                && expLevel >= 2;
    }

    private boolean hasSpec() {
        return spec != null;
    }

    public boolean canUseItem(AbstractInventoryItem item, SkillType requiredSkill) {
        boolean hasSkill = requiredSkill == null
                || getTeam().getTeamSkillsManager().getSkill(requiredSkill).isLearnStarted();
        if (isUfo()) {
            hasSkill = true;
        }
        return hasSkill;
    }

    public boolean canPickUpItem(AbstractInventoryItem item) {
//        if (item.getClass() == RadioactiveIsotopeItem.class) {
//            return getUnit().getGameClass() == Archon.class;
//        }
        return true;
    }

    public boolean testScaredByUfo() {
        if (sawUfo) {
            return false;
        } else {
            sawUfo = true;
            return true;
        }
    }

    public void setPanic() {
        PanicEffect panicEffect = effectManager.getOrCreate(PanicEffect.class);
        panicEffect.resetTickCounter();
        if (team.world.activeTeam == team) {
            PanicEffect.tryApply(getUnit());
        }
    }

    public int getTotalParam(UnitParameterType type) {
        UnitParamTypeEffectTotal totalEffectMlt = effectManager.get(type);
        int total = MathUtils.multiplyOnPercent(mainParams.get(type) + totalEffectMlt.value, totalEffectMlt.percentValue);
//        total = (int) (total * (1 + AreaObject.PARAM_MLT_PER_LEVEL * expLevel));
        return total;
    }

    public void becomeShaman() {
        effectManager.addEffect(new ShamanUnitEffect());
        getActionsController().invalidate();
        validate();
        team.setShaman(this);
        updateMana(HolyRainAction.MANA_COST * 5);
        getUnitInWorldHintPanel().getUnitIndicatorIconsPanel().setShamanIndicator(true);
    }

    @Override
    public void addAiTask(AiTask aiTask) {
        aiTasks.add(aiTask);
    }

    public void refreshVisibleCells() {
        resetCellsVisibility();

        Array<Cell> viewRadiusCells = getLastCell().getCellsAround(0, getViewRadius(), new Array<>());
        Array<Cell> canSeeCellsIfLight = getCells(0, getViewRadius(false));
        for (Cell cell : canSeeCellsIfLight) {
            if (cell.hasLight() && !viewRadiusCells.contains(cell, true)) {
                viewRadiusCells.add(cell);
            }
        }

        for (Cell viewRadiusCell : viewRadiusCells) {
            Array<Cell> lineOfSight = BrezenhamLine.getCellsLine(viewRadiusCell, getLastCell());
            for (int i = lineOfSight.size - 2; i >= 0; i--) {
                Cell cell = lineOfSight.get(i);
                if (!visibleCellsAround.contains(cell, true)) {
                    visibleCellsAround.add(cell);
                }
                if (cell.isBlockingSight()) {
                    break;
                }
            }
        }

        for (Cell cell : visibleCellsAround) {
            visibleCellsWithMyCell.add(cell);
            cell.visibleBySquads.add(this);
        }
        visibleCellsWithMyCell.add(getLastCell());
        getLastCell().visibleBySquads.add(this);
    }

    private void resetCellsVisibility() {
        for (Cell cell : visibleCellsWithMyCell) {
            Assert.assertTrue(cell.visibleBySquads.removeValue(this, true));
        }
        visibleCellsAround.clear();
        visibleCellsWithMyCell.clear();
    }

    public void clearTasks() {
        tasksSet.clear();
        setActiveTask(null);
    }

    public void addTaskToWipSet(AbstractSquadTaskSingle objectTask) {
        tasksSet.add(objectTask);
    }

    public void updateMoral(int onValue) {
        UiLogger.addInfoLabel("Moral " + MathUtils.formatNumber(onValue));
        getMainParams().update(UnitParameterType.MORAL, onValue);
    }

    public boolean areUnitsWithHealthyTemperature() {
        return hasHealthyTemperature();
    }

    public void executeActiveTask() {
        if (isRemovedFromWorld()) {
            return;
        }
        if (activeTask != null) {
            lastExecutedTask = activeTask;
            LOG.info("{} executing task: \n{}", this, activeTask);
            if (activeTask.execute()) {
                setActiveTask(null);
            }
        }
    }

    @Deprecated
    public void ai() {
        Assert.fail("Deprecated ai");
        applyNewTaskFromWipSet();
    }

    protected void applyNewTaskFromWipSet() {
        AbstractSquadTaskSingle theHighestPriorityNewTask = proceedTaskQueueAndGetTheHighestPriorityTask();
        if (theHighestPriorityNewTask != null) { // got new tasks
            if (activeTask == null) {
                // and has no task
                setActiveTask(theHighestPriorityNewTask);
            } else if (activeTask.priority > theHighestPriorityNewTask.priority) {
                // and has task, but new one has more priority
                activeTask.cancel();
                setActiveTask(theHighestPriorityNewTask);
            }
        }
    }

    private AbstractSquadTaskSingle proceedTaskQueueAndGetTheHighestPriorityTask() {
        if (tasksSet.size > 0) {
            WorldThreadLocalSort.instance().sort(tasksSet, AbstractSquadTaskSingle.TASK_PRIORITY);
            AbstractSquadTaskSingle theHighestTask = tasksSet.get(0);
//            _proceededTasksSet.addAll(tasksSet);
            tasksSet.clear();
            return theHighestTask;
        } else {
            return null;
        }
    }

    public void addSnapshotLog(String logMain) {
        addSnapshotLog(logMain, "unit=" + unit);
    }

    public void refreshStealth() {
        clearVisibleAndVisibleBy();

        // test my stealth
        for (AbstractSquad squadSeesCell : new Array.ArrayIterator<>(this.getLastCell().visibleBySquads)) {
            squadSeesCell.testStealth(this);
        }

        // test stealth of those whom I could see
        for (Cell cell : visibleCellsAround) {
            if (cell.hasSquad()) {
                testStealth(cell.squad);
            } else {
                // other objects
                for (AreaObject object : cell.getObjectsOnCell()) {
                    if (object != cell.squad && object != this) {
                        visibleObjects.add(object);
                        object.visibleForObjects.add(this);
                    }
                }
            }
        }
    }

    /** Assumption - it should be impossible to stealth on neighbor cell, due to AI implementation difficulty for such case */
    private void testStealth(AbstractSquad squadUnderStealthTest) {
        if (GdxgConstants.isAlwaysStealthOnCheck()) {
            return;
        }

        if (squadUnderStealthTest != this) {
            // ConcealmentEffect
            if (squadUnderStealthTest.unit != null
                    && squadUnderStealthTest.getEffectManager().containsEffect(ConcealmentEffect.class)) {
                if (getLastCell().isNeighborOf(squadUnderStealthTest.getLastCell())) {
                    if (!team.isAllyOf(squadUnderStealthTest.team)) {
                        squadUnderStealthTest.unit.revealIfConcealed();
                    }
                } else {
                    if (visibleObjects.contains(squadUnderStealthTest, true)) {
                        visibleObjects.removeValue(squadUnderStealthTest, true);
                        squadUnderStealthTest.visibleForObjects.removeValue(this, true);
                    }
                }
                return;
            }

            // visible
            if (!visibleObjects.contains(squadUnderStealthTest, true)) {
                visibleObjects.add(squadUnderStealthTest);
                squadUnderStealthTest.visibleForObjects.add(this);
                this.team.meetsTeam(squadUnderStealthTest.team, this.cell);
                squadUnderStealthTest.team.meetsTeam(this.team, squadUnderStealthTest.cell);
            }
        }
    }

    public void revealIfConcealed() {
        batchFloatingStatusLines.addLine("Reveal");
        getEffectManager().removeEffectIfExist(ConcealmentEffect.class);
    }

    @Override
    public void seizeCell(Cell newCell) {
        if (cell != null && cell.squad == this) {
            freeCell();
        }
        super.seizeCell(newCell);

        if (cell != null) {
            cell.setSquad(this);
            captureCampIfExist();
            consumeCellItems();
            calculateObjectsAround();

            if (unit != null) {
                for (AbstractSquad adjSquad : getSquadsAround()) {
                    Archon.testContact(this.unit, adjSquad.unit);
                    Archon.testContact(adjSquad.unit, this.unit);
                    if (team.isEnemyOf(adjSquad.team)) {
                        if (adjSquad.effectManager.containsEffect(GrinderEffect.class)) {
                            showFloatingLabel("Under Grinder attack", Color.WHITE);
                            adjSquad.meleeAttack(this);
                        }
                    }
//                    else if (team == adjSquad.team) {
//                        SoulType.Effect effectOn = soul.getType().getEffectOn(adjSquad.soul.getType());
//                        if (shouldShowSoulEffectLabel(effectOn)) {
//                            cell.addFloatLabel("Soul Effect: " + effectOn.name(), effectOn.getColor(), true);
//                            adjSquad.getCell().addFloatLabel("Soul Effect: " + effectOn.name(), effectOn.getColor(), true);
//                        }
//                    }
                }
            }

            refreshVisibleCells();
            refreshStealth();
            if (team.isHumanPlayer() && team.world.initialized) {
                team.world.generateFaunaOnAndAround(cell.getArea());
            }
        }


    }

    public void showFloatingLabel(String txt, Color color) {
        batchFloatingStatusLines.start();
        batchFloatingStatusLines.addLine(txt);
        batchFloatingStatusLines.flush(color);
    }

    private boolean shouldShowSoulEffectLabel(SoulType.Effect effect) {
        return effect == SoulType.Effect.POSITIVE || effect == SoulType.Effect.NEGATIVE;
    }

    private void consumeCellItems() {
        if (cell != null) {
            PrimalExperienceJewel primalExperienceJewel = cell.getObject(PrimalExperienceJewel.class);
            if (primalExperienceJewel != null && !isAnimal()) {
                primalExperienceJewel.pickedBy(this);
            }
            SupplyContainer supplyContainer = cell.getObject(SupplyContainer.class);
            if (supplyContainer != null) {
                if (canOpenSupplyCont()) {
                    supplyContainer.pickedBy(this);
                } else {
                    supplyContainer.cell.addFloatLabel("Not enough technology to open", Color.ORANGE);
                }
            }
        }
    }

    private boolean canOpenSupplyCont() {
        return isUfo();
    }

    private void captureCampIfExist() {
        if (team.canCaptureCamp(getLastCell())) {
            getLastCell().camp.captureBy(this.team);
        }
    }

    @Override
    protected void calculateObjectsAround() {
        super.calculateObjectsAround();
        Array<AbstractSquad> allAdjAndMe = getSquadsAround();
        allAdjAndMe.add(this);
        for (AbstractSquad squad : allAdjAndMe) {
            if (squad.isSquadInitialized() && squad.cell != null) {
                squad.getRootValidator().forceTreeValidationFromRootNode();
            }
        }
    }

    @Override
    public void removeFromWorld() {
        removeObjectFromCell();
        resetCellsVisibility();
        getArea().removeSquad(this);
        if (!removedTemporary) {
            cell.lastDeadSquad = this;
        }
        freeCell();
        if (Gdxg.getAreaViewer().getSelectedSquad() == this) {
            Gdxg.getAreaViewer().deselectCell();
        }
        clearVisibleAndVisibleBy();
        validateView();

        removedTemporary = false;
    }

    public void freeCell() {
        if (getLastCell().ritual != null) {
            cancelRitual();
        }
        getLastCell().setSquad(null);
        super.seizeCell(null);
    }

    @Deprecated
    private void cancelRitual() {
        FloatingStatusOnCellSystem.scheduleMessage(getLastCell(), team, "Ritual was cancelled");
        getLastCell().ritual = null;
    }

    @Override
    public void validateView() {
        team.recalculateVisibleCellsPlayerTribeOnly();
        if (Gdxg.getAreaViewer() != null) {
            if (isRemovedFromWorld()) {
                assertNotNull(removedOnCell);
                removedOnCell.setRefreshedInView(false);
                for (Cell cell : removedOnCell.getCellsAroundToRadiusInclusively(getViewRadius())) {
                    cell.setRefreshedInView(false);
                }
            } else {
                for (Cell cell : getCells(0, getViewRadius(false))) {
                    cell.setRefreshedInView(false);
                }
            }
            super.validateView();
        }
    }

    /**
     * Bigger is better for me:
     * My power > other = ratio > 1
     * My power < other = ratio < 1
     */
    public float getMyRelativePowerRatioWith(AbstractSquad otherObject) {
        return (float) getCurrentPower() / otherObject.getCurrentPower();
    }

    public boolean hasMoreActualPowerThan(AbstractSquad otherObject) {
        return getCurrentPower() > otherObject.getCurrentPower();
    }

    public abstract boolean couldJoinToTeam(AreaObject targetToBeJoined);

    public boolean hasEnoughFood() {
        return getLastCell().hasEnoughUnitFood();
    }

    public boolean hasEnoughWater() {
        return getLastCell().hasEnoughUnitWater();
    }

    public boolean moveStepsTo(Cell target, int maxMoveSteps) {
        Array<PathData> path = FindPath.getPath(this.getLastCell(), target);
        if (path == null) {
            return true;
        }

        int moveSteps = Math.min(maxMoveSteps, path.size);
        boolean lastStep = false;
        if (path.size == moveSteps) {
            lastStep = true;
        }

        for (int step = 0; step < moveSteps; step++) {
            if (isAlive()) {
                AnimationSystem.lockAnimation(AnimationSystem.ANIM_DURATION);
                this.moveOn(path.get(step).cell);
            }
        }
        PoolManager.ARRAYS_POOL.free(path);

        return lastStep;
    }

    /** Returns true if target was reached or further path is unavailable */
    public boolean moveOneStepTo(Cell target) {
        if (LOG.isDebugEnabled()) LOG.debug(this + " moveOneStepTo " + target);
        Array<PathData> path = FindPath.getPath(this.getLastCell(), target);
        if (path == null) {
            return true;
        }

        boolean lastStep = false;
        if (path.size == 1) {
            lastStep = true;
        }
        this.moveOn(path.get(0).cell);
        PoolManager.ARRAYS_POOL.free(path);

        return lastStep;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor = PoolManager.ARMY_MODEL_POOL.obtain();
        sceneBody.addNode(modelActor);
        float boxHeight = 0.5f * AbstractSquad.ACTOR_Z;
        sceneBody.createBoundingBox(0.7f * AbstractSquad.ACTOR_Z, 0.7f * AbstractSquad.ACTOR_Z, boxHeight);
        sceneBody.boundBoxActor.translate(MathUtils.toEngineCoords(0, 0, boxHeight / 2));
        return sceneBody;
    }

    @Override
    protected void initActions() {
        super.initActions();
    }

    @Override
    public void showBody() {
        super.showBody();
        if (sceneBody == null) {
            return;
        }

        if (hintNode == null) {
            hintNode = new SceneNode3dWith2dActor(unitInWorldHintPanel);
            unitInWorldHintPanel.setVisible(true);
            sceneBody.addNode(hintNode);
            hintNode.translate(IN_WORLD_PANEL_SHIFT);
        }

        hintNode.setVisible(true);
    }

    @Override
    public void hideBody() {
        super.hideBody();
        if (hintNode != null) {
            hintNode.setVisible(false);
        }
    }

    @Override
    public void removeObjectFromCell() {
        super.removeObjectFromCell();
        if (Gdxg.core.areaViewer.selectedSquad == this) {
            Gdxg.core.areaViewer.selectedSquad = null;
        }
    }

    @Override
    public void clearBody() {
        super.clearBody();
        if (hintNode != null /*&& !removedTemporary*/) {
            hintNode.removeFromParent();
            hintNode = null;
        }
    }

    public boolean sees(AreaObject targetObject) {
        // TODO add also check that teams sees it (but limit for animal team - herds do not share visibility info)
        return visibleObjects.contains(targetObject, true);
    }

    public void destroyUnit() {
        LOG.info("defeat: " + unit);
        destroyed = true;
        getUnitsController().remove(unit);
        getEquipment().drop();
        soul.free();
    }

    public void hadAiActAt(int step) {
        hadAiActAtStep = step;
    }

    public boolean canAttack(AbstractSquad other) {
        if (other == null) {
            return false;
        }
        return !team.isAllyOf(other.team);
    }

//    public void ride(AnimalHerd animalHerd) {
//        ridingOn = animalHerd;
//        animalHerd.rider = this;
//    }
//
//    public boolean canUseRide() {
//        return false;
//    }

    public boolean canAttack(Cell cell) {
        if (cell.canBeAttacked()) {
            boolean allyOnCell = cell.hasSquad() && cell.squad.isAlive() && this.team.isAllyOf(cell.squad.team);
            return !allyOnCell && cell.hasObjectWithPower();
        }
        return false;
    }

    public boolean canSprint() {
//        if (heroClass == HeroClass.SHADOW) {
//            CooldownEffect cooldownEffect = effectManager.getEffect(CooldownEffect.class);
//            return cooldownEffect == null || !cooldownEffect.hasCooldown(CooldownEffect.Type.SPRINT);
//        }
        return true;
    }

    public boolean canUseSleep() {
        return true;
    }

    public boolean canUseHook() {
        return true;
    }

    public boolean canUseCharge() {
        return true;
//        return heroClass == HeroClass.HODOR;
    }

    public boolean canUseSwing() {
        return true;
    }

    public boolean canChangeAnimalSpawn() {
        return heroClass == HeroClass.DRUID
//                squad.isHumanlike()
//                && squad.isShaman()
//                && squad.getCell().getArea().getBaseSpawn() != null
//                && squad.getCell().getArea().getBaseSpawn().getSpawnChance() < AnimalSpawn.MAX_SPAWN_CHANCE
                ;
    }

    public boolean canBuild() {
        return isArchon();
    }

    public boolean canConcentrate() {
        return true;
    }

    public boolean canJoin(AbstractSquad squad) {
        return squad.team != this.team
                && !squad.team.isHumanPlayer()
                && !squad.team.isAnimalTeam()
                && !squad.team.isUfo()
                && !squad.team.isEnemyOf(this.team)
                && getJoinChance(this.team, squad.team) > 0;
    }

    public boolean canTeleport() {
        return true;
    }

    public boolean hasUltReady() {
        return hasSuperAbilityReady();
    }

    public boolean canResurrect() {
        return isDruid();
    }

    public boolean canPlaceTrap() {
        return heroClass == HeroClass.SHADOW;
    }

    public void addAiGoal(Predicate<AbstractSquad> predicate) {
        aiGoals.add(predicate);
    }

    public WorldSquad cloneUnit() {
        Cell couldBeSeizedNeighborCell = this.cell.getCouldBeSeizedNeighborCell();
        WorldSquad cloned = null;
        if (couldBeSeizedNeighborCell != null) {
            cloned = WorldSquad.create(this.unit.getGameClass(), this.team, couldBeSeizedNeighborCell);
            cloned.updateExperience(this.experience / 2, "Clone exp");
        }
        return cloned;
    }

    public boolean canSkipTurn() {
        return skipTurn == false && (moveAp > 0 || attackAp > 0);
    }

    @Override
    public boolean givesExpOnHurt() {
        return true;
    }

    public boolean hasActiveAction() {
        for (AbstractAreaObjectAction action : getActions().values()) {
            if (action.active) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBrainToTalk() {
        if (team == null || isAnimal()) {
            return false;
        }

        boolean hasSkill = isHuman() && team.teamSkillsManager.getSkill(SkillType.BRAIN).isLearnStarted();
        return hasSkill || isUfo();
    }

    public boolean canBuildMountainDebris() {
        return isDruid()
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(MountainDebris.class));
    }

    public boolean canUseBomb() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill;
        hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(AppleUranusBombAction.SKILLS_REQ);
        return hasSkill &&
                team.getInventory().hasEnoughResources(ResourceCosts.getCost(AppleUranusBombAction.class));
    }

    public boolean canThrowStone() {
        if (isAnimal()) {
            return false;
        }
        boolean hasSkill;
        hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(ThrowStoneAction.SKILLS_REQ);
        return hasSkill
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(ThrowStoneAction.class));
    }

    public boolean canScorpionShot() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(ScorpionShotAction.SKILLS_REQ);
        return hasSkill && cell.containsObject(ScorpionObject.class)
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(ScorpionShotAction.class));
    }

    public boolean canBallistaSelfShot() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(BallistaSelfShotAction.SKILLS_REQ);
        return hasSkill && cell.containsObject(BallistaObject.class);
    }

    public boolean canBallistaHook() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(BallistaJavelinShotAction.SKILLS_REQ);
        return hasSkill && cell.containsObject(BallistaObject.class);
    }

    public boolean canBallistaJavelinShot() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(BallistaJavelinShotAction.SKILLS_REQ);
        return hasSkill && cell.containsObject(BallistaObject.class)
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(BallistaJavelinShotAction.class));
    }

    public boolean canBallistaShot() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(BallistaShotAction.SKILLS_REQ);
        return hasSkill && getLastCell().containsObject(BallistaObject.class)
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(BallistaShotAction.class));
    }

    public boolean canBallistaVolleyShot() {
        if (isAnimal()) {
            return false;
        }

        boolean hasSkill = !isHuman() || team.getTeamSkillsManager().hasAllSkillLearnStarted(BallistaVolleyShotAction.SKILLS_REQ);
        return hasSkill && getLastCell().containsObject(BallistaObject.class)
                && team.getInventory().hasEnoughResources(ResourceCosts.getCost(BallistaVolleyShotAction.class));
    }

    public boolean canHunt() {
        if (isAnimal()) {
            return false;
        }

        return !isHuman() || team.getTeamSkillsManager().getSkill(SkillType.HUNTING).isLearnStarted();
    }

    public boolean canEquipItems() {
        return !isAnimal();
    }

    public void checkAndNotifyIfCanEquipBetter() {
        boolean canUseBetterMelee = false;
        boolean canUseBetterRange = false;
        boolean canUseBetterCloth = false;
        for (ObjectMap.Entry<
                Class<? extends AbstractInventoryItem>,
                AbstractInventoryItem> itemEntry
                : team.getInventory().getItemsIterator()) {
            AbstractInventoryItem inventoryItem = itemEntry.value;
            InventoryItemStaticParams itemStaticParams = inventoryItem.getParams();
            if (equipment.couldEquip(inventoryItem)) {
                if (!knowsAboutBetterItems.contains(itemStaticParams)) {
                    if (inventoryItem instanceof MeleeWeaponItem) {
                        canUseBetterMelee = true;
                    } else if (inventoryItem instanceof RangeWeaponItem) {
                        canUseBetterRange = true;
                    } else if (inventoryItem instanceof ClothesItem) {
                        canUseBetterCloth = true;

                    }

                    knowsAboutBetterItems.add(itemStaticParams);
                }
            }
        }

        if (canUseBetterMelee || canUseBetterRange || canUseBetterCloth) {
            batchFloatingStatusLines.start();
            if (canUseBetterMelee) {
                batchFloatingStatusLines.addLine("Can equip new melee");
            }
            if (canUseBetterRange) {
                batchFloatingStatusLines.addLine("Can equip new range");
            }
            if (canUseBetterCloth) {
                batchFloatingStatusLines.addLine("Can equip new clothes");
            }
            batchFloatingStatusLines.flush(Color.ORANGE);
        }
    }

    public boolean canSelectSkill() {
        return learnedSkills.size < expLevel
                && isHuman();
    }

    public boolean hasAttackAp() {
        return attackAp > 0;
    }

    /** weak / distance */
    public void calculateTargetValueRelativeTo(AbstractSquad attacker) {
        relativeTargetValue = attacker.getMyRelativePowerRatioWith(this)
                / cell.distanceTo(attacker.cell);
        if (team.isHumanPlayer()) {
            relativeTargetValue *= 2;
        }
    }
}
