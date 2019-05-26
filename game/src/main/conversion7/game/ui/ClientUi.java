package conversion7.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import conversion7.engine.DefaultClientGraphic;
import conversion7.engine.DefaultClientUi;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.ui.ShowTeamUiSystem;
import conversion7.engine.custom2d.AnimatedWindow;
import conversion7.engine.dialog.view.DialogWindow;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.ui.battle.BattleBar;
import conversion7.game.ui.battle.BattleEndWindow;
import conversion7.game.ui.battle.BattleWindowManageArmyForRound;
import conversion7.game.ui.dialogs.AreaObjectCouldNotMoveDialog;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import conversion7.game.ui.inputlisteners.UiInputListener;
import conversion7.game.ui.quest.QuestWindow;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.ui.world.*;
import conversion7.game.ui.world.inventory.InventoryWindow;
import conversion7.game.ui.world.main_panel.CellDetailsRootPanel;
import conversion7.game.ui.world.main_panel.TeamControlsPanel;
import conversion7.game.ui.world.main_panel.WorldHintPanel;
import conversion7.game.ui.world.main_panel.WorldMainWindow;
import conversion7.game.ui.world.split_units.SplitMergeUnitsWindow;
import conversion7.game.ui.world.team_classes.TribeEvolutionWindow;
import conversion7.game.ui.world.team_skills.TeamSkillsWindow;
import org.slf4j.Logger;

public class ClientUi extends DefaultClientUi {

    public static final int SMALL_PROGRESS_BAR_WIDTH = 64;
    public static final int Z_INDEX_CELL_INDICATOR = 10000;
    public static final Group CELL_INDICATORS_LAYER = new Group();
    public static final Group UNIT_IN_WORLD_PANELS_MAIN = new Group();
    public static final Group UNIT_IN_WORLD_PANELS2 = new Group();
    public static final Color PANEL_COLOR = UiUtils.alpha(0.75f, Color.valueOf("#07434f"), false);
    public static final Color PANEL_COLOR_B = UiUtils.alpha(0.9f, PANEL_COLOR, false);
    private static final Logger LOG = Utils.getLoggerForClass();
    public JournalPanel journalPanel;
    boolean teamUiEnabled = true;
    private DebugCellHint debugCellHint;
    private BattleEndWindow battleEndWindow;
    private Console console;
    private BattleWindowManageArmyForRound battleWindowManageArmyForRound;
    private TestBar testBar;
    private TeamDefeatedWindow teamDefeatedWindow;
    private EventsBar eventsBar;
    private TeamSkillsWindow teamSkillsWindow;
    private TribeEvolutionWindow tribeEvolutionWindow;
    private GodsPanel godsPanel;
    private TribeInfoPanel tribeInfoPanel;
    private SplitMergeUnitsWindow splitMergeUnitsWindow;
    private AreaObjectCouldNotMoveDialog areaObjectCouldNotMoveDialog;
    private InventoryWindow inventoryWindow;
    private BattleBar battleBar;
    private QuestWindow questWindow;
    private TeamsTurnsPanel teamsTurnsPanel;
    private WorldMainWindow worldMainWindow;
    private WorldHintPanel worldHintPanel;
    private BaseDescriptionPanel eventDescriptionPanel;
    private DialogWindow dialogWindow;
    private CellDetailsRootPanel cellDetailsRootPanel;
    private TribeResorcesPanel tribeResorcesPanel;
    private DisabledActionsPanel disabledActionsPanel;
    private EventUiNotificationPanel eventUiNotificationPanel;

