package conversion7.game.ui.world.main_panel;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import conversion7.engine.AudioPlayer;
import conversion7.engine.DefaultClientUi;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.BattleSystem;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.actions.AbstractTeamAction;
import conversion7.game.stages.world.team.actions.SelectNextTeamObjectAction;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.hint.PopupHintPanel;

public class TeamControlsPanel extends Panel {

    public static final int BUTTON_SIZE = 40;
    private final Label defeatedLabel;
    Label evolutionPointsLabel;
    Label teamNameLabel;
    TextButton nextUnitBut;
    private Table infoTable = new Table();
    private Table actionsTable = new Table();
    private Team team;

    public TeamControlsPanel() {
        addEndTurnButtons();

        row();
        add(actionsTable).padTop(4).fill().expand();

        row();
        add(infoTable);

        teamNameLabel = new Label("", Assets.labelStyle14orange);

        defeatedLabel = new Label("DEFEATED", Assets.labelStyle14red);
        defeatedLabel.setColor(Color.SCARLET);

        evolutionPointsLabel = new Label("", Assets.labelStyle14orange);
    }

    private void addEndTurnButtons() {
        HBox row = new HBox();
        add(row).grow().pad(ClientUi.SPACING).height(DefaultClientUi.EXPANDED_BUTTON_HEIGHT * 1.3f);
        row.defaults().padRight(5);

        TextButton nextTeamBut = new TextButton("End Turn", Assets.uiSkin);
        nextTeamBut.setColor(Color.GOLD);
        row.add(nextTeamBut).center().grow();

        nextTeamBut.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdxg.core.world.isBattleActive()) {
                    Gdxg.core.world.getActiveBattle().getActiveSquad().showFloatingLabel("Fight is active", Color.YELLOW);
                    return;
                }
                if (AnimationSystem.isLocking()) {
                    UiLogger.addInfoLabel("Animation is active");
                    return;
                }

                int button = event.getButton();
                AbstractSquad activeUnit = null;
                // 2019-04-26 force end turn enabled (ignore active units)
//                activeUnit = getNextUnitToAct(Gdxg.core.world.activeTeam);
                if (activeUnit == null) {
                    Gdxg.core.world.requestNextTeamTurn();
                } else {
                    SelectNextTeamObjectAction.focusOn(activeUnit);
                    AudioPlayer.play("fx\\click1.mp3");
                    activeUnit.batchFloatingStatusLines.addLine("I want to do something..");
                }
            }
        });

        nextUnitBut = new TextButton("Next action", Assets.uiSkin);
        nextUnitBut.setColor(Color.GREEN);
        row.add(nextUnitBut).fill().growY();
        PopupHintPanel.assignHintTo(nextUnitBut, "Select active unit OR Proceed to next unit in battle");
        nextUnitBut.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tryActivateNext(team);
            }

        });
    }

    public void tryActivateNext(Team team) {
        if (team.world.isBattleActive()) {
            Gdxg.core.artemis.getSystem(BattleSystem.class).nextSquad();
        } else {
            AbstractSquad nextUnitToAct = getNextUnitToAct(team);
            if (nextUnitToAct != null) {
                SelectNextTeamObjectAction.focusOn(nextUnitToAct);
            } else {
                disableActiveUnitIndicator();
            }
        }
    }

    private AbstractSquad getNextUnitToAct(Team team) {
        for (AbstractSquad squad : team.getSquads()) {
            if (!squad.isAlive()){
                continue;
            }
            boolean unitHasAp = squad.getMoveAp() > 0 || squad.getAttackAp() > 0;
            if (unitHasAp && !squad.skipTurn && squad.hasActiveAction()) {
                return squad;
            }
        }

        return null;
    }

    public void showFor(Team team) {
        this.team = team;
        refreshContent(team);
        show();
    }

    public void refreshContent(Team team) {
//        displayTeamInfo(team);
        displayTeamActions(team);
        refreshActiveUnitIndicator(team);
        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX - getWidth() - ClientUi.SPACING,
                GdxgConstants.SCREEN_HEIGHT_IN_PX - getHeight() - ClientUi.SPACING);
    }

    private void refreshActiveUnitIndicator(Team team) {
        AbstractSquad nextUnitToAct = getNextUnitToAct(team);
        if (nextUnitToAct == null) {
            disableActiveUnitIndicator();
        } else {
            nextUnitBut.setColor(Color.GREEN);
        }
    }

    private void disableActiveUnitIndicator() {
        nextUnitBut.setColor(Color.GRAY);
    }

    private void displayTeamInfo(Team team) {
        infoTable.clearChildren();
        infoTable.defaults().left().top();

        teamNameLabel.setText("Team: " + team.getName());
        infoTable.add(teamNameLabel);
        infoTable.row();
        if (team.isDefeated()) {
            infoTable.add(defeatedLabel);
        } else {
            evolutionPointsLabel.setText("Evolution points: " + team.getEvolutionPoints());
            infoTable.add(evolutionPointsLabel);
            infoTable.row();
        }
    }


    private void displayTeamActions(Team team) {
        actionsTable.clearChildren();
        actionsTable.defaults().left().top().fill().expand();

        if (team.isDefeated()) {
            return;
        }

        for (final AbstractTeamAction teamAction : team.getActions()) {
            TextButton button = new TextButton(teamAction.getUiName(), Assets.uiSkin);
            actionsTable.add(button)
                    .pad(0)
                    .padLeft(ClientUi.DOUBLE_SPACING)
                    .padRight(ClientUi.DOUBLE_SPACING)
                    .fill().expand()
                    .center();
            actionsTable.row();

            PopupHintPanel.assignHintTo(button, teamAction.getHint());
            button.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() == Input.Buttons.LEFT) {
                        teamAction.action();
                    }
                }

            });
        }

    }
}
