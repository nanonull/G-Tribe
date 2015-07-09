package conversion7.game.ui;

import conversion7.engine.DefaultClientGraphic;
import conversion7.engine.DefaultClientUi;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.battle.BattleBar;
import conversion7.game.ui.battle.BattleEndWindow;
import conversion7.game.ui.battle.BattleWindowManageArmyForRound;
import conversion7.game.ui.dialogs.AreaObjectCouldNotMoveDialog;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import conversion7.game.ui.inputlisteners.UiInputListener;
import conversion7.game.ui.quest.QuestWindow;
import conversion7.game.ui.world.DebugCellHint;
import conversion7.game.ui.world.EventsBar;
import conversion7.game.ui.world.HighlightedCellBar;
import conversion7.game.ui.world.TeamBar;
import conversion7.game.ui.world.TeamDefeatedWindow;
import conversion7.game.ui.world.TribesSeparationBar;
import conversion7.game.ui.world.areaobject_activated.ActionsBar;
import conversion7.game.ui.world.areaobject_activated.AreaObjectDetailsBar;
import conversion7.game.ui.world.areaobject_activated.CellBar;
import conversion7.game.ui.world.army_overview.ArmyOverviewWindow;
import conversion7.game.ui.world.inventory.InventoryWindow;
import conversion7.game.ui.world.split_units.SplitMergeUnitsWindow;
import conversion7.game.ui.world.team_classes.TeamClassesWindow;
import conversion7.game.ui.world.team_skills.TeamSkillsWindow;
import org.slf4j.Logger;
import org.testng.Assert;

public class ClientUi extends DefaultClientUi {

    private static final Logger LOG = Utils.getLoggerForClass();


    public ClientUi(DefaultClientGraphic defaultClientGraphic) {
        super(defaultClientGraphic);
        Gdxg.clientUi = this;
        stageGUI.addListener(new UiInputListener());
        highlightedCellBar = new HighlightedCellBar(stageGUI);
        areaObjectDetailsBar = new AreaObjectDetailsBar(stageGUI);
        actionsBar = new ActionsBar(stageGUI);
        cellBar = new CellBar(stageGUI);
    }


    // =================================================================================================
    // main windows / panels / bars:
    // =================================================================================================

    public void showWelcomeHint() {
        getInfoDialog().show("Welcome!", "Basic controls:\n " +
                "- move camera: WSAD\n " +
                "- select: Left Mouse\n " +
                "- act: Right Mouse\n\n" +
                "Console: F1\n" +
                "Dev mode: F2 > Dev mode");
    }

    private DebugCellHint debugCellHint;

    public DebugCellHint getDebugCellHint() {
        if (debugCellHint == null) {
            debugCellHint = new DebugCellHint(stageGUI);
        }
        return debugCellHint;
    }

    //

    private ArmyOverviewWindow armyOverviewWindow;

    public ArmyOverviewWindow getArmyOverviewWindow() {
        if (armyOverviewWindow == null) {
            armyOverviewWindow = new ArmyOverviewWindow(stageGUI);
        }
        return armyOverviewWindow;
    }

    //

    public void showBattleEnd(Battle battle) {
        stageGUI.addActor(new BattleEndWindow(stageGUI));
    }

    private BattleEndWindow battleEndWindow;

    public BattleEndWindow getBattleEndWindow() {
        if (battleEndWindow == null) {
            battleEndWindow = new BattleEndWindow(stageGUI);
        }
        return battleEndWindow;
    }

    //

    private Console console;

    public Console getConsole() {
        if (console == null) {
            console = new Console(stageGUI);
        }
        return console;
    }

    //

    private BattleWindowManageArmyForRound battleWindowManageArmyForRound;

    public BattleWindowManageArmyForRound getBattleWindowManageArmyForRound() {
        if (battleWindowManageArmyForRound == null) {
            battleWindowManageArmyForRound = new BattleWindowManageArmyForRound(stageGUI);
        }
        return battleWindowManageArmyForRound;
    }

    //

    private TestBar testBar;

    public TestBar getTestBar() {
        if (testBar == null) {
            testBar = new TestBar();
        }
        return testBar;
    }

    // SELECTED OBJECT BARS

    public void showBarsForSelectedObject() {
        AreaObject selectedObject = World.getAreaViewer().selectedObject;
        Assert.assertNotNull(selectedObject);
        highlightedCellBar.hide();
        areaObjectDetailsBar.showFor(selectedObject);
        actionsBar.showFor(selectedObject);
        cellBar.showFor(selectedObject);
    }

    public void hideBarsForSelectedObject() {
        World.getAreaViewer().showHintOnCell(false);
        getAreaObjectDetailsBar().hide();
        getActionsBar().hide();
        getCellBar().hide();
    }

    private ActionsBar actionsBar;

    public ActionsBar getActionsBar() {
        return actionsBar;
    }

    private CellBar cellBar;

    public CellBar getCellBar() {
        return cellBar;
    }

    private AreaObjectDetailsBar areaObjectDetailsBar;