    public ClientUi(DefaultClientGraphic graphic) {
        super(graphic);
        Gdxg.clientUi = this;
        stageGUI.addListener(new UiInputListener());
        graphic.getGlobalStage().addActor(UNIT_IN_WORLD_PANELS_MAIN);
        graphic.getGlobalStage().addActor(UNIT_IN_WORLD_PANELS2);
        graphic.getGlobalStage().addActor(CELL_INDICATORS_LAYER);

        worldMainWindow = new WorldMainWindow(stageGUI, Assets.uiSkin);

        cellDetailsRootPanel = new CellDetailsRootPanel();
        stageGUI.addActor(cellDetailsRootPanel);
        godsPanel = new GodsPanel(stageGUI);
        tribeInfoPanel = new TribeInfoPanel(stageGUI);
        journalPanel = new JournalPanel(stageGUI);
    }

    public DebugCellHint getDebugCellHint() {
        if (debugCellHint == null) {
            debugCellHint = new DebugCellHint(stageGUI);
        }
        return debugCellHint;
    }

    public BattleEndWindow getBattleEndWindow() {
        if (battleEndWindow == null) {
            battleEndWindow = new BattleEndWindow(stageGUI);
        }
        return battleEndWindow;
    }

    public Console getConsole() {
        if (console == null) {
            console = new Console(stageGUI);
        }
        return console;
    }

    public BattleWindowManageArmyForRound getBattleWindowManageArmyForRound() {
        if (battleWindowManageArmyForRound == null) {
            battleWindowManageArmyForRound = new BattleWindowManageArmyForRound(stageGUI);
        }
        return battleWindowManageArmyForRound;
    }

    public TestBar getTestBar() {
        if (testBar == null) {
            testBar = new TestBar(stageGUI);
        }
        return testBar;
    }

    public TeamDefeatedWindow getTeamDefeatedWindow() {
        if (teamDefeatedWindow == null) {
            teamDefeatedWindow = new TeamDefeatedWindow(stageGUI);
        }
        return teamDefeatedWindow;
    }

