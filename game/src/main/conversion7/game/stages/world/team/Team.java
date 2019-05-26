package conversion7.game.stages.world.team;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.AudioPlayer;
import conversion7.engine.CameraController;
import conversion7.engine.Gdxg;
import conversion7.engine.ai_new.base.AiEntity;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.artemis.GlobalStrategyAiSystem;
import conversion7.engine.artemis.audio.PlayerTribeAudioSystem;
import conversion7.engine.artemis.ui.UnitHeroIndicatorSystem;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.ai.team.TeamAiEvaluator;
import conversion7.game.dialogs.IlluminatiDialog1;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldBattle;
import conversion7.game.stages.world.WorldRelations;
import conversion7.game.stages.world.adventure.IlluminatiCampaign;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.ai_deprecated.AnimalAiTeamControllerOld;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.gods.AbstractGod;
import conversion7.game.stages.world.inventory.TeamCraftInventory;
import conversion7.game.stages.world.inventory.TeamInventory;
import conversion7.game.stages.world.landscape.Biom;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.buildings.CommunicationSatellite;
import conversion7.game.stages.world.objects.buildings.IronFactory;
import conversion7.game.stages.world.objects.buildings.UranusFactory;
import conversion7.game.stages.world.objects.composite.CompositeAreaObject;
import conversion7.game.stages.world.objects.totem.AbstractTotem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.quest.Journal;
import conversion7.game.stages.world.quest.items.FindAndKillIlluminatiDadQuest;
import conversion7.game.stages.world.quest.items.SendSosQuest;
import conversion7.game.stages.world.quest.items.WarWithTribeQuest;
import conversion7.game.stages.world.team.actions.*;
import conversion7.game.stages.world.team.events.*;
import conversion7.game.stages.world.team.goals.AbstractTribeGoal;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitNamesDatabase;
import conversion7.game.stages.world.unit.effects.items.ConcentrationEffect;
import conversion7.game.stages.world.unit.effects.items.HeroUnitEffect;
import conversion7.game.stages.world.unit.effects.items.PanicEffect;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;
import conversion7.game.stages.world.unit.hero_classes.SpecClass;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.*;

import static java.lang.String.format;

public class Team implements AiEntity {

    public static final int BASE_REL_ITEM = 1;
    public static final int EVOLUTION_EXP_PER_EVOLUTION_POINT = 15000;
    public static final double EVOLUTION_EXP_PER_1_CLAIMED_FOOD = 0.1f;
    public static final int START_EVOLUTION_POINTS = GdxgConstants.DEVELOPER_MODE ? 99 : 3;
    public static final Comparator<? super Team> RICH_COMPARATOR = new Comparator<Team>() {
        @Override
        public int compare(Team o1, Team o2) {
            return Integer.compare(o2.getRichValue(), o1.getRichValue());
        }
    };
    public static final Comparator<? super Team> WIP_TRIBE_VALUE_COMPARATOR = new Comparator<Team>() {
        @Override
        public int compare(Team o1, Team o2) {
            return Float.compare(o2.wipTribeValue, o1.wipTribeValue);
        }
    };
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int ANIMALS_TEAM_ALWAYS_ALIVE = 1;
    private static int teamsCounter = 0;
    public Array<AbstractSquad> potentialBattleTargets = new Array<>();
    public TeamSkillsManager teamSkillsManager = new TeamSkillsManager(this);
    public TeamClassesManager teamClassesManager = new TeamClassesManager(this);
    public boolean defeated;
    public World world;
    public TeamAiEvaluator tribeAiEvaluator = TeamAiEvaluator.instance;
    public Set<TribeBehaviourTag> tags = new HashSet<>();
    public boolean aiInProgress;
    public Array<AbstractTotem> totems = new Array<>();
    public TeamScenario scenario = new TeamScenario();
    public Journal journal = new Journal(this);
    @Deprecated
    public AbstractGod myGod;
    public int godExp;
    public float wipTribeValue;
    public Array<AbstractTribeGoal> goals = new Array<>();
    public int triedJoinOtherTribeOnStep = -1;
    public int playerUnitProgress;
    public AbstractSquad illumDad;
    public ObjectSet<AnimalSpawn> visitedSpawns = new ObjectSet<>();
    public ObjectSet<Team> metTeams = new ObjectSet<>();
    public ObjectSet<Class> learntAnimals = new ObjectSet<>();
    public AreaObject goalObject;
    public Area toBePlacedInArea;
    public Biom toBePlacedOnBiom;
    public IronFactory ironFactory;
    public UranusFactory uranFactory;
    private Unit unitControlsTribe;
    private String name;
    private int teamId = -1;
    private int teamOrder = 42;
    private boolean humanPlayer;
    private ObjectSet<Cell> visibleCellsPlayerTribeOnly;
    private ObjectSet<Cell> exploredCells;
    private ObjectSet<Area> visibleAreasPlayerTribeOnly;
    private Array<Camp> camps = new Array<>();
    private Array<AbstractSquad> squads = new Array<>();
    private Array<AbstractSquad> heroes = new Array<>();
    private Array.ArrayIterator<AbstractSquad> armiesReadIterator = new Array.ArrayIterator<>(squads, false);
    private Array<AbstractEventNotification> events = new Array<>();
    private Array<AbstractEventNotification> nextStepEvents = new Array<>();
    private Array<AbstractTeamAction> actions = new Array<>();
    @Deprecated
    private AiTeamControllerOld aiTeamControllerOld;
    private boolean completeTurnInProgress;
    private int armyInActIndex;
    private boolean defeatedWindowShown = false;
    private AbstractSquad lastSelectedObject;
    private int lastActAtWorldStep;
    private Integer tribeSeparationValue;
    private int evolutionPoints;
    private int evolutionExperience;
    private List<AiTask> aiTasks = new ArrayList<>();
    private GatheringStatistic gatheringStatistic = new GatheringStatistic();
    @Deprecated
    private AreaObject shaman;
    private Boolean sawDayNightEventAt;
    private boolean humanRace = false;
    private boolean animals = false;
    private boolean archonsRace = false;
    private boolean baalsRace = false;
    private ObjectSet<HeroClass> avaiableHeroClasses = new ObjectSet<>();
    private ObjectSet<SpecClass> avaiableSpecs = new ObjectSet<>();
    private TeamInventory inventory = new TeamInventory(this);
    private TeamCraftInventory craftInventory = new TeamCraftInventory(this);
    private Array<String> eventUiNotifications = new Array<>();
    private int evolutionPointsTotal;
    private boolean illuminati;
    private Cell tribeCenterPoint;
    private int triedAllyOtherTribeOnStep = -1;
    private Array<CompositeAreaObject> compositeObjects = new Array<>();
    private boolean hasDeadComposite;
    private CommunicationSatellite satellite;

