package conversion7.game.stages.world.team;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.ai.AnimalAiTeamController;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.objects.TownFragment;
import conversion7.game.stages.world.team.actions.AbstractTeamAction;
import conversion7.game.stages.world.team.actions.FocusNextTeamObjectAction;
import conversion7.game.stages.world.team.actions.OpenClassesWindowAction;
import conversion7.game.stages.world.team.actions.OpenTeamSkillsWindowAction;
import conversion7.game.stages.world.team.events.AbstractEventNotification;
import conversion7.game.stages.world.team.events.NewStepStartedEvent;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

import java.util.Iterator;

import static java.lang.String.format;

public class Team {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static int teamsCounter = 0;
    public static final int EVOLUTION_SUB_POINTS_PER_POINT = 10000;

    private String name;
    private int teamId = -1;
    private boolean humanPlayer;

    private ObjectSet<Cell> visibleCells;
    private ObjectSet<Area> visibleAreas;
    private Array<TownFragment> townFragments = new Array<>();
    private Array<AbstractSquad> armies = new Array<>();
    private Array<AbstractEventNotification> events = new Array<>();
    private Array<AbstractEventNotification> nextStepEvents = new Array<>();
    private Array<AbstractTeamAction> actions = new Array<>();
    private Array<Team> allies = new Array<>();
    private AiTeamController aiTeamController;
    private TeamSkillsManager teamSkillsManager = new TeamSkillsManager(this);
    private TeamClassesManager teamClassesManager = new TeamClassesManager(this);
    private int evolutionPoints;
    private boolean defeated;
    private boolean completeTurnInProgress;
    private int armyInActIndex;
    private int townInActIndex;
    private boolean defeatedWindowShown = false;
    private AreaObject lastSelectedObject;
    private Integer tribeSeparationValue;
    private int evolutionSubpoints;

    public Team(boolean isHumanPlayer, String name) {
        this.name = name;
        teamId = teamsCounter++;
        humanPlayer = isHumanPlayer;
        evolutionPoints = 10;
        if (isHumanPlayer) {
            visibleAreas = new ObjectSet<>();
            visibleCells = new ObjectSet<>();
        }
        if (LOG.isDebugEnabled()) LOG.debug("Created. id=" + getTeamId() + " name=" + name);
    }