    public TeamSkillsWindow getTeamSkillsWindow() {
        if (teamSkillsWindow == null) {
            teamSkillsWindow = new TeamSkillsWindow(stageGUI, TeamSkillsWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return teamSkillsWindow;
    }

    public TribeEvolutionWindow getTribeEvolutionWindow() {
        if (tribeEvolutionWindow == null) {
            tribeEvolutionWindow = new TribeEvolutionWindow(stageGUI, TribeEvolutionWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return tribeEvolutionWindow;
    }

    @Deprecated
    public SplitMergeUnitsWindow getSplitMergeUnitsWindow() {
        if (splitMergeUnitsWindow == null) {
            splitMergeUnitsWindow = new SplitMergeUnitsWindow(stageGUI, SplitMergeUnitsWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return splitMergeUnitsWindow;
    }

    public AreaObjectCouldNotMoveDialog getAreaObjectCouldNotMoveDialog() {
        if (areaObjectCouldNotMoveDialog == null) {
            areaObjectCouldNotMoveDialog = new AreaObjectCouldNotMoveDialog(stageGUI,
                    Assets.uiSkin, AnimatedWindow.Direction.down);
        }
        return areaObjectCouldNotMoveDialog;
    }

    public InventoryWindow getInventoryWindow() {
        if (inventoryWindow == null) {
            inventoryWindow = new InventoryWindow(stageGUI, InventoryWindow.class.getSimpleName(),
                    Assets.uiSkin, AnimatedWindow.Direction.up);
        }
        return inventoryWindow;
    }


    public BattleBar getBattleBar() {
        if (battleBar == null) {
            battleBar = new BattleBar(stageGUI, Assets.uiSkin);
        }
        return battleBar;
    }

    public QuestWindow getQuestWindow() {
        if (questWindow == null) {
            questWindow = new QuestWindow(stageGUI, Assets.uiSkin);
        }
        return questWindow;
    }

    public TeamsTurnsPanel getTeamsTurnsPanel() {
        if (teamsTurnsPanel == null) {
            teamsTurnsPanel = new TeamsTurnsPanel();
            stageGUI.addActor(teamsTurnsPanel);
        }
        return teamsTurnsPanel;
    }

    public TeamControlsPanel getTeamBar() {
        return worldMainWindow.teamControlsPanel;
    }

    public EventsBar getEventsBar() {
        if (eventsBar == null) {
            eventsBar = new EventsBar(stageGUI);
        }
        return eventsBar;
    }

    public WorldMainWindow getWorldMainWindow() {
        return worldMainWindow;
    }

    public WorldHintPanel getWorldHintPanel() {
        if (worldHintPanel == null) {
            worldHintPanel = new WorldHintPanel(stageGUI);
        }
        return worldHintPanel;
    }

    public BaseDescriptionPanel getEventDescriptionPanel() {
        if (eventDescriptionPanel == null) {
            eventDescriptionPanel = new BaseDescriptionPanel();
            stageGUI.addActor(eventDescriptionPanel);
        }
        return eventDescriptionPanel;
    }

    public DialogWindow getDialogWindow() {
        if (dialogWindow == null) {
            dialogWindow = new DialogWindow(stageGUI, Assets.uiSkin);
        }
        return dialogWindow;
    }

    public CellDetailsRootPanel getCellDetailsRootPanel() {
        return cellDetailsRootPanel;
    }

    public GodsPanel getGodsPanel() {
        return godsPanel;
    }

    public TribeInfoPanel getTribeInfoPanel() {
        return tribeInfoPanel;
    }

    public TribeResorcesPanel getTribeResorcesPanel() {
        if (tribeResorcesPanel == null) {
            tribeResorcesPanel = new TribeResorcesPanel(stageGUI);
        }
        return tribeResorcesPanel;
    }

    public DisabledActionsPanel getDisabledActionsPanel() {
        if (disabledActionsPanel == null) {
            disabledActionsPanel = new DisabledActionsPanel();
            stageGUI.addActor(disabledActionsPanel);
        }
        return disabledActionsPanel;
    }

    public EventUiNotificationPanel getEventUiNotificationPanel() {
        if (eventUiNotificationPanel == null) {
            eventUiNotificationPanel = new EventUiNotificationPanel();
            stageGUI.addActor(eventUiNotificationPanel);
        }
        return eventUiNotificationPanel;
    }

    public void setTeamUiEnabled(boolean teamUiEnabled) {
        this.teamUiEnabled = teamUiEnabled;
    }

    public void showWelcomeHint() {
        getInfoDialog().show("Welcome!", "Basic controls:\n " +
                "- move camera: WSAD and Z/X\n " +
                "- select/action: Left Mouse\n " +
                "- dialog/cancel: Right Mouse\n");
    }

    public void showBattleEnd(Battle battle) {
        stageGUI.addActor(new BattleEndWindow(stageGUI));
    }

    public void enableWorldInteraction() {
        Gdxg.getAreaViewer().setInteractionEnabled(true);
        Gdxg.clientUi.setTeamUiEnabled(true);
        ContinuousInput.setEnabled(true);
        Gdxg.clientUi.showWorldManagementInterface();
        Gdxg.clientUi.showTeamUi();
    }

    public void disableWorldInteraction() {
        if (Gdxg.getAreaViewer() == null) {
            LOG.info("skip disableWorldInteraction");
        } else {
            Gdxg.getAreaViewer().setInteractionEnabled(false);
            Gdxg.clientUi.setTeamUiEnabled(false);
            ContinuousInput.setEnabled(false);
            Gdxg.clientUi.hideWorldManagementInterface();
            Gdxg.clientUi.hideTeamUi();
        }
    }

    public void showWorldManagementInterface() {
        worldMainWindow.refresh();
        worldMainWindow.show();
    }

    public void hideWorldManagementInterface() {
        cellDetailsRootPanel.getHighlightedCellPanel().hide();
        worldMainWindow.hide();
    }

    public void hideTeamUi() {
        getTeamBar().hide();
        getEventsBar().hide();
    }

    public void showTeamUi() {
        if (!teamUiEnabled) {
            LOG.info("teamUi was disabled");
            return;
        }

        ShowTeamUiSystem.components.create(Gdxg.core.world.worldEntityId);
    }
}