    public Team(boolean isHumanPlayer, String name, World world) {
        this.name = isHumanPlayer ? "Player-" + name : name;
        this.world = world;
        teamId = teamsCounter++;
        humanPlayer = isHumanPlayer;
        evolutionPoints = START_EVOLUTION_POINTS;
        lastActAtWorldStep = world.getStep() - 1;
//        if (isHumanPlayer) {
        visibleAreasPlayerTribeOnly = new ObjectSet<>();
        visibleCellsPlayerTribeOnly = new ObjectSet<>();
        exploredCells = new ObjectSet<>();
//        }
        if (!isAnimalTeam()) {
            int godRndId = MathUtils.random(0, world.godsGlobalStats.gods.size() - 1);
            myGod = (AbstractGod) world.godsGlobalStats.gods.values().toArray()[godRndId];
        }
        if (LOG.isDebugEnabled()) LOG.debug("Created. {}", this);
        if (LOG.isDebugEnabled()) LOG.debug("lastActAtWorldStep {}", lastActAtWorldStep);
    }

    public static int getJoinChance(Team team, Team theirTeam) {
        float armiesRelSize = team.getSquads().size / (float) theirTeam.getSquads().size;
        if (armiesRelSize > WorldRelations.MIN_CHANCE_AT_SQUADS_REL) {
            float chanceFloat = armiesRelSize - WorldRelations.MIN_CHANCE_AT_SQUADS_REL;
            return (int) (chanceFloat * 100);
        } else {
            return 0;
        }
    }

    private int getRichValue() {
        int richValue = squads.size + camps.size;
        return richValue;
    }

    public int getTeamId() {
        return teamId;
    }