    public boolean isCompleteTurnInProgress() {
        return completeTurnInProgress;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public int getEvolutionSubpoints() {
        return evolutionSubpoints;
    }

    public int getEvolutionPoints() {
        return evolutionPoints;
    }

    public void updateEvolutionSubPointsOn(int changeOnValue) {
        evolutionSubpoints += changeOnValue;
        int newEvolPoints = evolutionSubpoints / EVOLUTION_SUB_POINTS_PER_POINT;
        if (newEvolPoints > 0) {
            evolutionSubpoints -= newEvolPoints * EVOLUTION_SUB_POINTS_PER_POINT;
            updateEvolutionPointsOn(newEvolPoints);
        } else {
            if (isHumanPlayer()) {
                Gdxg.clientUi.getTeamBar().refreshContent(this);
            }
        }
    }

    public void updateEvolutionPointsOn(int changeOnValue) {
        if (changeOnValue != 0) {
            this.evolutionPoints += changeOnValue;
            if (isHumanPlayer()) {
                UiLogger.addInfoLabel("Evolution points " + MathUtils.formatNumber(changeOnValue));
                Gdxg.clientUi.getTeamBar().refreshContent(this);
            }
        }
    }

    public TeamSkillsManager getTeamSkillsManager() {
        return teamSkillsManager;
    }

    public TeamClassesManager getTeamClassesManager() {
        return teamClassesManager;
    }

    public int getTeamId() {
        return teamId;
    }

    public boolean isHumanPlayer() {
        return humanPlayer;
    }

    public boolean isAiPlayer() {
        return !humanPlayer;
    }

    public boolean isAnimalTeam() {
        return aiTeamController != null && AnimalAiTeamController.class.equals(aiTeamController.getClass());
    }

    public Array<TownFragment> getTownFragments() {
        return townFragments;
    }

    public Array<AbstractSquad> getArmies() {
        return armies;
    }

    public Array<Team> getAllies() {
        return allies;
    }

    public AiTeamController getAiTeamController() {
        return aiTeamController;
    }

    public String getName() {
        return name;
    }

    public Array<AbstractEventNotification> getEvents() {
        return events;
    }

    public Array<AbstractTeamAction> getActions() {
        return actions;
    }

    public boolean isAllyOf(Team team) {
        return getAllies().contains(team, false);
    }

    public boolean isEnemyOf(Team team) {
        return true;
    }

    public void setAiController(AiTeamController aiTeamController) {
        this.aiTeamController = aiTeamController;
    }

    public int getObjectsAmount() {
        return getArmies().size + getTownFragments().size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TEAM ")
                .append("name=").append(getName()).append(GdxgConstants.HINT_SPLITTER)
                .append("id=").append(getTeamId()).append(GdxgConstants.HINT_SPLITTER)
                .append("humanPlayer=").append(isHumanPlayer()).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }


    @Override
    public int hashCode() {
        return super.hashCode() + getTeamId();
    }

    private void addAction(AbstractTeamAction abstractTeamAction) {
        actions.add(abstractTeamAction);
    }

    public TownFragment createTown(Cell cell) {
        TownFragment fragment = new TownFragment(cell, this);
        getTownFragments().add(fragment);
        fragment.validateView();
        fragment.getActionsController().validate();
        return fragment;
    }

    public void addSquad(AbstractSquad abstractSquad) {
        getArmies().add(abstractSquad);
        abstractSquad.setTeam(this);
        abstractSquad.validateView();
        abstractSquad.getActionsController().validate();
        for (Unit unit : abstractSquad.getUnits()) {
            teamClassesManager.addUnitIfNewcomerInTeam(unit);
        }
        abstractSquad.addSnapshotLog("Added to team: " + this);
    }

    public void removeSquad(AbstractSquad abstractSquad) {
        getArmies().removeValue(abstractSquad, true);
        abstractSquad.setTeam(null);
        validateIsDefeated();
        recalculateVisibleCells();
        for (Unit unit : abstractSquad.getUnits()) {
            teamClassesManager.removeUnit(unit);
        }
        abstractSquad.addSnapshotLog("Removed from team: " + this);
    }

    public HumanSquad createHumanSquad(Cell cell) {
        if (cell.isSeized()) {
            if (cell.getSeizedBy().isAnimalHerd()) {
                if (!World.initialized) {
                    LOG.info("World is under initialization > will defeat AnimalHerd to place HumanSquad here: " + cell);
                    cell.getSeizedBy().defeat();
                }
            }
        }
        HumanSquad army = new HumanSquad(cell, this);
        addSquad(army);
        return army;
    }

    public AnimalHerd createAnimalHerd(Cell cell) {
        AnimalHerd army = new AnimalHerd(cell, this);
        addSquad(army);
        World.createdAnimalHerds++;
        return army;
    }

    public void validateIsDefeated() {
        int aliveObjects = 0;
        for (AbstractSquad army : armies) {
            if (!army.isRemovedFromWorld()) {
                aliveObjects++;
            }
        }

        for (TownFragment townFragment : townFragments) {
            if (!townFragment.isRemovedFromWorld()) {
                aliveObjects++;
            }
        }

        if (aliveObjects == 0) {
            defeat();
        }
    }

    public void defeat() {
        // animals team is regenerating armies
        if (!isAnimalTeam()) {
            LOG.info("Defeated: " + toString());
            defeated = true;
            if (humanPlayer) {
                if (ClientCore.core.isAreaViewerActiveStage()) {
                    showDefeatedWindow();
                    if (World.getActiveTeam().equals(this)) {
                        Gdxg.clientUi.getTeamBar().refreshContent(this);
                        Gdxg.clientUi.getEventsBar().hide();
                    }
                }
            }
        }
    }

    public void addAlly(Team newAlly) {
        getAllies().add(newAlly);
    }

    public void removeAlly(Team ally) {
        getAllies().removeValue(ally, false);
    }

    /** TODO team could not create settlement from beginning, define when could */
    public boolean couldCreateSettlement() {
        return false;
    }

    public void startTurn() {
        if (LOG.isDebugEnabled()) LOG.debug(format("startTurn id=%s name=%s", getTeamId(), getName()));

        World.setActiveTeam(this);
        if (humanPlayer) {
            if (defeated) {
                LOG.info("spectator");
                if (!defeatedWindowShown) {
                    showDefeatedWindow();
                }
            } else {
                recalculateVisibleCells();
                // setup notifications:
                events.addAll(new NewStepStartedEvent(this));
                events.addAll(nextStepEvents);
                nextStepEvents.clear();
                // setup team actions:
                addAction(new OpenTeamSkillsWindowAction(this));
                addAction(new OpenClassesWindowAction(this));
                addAction(new FocusNextTeamObjectAction(this));
            }
            Gdxg.clientUi.showTeamUi();

        } else {
            getAiTeamController().ai();
        }
    }

    public void recalculateVisibleCells() {
        LOG.info("recalculateVisibleCells");
        if (!isHumanPlayer() || !GdxgConstants.AREA_VIEWER_FOG_OF_WAR_ENABLED) {
            return;
        }

        // mark old cells
        for (Cell visibleCell : visibleCells) {
            visibleCell.setDiscovered(Cell.Discovered.NOT_VISIBLE);
        }
        visibleCells.clear();

        // mark new cells:
        for (AbstractSquad army : armies) {
            if (!army.isRemovedFromWorld()) {
                visibleAreas.add(army.getArea());
                visibleCells.add(army.getCell());
                visibleCells.addAll(army.getVisibleCellsAroundOnly());
            }
        }

        for (TownFragment townFragment : townFragments) {
            if (!townFragment.isRemovedFromWorld()) {
                visibleAreas.add(townFragment.getArea());
                visibleCells.add(townFragment.getCell());
                visibleCells.addAll(townFragment.getVisibleCellsAroundOnly());
            }
        }

        for (Cell visibleCell : visibleCells) {
            visibleCell.setDiscovered(Cell.Discovered.VISIBLE);
        }
        for (Area visibleArea : visibleAreas) {
            visibleArea.setDiscovered(true);
        }

    }

    /** Returns true if fully completed */
    public boolean completeTurn() {
        if (LOG.isDebugEnabled()) LOG.debug("completeTurn " + this);

        if (completeTurnInProgress) {
            return completeTurnAct();
        }

        if (isHumanPlayer()) {
            Gdxg.clientUi.getEventsBar().hide();
            events.clear();
            actions.clear();
            World.getAreaViewer().deselect();
        }

        completeTurnInProgress = true;
        armyInActIndex = -1;
        townInActIndex = -1;
        return completeTurnAct();
    }

    /** Returns true if fully completed */
    public boolean completeTurnAct() {
        LOG.info("completeTurnAct");
        while (++armyInActIndex < armies.size) {
            armies.get(armyInActIndex).executeTask();
            if (ClientCore.core.isBattleActiveStage()) {
                return false;
            }
        }

        while (++townInActIndex < townFragments.size) {
            townFragments.get(townInActIndex).executeTask();
            if (ClientCore.core.isBattleActiveStage()) {
                return false;
            }
        }

        PathData.resetObstacleFilters();

        LOG.info("completeTurnAct DONE");
        completeTurnInProgress = false;
        return true;
    }

    public void updatedAttitude(Team team, int updateAttitudeOnValue) {
        UiLogger.addInfoLabel(format("%s%d team attitude: %s > %s ",
                (updateAttitudeOnValue > 0 ? "+" : ""), updateAttitudeOnValue, getName(), team.getName()));
        // TODO implement attitude system: map <Team, Integer-attitude>, by default map is empty (0 attitude to all)
    }

    public void clearDefeatedObjects() {
        Iterator<AbstractSquad> iterator = armies.iterator();
        while (iterator.hasNext()) {
            AbstractSquad next = iterator.next();
            if (next.isRemovedFromWorld()) {
                iterator.remove();
            }
        }

        Iterator<TownFragment> townFragmentIterator = townFragments.iterator();
        while (townFragmentIterator.hasNext()) {
            TownFragment next = townFragmentIterator.next();
            if (next.isRemovedFromWorld()) {
                townFragmentIterator.remove();
            }
        }

        // TODO clear on iteration end
        if (!isHumanPlayer() && !isAnimalTeam()) {
            FastAsserts.assertMoreThan(armies.size + townFragments.size, 0);
        }
    }

    public void showDefeatedWindow() {
        defeatedWindowShown = true;
        Gdxg.clientUi.getTeamDefeatedWindow().show();
    }

    public void setLastSelectedObject(AreaObject lastSelectedObject) {
        this.lastSelectedObject = lastSelectedObject;
    }

    public void validateInventoriesAndEquipment() {
        for (AbstractSquad army : armies) {
            army.getMilitaryInventory().reequip();
            army.getCraftInventory().update();
        }

        for (TownFragment townFragment : townFragments) {
            townFragment.getMilitaryInventory().reequip();
            townFragment.getCraftInventory().update();
        }
    }

    public void validateObjectActions() {
        for (AbstractSquad army : armies) {
            army.getActionsController().invalidate();
            army.getActionsController().validate();
        }

        for (TownFragment townFragment : townFragments) {
            townFragment.getActionsController().invalidate();
            townFragment.getActionsController().validate();
        }
    }

    public int getTribeSeparationValue() {
        if (tribeSeparationValue == null) {
            tribeSeparationValue = Utils.RANDOM.nextInt(World.TRIBE_SEPARATION_VALUE_MAX);
        }
        return tribeSeparationValue;
    }

    public void joinSquad(AbstractSquad abstractSquad) {
        LOG.info("{} joins {}", toString(), abstractSquad);
        Team areaObjectTeam = abstractSquad.getTeam();
        areaObjectTeam.removeSquad(abstractSquad);
        this.addSquad(abstractSquad);
    }

    public void setTribeSeparationValue(Integer tribeSeparationValue) {
        this.tribeSeparationValue = tribeSeparationValue;
    }

    public Array<AbstractEventNotification> getNextStepEvents() {
        return nextStepEvents;
    }

}
