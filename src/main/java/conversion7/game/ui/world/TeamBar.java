package conversion7.game.ui.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.DefaultClientUi;
import conversion7.engine.custom2d.ButtonWithActor;
import conversion7.engine.custom2d.CustomWindow;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.actions.AbstractTeamAction;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.HintForm;

public class TeamBar extends CustomWindow {

    public static final int BUTTON_SIZE = 40;
    public static ProgressBar.ProgressBarStyle BAR_STYLE;

    static {
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixelWhite);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Color.GRAY, Assets.pixelWhite);
        BAR_STYLE = new ProgressBar.ProgressBarStyle(backDrawable, knobDrawable);
        BAR_STYLE.knob.setMinHeight(10);
        BAR_STYLE.knobBefore = BAR_STYLE.knob;
        BAR_STYLE.background.setMinHeight(8);
    }

    private Table infoTable = new Table();
    private Table actionsTable = new Table();

    private final Label defeatedLabel;
    private ProgressBar evolutionSubProgressBar;
    Label evolutionPointsLabel;
    Label teamNameLabel;


    public TeamBar(Stage linkedStage) {
        super(linkedStage, "TeamBar", Assets.uiSkin);

        add(infoTable);
        row();
        add(actionsTable).padTop(8);

        row();
        TextButton nextTeam = new TextButton("Next Turn", Assets.uiSkin);
        add(nextTeam).pad(ClientUi.SPACING).height(DefaultClientUi.BUTTON_HEIGHT).fill();

        nextTeam.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                World.nextTeamTurn();
            }
        });

        teamNameLabel = new Label("", Assets.labelStyle14_whiteAndLittleGreen);

        defeatedLabel = new Label("DEFEATED", Assets.labelStyle14_whiteAndLittleGreen);
        defeatedLabel.setColor(Color.RED);

        evolutionPointsLabel = new Label("", Assets.labelStyle14_whiteAndLittleGreen);
        evolutionSubProgressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);

    }

    public void showFor(Team team) {
        refreshContent(team);
        show();
    }

    public void refreshContent(Team team) {
        displayTeamInfo(team);
        displayTeamActions(team);
        pack();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX - getWidth() - ClientUi.SPACING,
                GdxgConstants.SCREEN_HEIGHT_IN_PX - getHeight() - ClientUi.SPACING);
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

            int teamEvolutionSubPoints = team.getEvolutionSubpoints();
            int healthPercent = (int) ((teamEvolutionSubPoints / (float) Team.EVOLUTION_SUB_POINTS_PER_POINT) * 100);
            evolutionSubProgressBar.setValue(healthPercent);
            infoTable.add(evolutionSubProgressBar).width(100).right();

        }
    }


    private void displayTeamActions(Team team) {
        actionsTable.clearChildren();
        actionsTable.defaults().left().top();

        if (team.isDefeated()) {
            return;
        }

        for (final AbstractTeamAction teamAction : team.getActions()) {
            ButtonWithActor button = new ButtonWithActor(new Image(teamAction.getIconTexture()));
            actionsTable.add(button).width(BUTTON_SIZE).height(BUTTON_SIZE).pad(ClientUi.SPACING);

            // clear previous click and hint listeners:
            button.getListeners().clear();
            HintForm.assignHintTo(button, teamAction.getHint());
            button.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return button == Input.Buttons.LEFT;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!Gdx.input.isTouched()) {
                        teamAction.action();
                    }
                }
            });
        }

    }
}
