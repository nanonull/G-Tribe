package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.main_panel.unit.UnitParametersBasePanelType1;

public class TeamsTurnsPanel extends VBox {

    private static final int BAR_WIDTH = ClientUi.SMALL_PROGRESS_BAR_WIDTH * 4;
    private final ProgressBar teamsProgressBar;
    private final ProgressBar unitsProgressBar;
    Label teamName;

    public TeamsTurnsPanel() {
        hide();
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
        defaults().center().space(2);
        pad(4);

        addLabel("Calculating events to generate history", Assets.labelStyle14white2).getActor();
        teamName = addLabel("", Assets.labelStyle14orange2).center().getActor();
        teamsProgressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(teamsProgressBar, BAR_WIDTH).padRight(ClientUi.SPACING);

        unitsProgressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(unitsProgressBar, BAR_WIDTH).padRight(ClientUi.SPACING);
        pack();
    }

    @Override
    public void validate() {
        super.validate();
        setPosition(GdxgConstants.SCREEN_WIDTH_IN_PX / 2 - getWidth() / 2,
                GdxgConstants.SCREEN_HEIGHT_IN_PX - ClientUi.DOUBLE_SPACING * 4 - getHeight());
    }

    public void newTeamStarted(World world) {
        teamName.setText(world.activeTeam.getName());

        teamsProgressBar.setRange(0, world.teams.size);
        int teamIndex = world.teams.indexOf(world.activeTeam, true);
        teamsProgressBar.setValue(teamIndex);

        unitsProgressBar.setRange(0, world.activeTeam.getSquads().size);
        unitsProgressBar.setValue(0);

    }

    public void newSquadStarted() {
        unitsProgressBar.setValue(unitsProgressBar.getValue() + 1);
    }

    public void newCompStarted() {
        newSquadStarted();
    }
}