    public AreaObjectDetailsBar getAreaObjectDetailsBar() {
        return areaObjectDetailsBar;
    }

    // HIGHLIGHTED CELL

    private HighlightedCellBar highlightedCellBar;

    public HighlightedCellBar getHighlightedCellBar() {
        return highlightedCellBar;
    }

    //

    private TeamDefeatedWindow teamDefeatedWindow;

    public TeamDefeatedWindow getTeamDefeatedWindow() {
        if (teamDefeatedWindow == null) {
            teamDefeatedWindow = new TeamDefeatedWindow(stageGUI);
        }
        return teamDefeatedWindow;
    }

    //

    boolean teamUiEnabled = true;

    public void setTeamUiEnabled(boolean teamUiEnabled) {
        this.teamUiEnabled = teamUiEnabled;
    }

    public void showTeamUi() {
        if (!teamUiEnabled) {
            LOG.info("teamUi was disabled");
            return;
        }

        Team activeTeam = World.getActiveTeam();
        if (activeTeam.isHumanPlayer()) {
            if (!activeTeam.isDefeated()) {
                Gdxg.clientUi.getEventsBar().showFor(activeTeam);
            }
            Gdxg.clientUi.getTeamBar().showFor(activeTeam);
        }
    }

    public void hideTeamUi() {
        getTeamBar().hide();
        getEventsBar().hide();
    }

    private TeamBar teamBar;

    public TeamBar getTeamBar() {
        if (teamBar == null) {
            teamBar = new TeamBar(stageGUI);
        }
        return teamBar;
    }

    //

    private EventsBar eventsBar;

    public EventsBar getEventsBar() {
        if (eventsBar == null) {
            eventsBar = new EventsBar(stageGUI);
        }
        return eventsBar;
    }

    //

    private TeamSkillsWindow teamSkillsWindow;

    public TeamSkillsWindow getTeamSkillsWindow() {
        if (teamSkillsWindow == null) {
            teamSkillsWindow = new TeamSkillsWindow(stageGUI, TeamSkillsWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return teamSkillsWindow;
    }

    //

    private TeamClassesWindow teamClassesWindow;

    public TeamClassesWindow getTeamClassesWindow() {
        if (teamClassesWindow == null) {
            teamClassesWindow = new TeamClassesWindow(stageGUI, TeamClassesWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return teamClassesWindow;
    }

    //

    private SplitMergeUnitsWindow splitMergeUnitsWindow;

    public SplitMergeUnitsWindow getSplitMergeUnitsWindow() {
        if (splitMergeUnitsWindow == null) {
            splitMergeUnitsWindow = new SplitMergeUnitsWindow(stageGUI, SplitMergeUnitsWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return splitMergeUnitsWindow;
    }

    //

    private AreaObjectCouldNotMoveDialog areaObjectCouldNotMoveDialog;

    public AreaObjectCouldNotMoveDialog getAreaObjectCouldNotMoveDialog() {
        if (areaObjectCouldNotMoveDialog == null) {
            areaObjectCouldNotMoveDialog = new AreaObjectCouldNotMoveDialog(stageGUI,
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return areaObjectCouldNotMoveDialog;
    }

    //

    private InventoryWindow inventoryWindow;

    public InventoryWindow getInventoryWindow() {
        if (inventoryWindow == null) {
            inventoryWindow = new InventoryWindow(stageGUI, InventoryWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.up);
        }
        return inventoryWindow;
    }

    //

    private BattleBar battleBar;

    public BattleBar getBattleBar() {
        if (battleBar == null) {
            battleBar = new BattleBar(stageGUI, Assets.uiSkin);
        }
        return battleBar;
    }

    //

    private QuestWindow questWindow;

    public QuestWindow getQuestWindow() {
        if (questWindow == null) {
            questWindow = new QuestWindow(stageGUI, Assets.uiSkin);
        }
        return questWindow;
    }

    //

    private TribesSeparationBar tribesSeparationBar = new TribesSeparationBar(stageGUI, Assets.uiSkin);

    public TribesSeparationBar getTribesSeparationBar() {
        return tribesSeparationBar;
    }

    // ==========================================================================================================
    public void disableWorldInteraction() {
        if (World.getAreaViewer() == null) {
            LOG.info("skip disableWorldInteraction");
        } else {
            World.getAreaViewer().setInteractionEnabled(false);
            Gdxg.clientUi.setTeamUiEnabled(false);
            ContinuousInput.setEnabled(false);
            Gdxg.clientUi.hideBarsForSelectedObject();
            Gdxg.clientUi.hideTeamUi();
        }
    }

    public void enableWorldInteraction() {
        World.getAreaViewer().setInteractionEnabled(true);
        Gdxg.clientUi.setTeamUiEnabled(true);
        ContinuousInput.setEnabled(true);
        if (World.getAreaViewer().selectedObject != null) {
            World.getAreaViewer().refreshAndUnhideSelection();
        }
        Gdxg.clientUi.showTeamUi();
    }
}