    public boolean isCompleteTurnInProgress() {
        return completeTurnInProgress;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public int getEvolutionExperience() {
        return evolutionExperience;
    }

    public int getEvolutionPoints() {
        return evolutionPoints;
    }

    public TeamSkillsManager getTeamSkillsManager() {
        return teamSkillsManager;
    }

    public TeamClassesManager getTeamClassesManager() {
        return teamClassesManager;
    }

    public boolean isAiPlayer() {
        return !humanPlayer;
    }

    public Array<AbstractEventNotification> getEvents() {
        return events;
    }

    public Array<AbstractTeamAction> getActions() {
        return actions;
    }

    public int getObjectsAmount() {
        return getSquads().size + getCamps().size;
    }

    public Array<AbstractSquad> getSquads() {
        return squads;
    }

    public Array<Camp> getCamps() {
        return camps;
    }

    public int getTribeSeparationValue() {
        if (tribeSeparationValue == null) {
            tribeSeparationValue = MathUtils.RANDOM.nextInt(GdxgConstants.TRIBE_SEPARATION_VALUE_MAX);
        }
        return tribeSeparationValue;
    }

    public void setTribeSeparationValue(Integer tribeSeparationValue) {
        this.tribeSeparationValue = tribeSeparationValue;
    }

    public CommunicationSatellite getSatellite() {
        return satellite;
    }

    public void setSatellite(CommunicationSatellite satellite) {
        this.satellite = satellite;
    }

    public Array<? extends CompositeAreaObject> getCompositeObjects() {
        if (hasDeadComposite) {
            Iterator<CompositeAreaObject> iterator = compositeObjects.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().alive) {
                    iterator.remove();
                }
            }
            hasDeadComposite = false;
        }
        return compositeObjects;
    }

    public Cell getTribeCenterPoint() {
        if (tribeCenterPoint == null) {
            calcTribeCenterPoint();
        }
        return tribeCenterPoint;
    }

    public boolean isIlluminati() {
        return illuminati;
    }

    public void setIlluminati(boolean illuminati) {
        this.illuminati = illuminati;
    }

    public AbstractSquad getIllumDad() {
        return illumDad;
    }

    public int getEvolutionPointsTotal() {
        return evolutionPointsTotal;
    }

    public Array<String> getEventUiNotifications() {
        return eventUiNotifications;
    }

    public TeamInventory getInventory() {
        return inventory;
    }

    public TeamCraftInventory getCraftInventory() {
        return craftInventory;
    }

    public boolean isAggressive() {
        return tags.contains(TribeBehaviourTag.AGGRESSIVE);
    }

    public Unit getUnitControlsTribe() {
        return unitControlsTribe;
    }

    public void setUnitControlsTribe(Unit unit) {
        this.unitControlsTribe = unit;
    }

    public boolean isHumanRace() {
        return humanRace;
    }

    public void setHumanRace(boolean humanRace) {
        this.humanRace = humanRace;
    }

    public boolean isAnimals() {
        return animals;
    }

    public void setAnimals(boolean animals) {
        this.animals = animals;
    }

    public boolean isArchonsRace() {
        return archonsRace;
    }

    public void setArchonsRace(boolean archonsRace) {
        this.archonsRace = archonsRace;
    }

    public void setBaalsRace(boolean baalsRace) {
        this.baalsRace = baalsRace;
    }

    public ObjectSet<Cell> getExploredCells() {
        return exploredCells;
    }

    public ObjectSet<Cell> getVisibleCellsPlayerTribeOnly() {
        return visibleCellsPlayerTribeOnly;
    }

    public AreaObject getShaman() {
        return shaman;
    }

    public void setShaman(AreaObject shaman) {
        this.shaman = shaman;
    }

    public Array.ArrayIterator<AbstractSquad> getArmiesReadIterator() {
        armiesReadIterator.reset();
        return armiesReadIterator;
    }

    public GatheringStatistic getGatheringStatistic() {
        return gatheringStatistic;
    }

    public int getLastActAtWorldStep() {
        return lastActAtWorldStep;
    }

    public Integer getTeamOrder() {
        return teamOrder;
    }

    public void setTeamOrder(Integer teamOrder) {
        this.teamOrder = teamOrder;
    }

    public Array<AbstractEventNotification> getNextStepEvents() {
        return nextStepEvents;
    }

    public boolean isHumanActivePlayer() {
        return isHumanPlayer() && world.activeTeam == this;
    }

    public boolean isHumanPlayer() {
        return humanPlayer;
    }

    public void setHumanPlayer(boolean humanPlayer) {
        this.humanPlayer = humanPlayer;
        if (humanPlayer) {
            world.humanPlayersCreated++;
            world.humanPlayers.add(this);
            World.initPlayerTeam(this, world);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AiTeamControllerOld getAiTeamControllerOld() {
        return aiTeamControllerOld;
    }

    @Deprecated
    public boolean isAnimalTeam() {
        return aiTeamControllerOld != null && AnimalAiTeamControllerOld.class.equals(aiTeamControllerOld.getClass());
    }

    public int getAliveObjects() {
        int aliveObjects = 0;
        for (AbstractSquad army : squads) {
            if (!army.isRemovedFromWorld()) {
                aliveObjects++;
            }
        }

        for (Camp camp : camps) {
            if (!camp.isRemovedFromWorld()) {
                aliveObjects++;
            }
        }
        return aliveObjects;
    }

    @Override
    public List<AiTask> getAiTasks() {
        return aiTasks;
    }

    public float getAverageUnitsExperience() {
        float sum = 0;
        for (AbstractSquad army : squads) {
            sum += army.experience;
        }
        return sum / squads.size;
    }

    public int getHeroSquadsAmount() {
        Iterator<AbstractSquad> iterator = heroes.iterator();
        while (iterator.hasNext()) {
            AbstractSquad next = iterator.next();
            if (!next.isAlive()) {
                iterator.remove();
            }
        }
        return heroes.size;
    }

    public boolean isBaals() {
        return baalsRace;
    }

    public boolean isHumanAiTribe() {
        return isAiPlayer() && isHumanRace();
    }

    public String getBehaviourTagsHint() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TribeBehaviourTag tag : tags) {
            stringBuilder.append(tag.toString()).append(" ");
        }
        return stringBuilder.toString();
    }

    public boolean isUfo() {
        return isArchonsRace() || isBaals();
    }

    public ObjectSet<HeroClass> getAvaiableHeroClasses() {
        return avaiableHeroClasses;
    }

    public ObjectSet<SpecClass> getAvaiableUnitSpecs() {
        return avaiableSpecs;
    }

    private int getHeroMax() {
        return 1 + visitedSpawns.size;
    }

    public String getHeroProgressAsText() {
        return getHeroSquadsAmount() + "/" + getHeroMax();
    }

    public Array.ArrayIterable<AbstractSquad> getSquadsIter() {
        return new Array.ArrayIterable<>(squads);
    }

    private boolean isQuestTribe() {
        return unitControlsTribe != null || isUfo();
    }

    public boolean getGenderRatio() {
        int males = 0;
        int females = 0;
        for (AbstractSquad squad : squads) {
            if (squad.isMale()) {
                males++;
            } else {
                females++;
            }
        }
        return males >= females;
    }

    public void setAiController(AiTeamControllerOld aiTeamControllerOld) {
        this.aiTeamControllerOld = aiTeamControllerOld;
    }

    public void setLastSelectedObject(AbstractSquad lastSelectedObject) {
        this.lastSelectedObject = lastSelectedObject;
    }

    public void setGoalObject(AreaObject object) {
        goalObject = object;
        world.addImportantObj(object);
        object.getLastCell().addFloatLabel("New goal", Color.ORANGE);
    }

    public ObjectSet<Cell> calcVisibleCells(ObjectSet<Cell> cells) {
        for (AbstractSquad army : getArmiesReadIterator()) {
            if (!army.isRemovedFromWorld()) {
                cells.add(army.getLastCell());
                cells.addAll(army.getVisibleCellsAround());
            }
        }
        return cells;
    }

    private boolean canUseClone() {
        return !IlluminatiCampaign.gotAway && illumDad != null && illumDad.isAlive();
    }

    public void recalcAlly() {

    }

    public void recalcEnemies() {
        if (isHumanPlayer()) {
            Array<Team> enemies = world.getEnemies(this);
            if (enemies.size > ANIMALS_TEAM_ALWAYS_ALIVE) {
                WarWithTribeQuest.newWarStarted(this);
            } else {
                WarWithTribeQuest.noActiveWars(this);
            }
        }
    }

    public void addHero(AbstractSquad squad) {
        heroes.add(squad);
        recalculateObjectActions();
    }

    public void updateEvolutionExp(int changeOnValue) {
        gatheringStatistic.evolutionExp += changeOnValue;
        evolutionExperience += changeOnValue;
        int newEvolPoints = evolutionExperience / EVOLUTION_EXP_PER_EVOLUTION_POINT;
        if (newEvolPoints > 0) {
            evolutionExperience -= newEvolPoints * EVOLUTION_EXP_PER_EVOLUTION_POINT;
            updateEvolutionPointsOn(newEvolPoints, "Simple evolution");
        } else {
            if (isHumanPlayer()) {
                Gdxg.clientUi.getTeamBar().refreshContent(this);
            }
        }
    }

    public void updateEvolutionPointsOn(int changeOnValue, String msg) {
        if (changeOnValue != 0) {
            if (changeOnValue > 0) {
                gatheringStatistic.evolutionPoints += changeOnValue;
            }
            this.evolutionPoints += changeOnValue;
            if (changeOnValue > 0) {
                this.evolutionPointsTotal += changeOnValue;
                if (isHumanPlayer()) {
                    AudioPlayer.play("fx\\new_skill.mp3");
                }
            }
            if (isHumanActivePlayer()) {
                if (msg != null) {
                    UiLogger.addImportantGameInfoLabel("EP " + UiUtils.getNumberWithSign(changeOnValue) + ": " + msg);
                }
                Gdxg.clientUi.getTeamBar().refreshContent(this);
                Gdxg.clientUi.getTribeResorcesPanel().showFor(this);
                world.savePlayerTeamProgress(this);
            }
        }
    }

    public boolean isAllyOf(Team team) {
        return world.areAllies(this, team);
    }

    public boolean isEnemyOf(Team team) {
        return world.areEnemies(this, team);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getTeamId();
    }

    public Camp createCamp(Cell cell) {
        Assert.assertNull(cell.getCamp());
        Camp camp = new Camp(cell, this);
        camp.completeConstruction();
        addCamp(camp);
        if (cell.hasSquad()) {
            cell.squad.unitCellValidator.invalidate();
            cell.squad.getActionsController().invalidate();
            cell.squad.validate();
        }
        return camp;
    }

    public void removeCamp(Camp camp) {
        camps.removeValue(camp, true);
    }

    public void addCamp(Camp camp) {
        camps.add(camp);
        camp.validateView();
        camp.validate();

        Cell cell = camp.getLastCell();
        if (cell.hasSquad()) {
            cell.squad.getActionsController().invalidate();
            cell.squad.unitCellValidator.invalidate();
            cell.squad.validate();
        }
    }

    public WorldSquad createWorldSquad(Cell cell, Unit unit) {
        if (cell.hasSquad()) {
            if (cell.getSquad().isAnimal()) {
                if (!world.initialized) {
                    LOG.warn("World is under initialization > will defeat AnimalHerd to place HumanSquad here: " + cell);
                    WorldSquad.killUnit(cell.getSquad());
                }
            }
        }
        WorldSquad squad = WorldSquad.create(unit, this, cell);
        return squad;
    }

    public void addSquad2(WorldSquad squad) {
        squad.setTeam(this);
        getSquads().add(squad);
        HeroUnitEffect heroUnitEffect = squad.getEffectManager().getEffect(HeroUnitEffect.class);
        if (heroUnitEffect != null) {
            heroUnitEffect.remove();
        }

        Team prevTeam = squad.getPrevTeam();
        if (prevTeam != null) {
        }

        UnitHeroIndicatorSystem.components.create(squad.entityId).squad = squad;
        squad.validateView();
        teamClassesManager.addUnitIfNewcomerInTeam(squad.unit);
        squad.validate();
        squad.addSnapshotLog("Added to team: " + this);
        squad.refreshUiPanelInWorld();
    }

    /** TODO team could not create settlement from beginning, define when could */
    public boolean couldCreateSettlement() {
        return false;
    }

    public void startTurn() {
        LOG.info(format("startTurn %s", toString()));
        Assert.assertFalse(defeated);
        // disabled because of new teams added in middle of world-turns, but before activeTeam, os they have no turn on prev. step
//        Assert.assertEquals(world.getStep(), lastActAtWorldStep + 1, "Team or World has wrong step counter!");
        for (AbstractSquad squad : squads) {
            squad.effectManager.removeEffectIfExist(ConcentrationEffect.class);
//            squad.effectManager.removeEffectIfExist(EvadeEffect.class);
            squad.relax = 0;
        }

        runPanicIfNeed();

        if (humanPlayer) {
            humanPlayerStartsTurn();
        } else {
            if (world.lastActivePlayerTeam == null || world.lastActivePlayerTeam.isDefeated()) {
                if (squads.size > 0) {
                    SelectNextTeamObjectAction.focusOn(squads.get(0));
                }
            }
            potentialBattleTargets.clear();
            if (GdxgConstants.AREA_OBJECT_AI) {
                aiInProgress = true;
                tribeAiEvaluator.loop(this);
                GlobalStrategyAiSystem.startFor(this);
            }
        }
    }

    private void runPanicIfNeed() {
        for (AbstractSquad squad : new Array.ArrayIterable<>(squads)) {
            if (squad.isAlive() && squad.effectManager.containsEffect(PanicEffect.class)) {
                PanicEffect.tryApply(squad.unit);
            }
        }
    }

    private void humanPlayerStartsTurn() {
        if (defeated) {
            LOG.info("spectator");
            if (!defeatedWindowShown) {
                showDefeatedWindow();
            }
        } else {
            UiLogger.addImportantGameInfoLabel("Player turn: " + name);
            recalculateVisibleCellsPlayerTribeOnly();
            for (AbstractSquad squad : squads) {
                squad.skipTurn = false;
            }


            // setup notifications:
//                if (world.step > 0) {
            events.add(new NewStepStartedEvent(this));
//                }
            boolean daytime = world.isDaytime();
            if (sawDayNightEventAt == null || sawDayNightEventAt != daytime) {
                sawDayNightEventAt = daytime;
                addEventNotification(daytime ?
                        new DayTimeStartedEvent(this) : new NightTimeStartedEvent(this));
            }

            if (world.isChaosStartedOnThisStep()) {
                addEventNotification(new ChaosPeriodStartedEvent(this));
            }
            events.addAll(nextStepEvents);
            nextStepEvents.clear();

            // setup team actions:
            addAction(new SelectNextTeamObjectAction(this));
            addAction(new SelectNextImportantObjectAction(this));
            addAction(new OpenJournalAction(this));
            addAction(new OpenTeamSkillsWindowAction(this));
            addAction(new OpenTeamInfoPanelAction(this));
            addAction(new OpenClassesWindowAction(this));
            addAction(new OpenGodsPanelAction(this));


            AbstractSquad focusOn = this.lastSelectedObject;
            if (focusOn == null || focusOn.isRemovedFromWorld()) {
                if (squads.size > 0) {
                    focusOn = squads.first();
                }
            }
            if (focusOn != null) {
                CameraController.scheduleCameraFocusOn(0, focusOn.getLastCell());
                try {
                    Gdxg.getAreaViewer().selectCell(focusOn.getLastCell());
                } catch (NullPointerException e) {
                    LOG.error(e.getMessage(), e);
                }
            }

            gatheringStatistic.reset();

            if (world.lastActivePlayerTeam != null) {
                world.lastActivePlayerTeam.refreshUiForAllObjects();
            }
            this.refreshUiForAllObjects();
            Gdxg.clientUi.getTeamBar().refreshContent(this);
            Gdxg.clientUi.getTribeResorcesPanel().showFor(this);

            AudioPlayer.playMultiSnares();

            WorldBattle.processPostponedBattles(world);
        }
        Gdxg.clientUi.showTeamUi();
        Gdxg.core.world.totalViewReload2();

    }

    private void refreshUiForAllObjects() {
        for (AbstractSquad squad : getSquadsIter()) {
            squad.refreshUiPanelInWorld();
        }
    }

    public void showDefeatedWindow() {
        defeatedWindowShown = true;
        Gdxg.clientUi.getTeamDefeatedWindow().show();
    }

    // bad design
    @Deprecated
    public void recalculateVisibleCellsPlayerTribeOnly() {
        if (!isHumanPlayer() || !world.settings.fogOfWar) {
            return;
        }

        // mark old cells
        for (Cell visibleCell : visibleCellsPlayerTribeOnly) {
            visibleCell.setDiscovered(Cell.Discovered.NOT_VISIBLE);
        }

        // new visibleCells:
        visibleCellsPlayerTribeOnly.clear();

        for (AbstractSquad army : getArmiesReadIterator()) {
            if (!army.isRemovedFromWorld()) {
                visibleAreasPlayerTribeOnly.add(army.getArea());
                visibleCellsPlayerTribeOnly.add(army.getLastCell());
                visibleCellsPlayerTribeOnly.addAll(army.getVisibleCellsAround());
            }
        }

        // explored
        exploredCells.addAll(visibleCellsPlayerTribeOnly);
        for (Cell exploredCell : exploredCells) {
            exploredCell.setDiscovered(Cell.Discovered.NOT_VISIBLE);
        }

        // new visibleCells:
        for (Cell visibleCell : visibleCellsPlayerTribeOnly) {
            visibleCell.setDiscovered(Cell.Discovered.VISIBLE);
        }
        for (Area visibleArea : visibleAreasPlayerTribeOnly) {
            visibleArea.setDiscovered(true);
        }

    }

    private void addAction(AbstractTeamAction abstractTeamAction) {
        actions.add(abstractTeamAction);
    }

    /** Returns true if fully completed */
    public boolean completeTurn() {
        if (LOG.isDebugEnabled()) LOG.debug("completeTurn " + this);

        if (completeTurnInProgress) {
            return completeTurnInProgress();
        }

        events.clear();
        actions.clear();

        if (isHumanPlayer()) {
            Gdxg.clientUi.getEventsBar().hide();
            Gdxg.getAreaViewer().deselectCell();
            Gdxg.clientUi.getWorldMainWindow().clear();
        }

        for (AbstractSquad squad : getSquadsIter()) {
            if (squad.getAttackAp() > 0 && !squad.isAnimal()) {
                squad.effectManager.getOrCreate(ConcentrationEffect.class);
            }
        }

        if (isIlluminati() && !IlluminatiCampaign.worldWarStarted) {
            IlluminatiCampaign.giftOthers(this);
        }

        completeTurnInProgress = true;
        armyInActIndex = -1;
        calcTribeCenterPoint();
        return completeTurnInProgress();
    }

    public Cell calcTribeCenterPoint() {
        Point2s point2s = new Point2s();
        for (AbstractSquad squad : squads) {
            Cell cell = squad.getLastCell();
            point2s.x += cell.x;
            point2s.y += cell.y;
        }

        if (squads.size > 0) {
            point2s.x /= squads.size;
            point2s.y /= squads.size;
        }

        tribeCenterPoint = world.getCell(point2s.x, point2s.y);
        return tribeCenterPoint;
    }

    /** Returns true if fully completed */
    public boolean completeTurnInProgress() {
        LOG.info("completeTurnInProgress");
        while (++armyInActIndex < squads.size) {
            squads.get(armyInActIndex).executeActiveTask();
            if (Gdxg.core.isBattleActiveStage()) {
                return false;
            }
        }

        PathData.resetObstacleFilters();

        lastActAtWorldStep = world.getStep();
        LOG.info("completeTurnInProgress DONE > Team {} completed turn at world step {}", name, lastActAtWorldStep);
        completeTurnInProgress = false;
        return true;
    }

    public void updatedAttitude(Team team, int updateAttitudeOnValue) {
        UiLogger.addInfoLabel(format("%s%d team attitude: %s > %s ",
                (updateAttitudeOnValue > 0 ? "+" : ""), updateAttitudeOnValue, getName(), team.getName()));
        // TODO implement attitude system: map <Team, Integer-attitude>, by default map is empty (0 attitude to all)
    }

    public void clearDefeatedObjects() {
        LOG.info("clearDefeatedObjects {}", toString());
        Iterator<AbstractSquad> iterator = squads.iterator();
        while (iterator.hasNext()) {
            AbstractSquad next = iterator.next();
            if (next.isRemovedFromWorld()) {
                disposeSquad(next, iterator);
            }
        }

        Iterator<Camp> townFragmentIterator = camps.iterator();
        while (townFragmentIterator.hasNext()) {
            Camp next = townFragmentIterator.next();
            if (next.isRemovedFromWorld()) {
                townFragmentIterator.remove();
            }
        }

    }

    public void validateInventoriesAndEquipment() {
        craftInventory.update();
    }

    public void validateObjectActions() {
        for (AbstractSquad army : new Array.ArrayIterable<>(squads)) {
            army.getActionsController().invalidate();
            army.validate();
        }

        for (Camp camp : new Array.ArrayIterable<>(camps)) {
            camp.getActionsController().invalidate();
            camp.validate();
        }
    }

    public void joinSquad(AbstractSquad squad) {
        Team prevTeam = squad.getTeam();
        LOG.info("team: {}\njoins squad: {}\nfrom team: {}", toString(), squad, prevTeam);
        squad.clearTasks();
        prevTeam.disposeSquad(squad, null);

        tryLearnAnimal(squad);

        addSquad2((WorldSquad) squad);

        Array<AbstractSquad> squads = squad.getLastCell().
                getObjectsAroundFromToRadiusInclusively(1, 2, AbstractSquad.class);
        squads.add(squad);
        for (AbstractSquad abstractSquad : squads) {
            abstractSquad.validate(true);
        }
    }

    public void tryLearnAnimal(AbstractSquad squad) {
        tryLearnAnimal(squad.getLastCell(), squad.getUnitClass());
    }

    public void tryLearnAnimal(Cell onCell, Class<? extends Unit> probAnimalClass) {
        if (BaseAnimalClass.class.isAssignableFrom(probAnimalClass) && !hasLearntAnimal(probAnimalClass)) {
            learntAnimals.add(probAnimalClass);
            updateEvolutionPointsOn(1, "Learnt animal class");
        }
    }

    private boolean hasLearntAnimal(Class<? extends Unit> unitClass) {
        return learntAnimals.contains(unitClass);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TEAM ")
                .append(" name=").append(getName()).append(GdxgConstants.HINT_SPLITTER)
                .append(" id=").append(getTeamId()).append(GdxgConstants.HINT_SPLITTER)
                .append(" humanPlayer=").append(isHumanPlayer()).append(GdxgConstants.HINT_SPLITTER)
                .append(" defeated=").append(defeated).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public void disposeSquad(AbstractSquad squad, Iterator<AbstractSquad> iterator) {
        if (iterator == null) {
            getSquads().removeValue(squad, true);
        } else {
            iterator.remove();
        }
        heroes.removeValue(squad, true);
        recalculateVisibleCellsPlayerTribeOnly();
        squad.addSnapshotLog("Removed from team: " + this);
    }

    public void defeat() {
        world.defeat(this);
    }

    public boolean canSeeObject(AreaObject areaObject) {
        if (areaObject instanceof AbstractSquad) {
            for (AbstractSquad army : squads) {
                if (army.visibleObjects.contains(areaObject, true)
                        || areaObject.team == army.team) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    /** destroy > dispose */
    public void defeatUnit(AbstractSquad squad) {
        if (squad.destroyed) {
            return;
        }
        LOG.info("destroy: {}", squad);

        Team killedByTeam = squad.getKilledByTeam();
        if (killedByTeam != null && world.getImportantObjects().contains(squad, true)) {
            killedByTeam.updateEvolutionPointsOn(1, "World goal completed");
        }

        if (squad.isIlluminat() && squad.team.canUseClone()) {
            squad.cloneUnit();
        }

        if (squad.team.illumDad == squad && !IlluminatiCampaign.gotAway) {
            squad.batchFloatingStatusLines.addImportantLine("Illuminati Dad defeated");
            FindAndKillIlluminatiDadQuest illuminatiDadQuest = IlluminatiCampaign.targTeam.journal.getOrCreate(FindAndKillIlluminatiDadQuest.class);
            illuminatiDadQuest.complete(FindAndKillIlluminatiDadQuest.State.S1);
            if (killedByTeam != null) {
                killedByTeam.updateEvolutionPointsOn(1, FindAndKillIlluminatiDadQuest.class.getSimpleName());
            }
            illuminatiDadQuest.initEntry(FindAndKillIlluminatiDadQuest.State.S2, "Defeat Illuminati");
        }

        if (squad.isBaalBoss() || squad.isArchon()) {
//            IlluminatiCampaign.failed(world);
        }

        if (squad.isArchon()) {
            world.lastActivePlayerTeam.journal.getOrCreate(SendSosQuest.class)
                    .failAllOpen();
        }

        if (squad.team.isHumanPlayer()) {
            PlayerTribeAudioSystem.unitLost = true;
        }

        // post
        squad.destroyUnit();
        getTeamClassesManager().removeUnit(squad.unit);

        squad.removeFromWorld();
        squad.team.addEventNotification(new UnitDefeatedEvent(squad));

        squad.releaseControlEffects();

    }

    public void addEventNotification(AbstractEventNotification event) {
        nextStepEvents.add(event);
    }

    public boolean shouldSeeUiControls() {
        return this == world.activeTeam || GdxgConstants.DEVELOPER_MODE;
    }

    @Override
    public void addAiTask(AiTask aiTask) {
        aiTasks.add(aiTask);
    }

    public void validateObjects() {
        for (AbstractSquad army : squads) {
            army.validate();
        }
        for (Camp camp : camps) {
            camp.validate();
        }
    }

    public void recalculateObjectActions() {
        for (AbstractSquad army : squads) {
            army.getActionsController().forceTreeValidationFromThisNode();
        }
        for (Camp camp : camps) {
            camp.getActionsController().forceTreeValidationFromThisNode();
        }
    }

    public boolean hasShaman() {
        return shaman != null;
    }

    public boolean canCaptureCamp(Cell cell) {
        if (cell.camp != null
                && cell.camp.team != this
                && isHumanPlayer()) {
            return canAttack(cell.camp.team);
        }
        return false;
    }

    public boolean canAttack(Team team) {
        if (team == null || this == team) {
            return false;
        }
        return isEnemyOf(team) ||
                (isAggressive() && !isAllyOf(team));
    }

    public void addTotem(AbstractTotem abstractTotem) {
        totems.add(abstractTotem);
    }

    public void updateGodExp(int godExpUpdate) {
        godExp += godExpUpdate;
//        if (shaman != null) {
//            shaman.updateMana(godExpUpdate);
//        }
    }

    public void recalculateVisibleCellsFull() {
        for (AbstractSquad squad : getSquads()) {
            squad.refreshVisibleCells();
        }
        recalculateVisibleCellsPlayerTribeOnly();
    }

    public boolean canSeeCell(Cell cell) {
        return !world.settings.fogOfWar || visibleCellsPlayerTribeOnly.contains(cell);
    }

    public boolean checkAlive() {
        return getAliveObjects() > 0 || isAnimalTeam() || isBaals();
    }

    public void addGoal(AbstractTribeGoal goal) {
        goals.add(goal);
    }

    public void startQuest(Class<? extends BaseQuest> questClass) {
        journal.getOrCreate(questClass);
        if (isHumanPlayer()) {
            AudioPlayer.playTribe();
        }
    }


    public int getRelationBalance(Team team) {
        return world.worldRelations.getRelationData(this, team).getBalance();
    }

    public String getRelationName(Team team) {
        if (this.isAllyOf(team)) {
            return "Ally";
        } else if (this.isEnemyOf(team)) {
            return "Enemy";
        } else {
            return "Neutral";
        }
    }

    public int getAllyChance(Team team) {
        int relationBalance = getRelationBalance(team);
        if (relationBalance > 0) {
            return relationBalance * WorldRelations.ALLY_PERC_PER_REL_POINT;
        }
        return 0;
    }

    public boolean tryToAlly(Team other) {
        triedAllyOtherTribeOnStep = world.step;
        int allyChance = getAllyChance(other);
        if (MathUtils.testPercentChance(allyChance)) {
            this.world.addRelationType(TribeRelationType.ALLY, other, this);
            return true;
        }
        return false;
    }

    public boolean tryToJoin(AbstractSquad squad) {
        triedJoinOtherTribeOnStep = world.step;
        int joinChance = Team.getJoinChance(this, squad.team);
        if (MathUtils.testPercentChance(joinChance)) {
            this.joinSquad(squad);
            return true;
        }
        return false;
    }

    public boolean canAskAboutAllyAtWorldStep() {
        return triedAllyOtherTribeOnStep < world.step;
    }

    public boolean canAskToJoinAtWorldStep() {
        return triedJoinOtherTribeOnStep < world.step;
    }

    public String getNextUnitName(boolean gender) {
        Array<String> names;
        if (gender) {
            names = UnitNamesDatabase.maleNames;
        } else {
            names = UnitNamesDatabase.femaleNames;
        }
        return names.random() + " " + UnitNamesDatabase.secondNames.random();
    }

    public void addEventMainUiNotification(String msg) {
        eventUiNotifications.add(msg);
    }

    public void removeGoal(Class<? extends AbstractTribeGoal> goalClass) {
        Iterator<AbstractTribeGoal> tribeGoalIterator = goals.iterator();
        while (tribeGoalIterator.hasNext()) {
            AbstractTribeGoal goal = tribeGoalIterator.next();
            if (goal.getClass().equals(goalClass)) {
                tribeGoalIterator.remove();
            }
        }
    }

    public boolean canHaveMoreHeroes() {
        return getHeroMax() > getHeroSquadsAmount();
    }

    public void meetsTeam(Team team, Cell onCell) {
        if (team == null || this == team) {
            return;
        }

        world.initRelations(this, team);

        if (!metTeams.contains(team)) {
            metTeams.add(team);
            if (isHumanPlayer()) {
                String msg = "Meet team: " + team.getName();
                UiLogger.addImportantGameInfoLabel(msg);
                onCell.addFloatLabel(msg, Color.WHITE);
                PlayerTribeAudioSystem.metTribe = true;
                if (team.isIlluminati() && !IlluminatiCampaign.metPlayer) {
                    IlluminatiCampaign.metPlayer = true;
                    new IlluminatiDialog1(world.lastActivePlayerTeam, team, onCell).start();
                }
            }
        }
    }

    public void onCompleteTurn() {
        for (AbstractSquad squad : getSquadsIter()) {
            if (squad.getMoveAp() > 0) {
                squad.relax = squad.getMoveAp();
            }
            squad.moveExtraApToArmor();
        }
    }

    public boolean canBeAlly() {
        return !isHumanPlayer() && isHumanAiTribe() && !isQuestTribe();
    }

    public boolean canUseCraft() {
        return teamSkillsManager.getSkill(SkillType.STONE_WORK).isLearnStarted();
    }

    public void addRelation(TribeRelationType type, Team team) {
        world.addRelationType(type, this, team);
    }

    public void completesGoalFor(Team other, Cell atCell) {
        addRelation(TribeRelationType.GOAL, other);
        atCell.addFloatLabel("Goal done by: " + this.name, Color.ORANGE);
        goalObject = null;
    }

    public void addComposite(CompositeAreaObject composit) {
        compositeObjects.add(composit);
        composit.setTeam(this);
    }

    public void defeatComposite(CompositeAreaObject composit) {
        hasDeadComposite = true;
    }

    public boolean hasSatellite() {
        return satellite != null;
    }
}


