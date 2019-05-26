package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.ClientUi;

public class TribeInfoPanel extends VBox {

    public static ProgressBar.ProgressBarStyle BAR_STYLE;
    private Stage stageGUI;
    private final ProgressBar evolutionSubProgressBar;

    static {
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Color.GRAY, Assets.pixel);
        BAR_STYLE = new ProgressBar.ProgressBarStyle(backDrawable, knobDrawable);
        BAR_STYLE.knob.setMinHeight(10);
        BAR_STYLE.knobBefore = BAR_STYLE.knob;
        BAR_STYLE.background.setMinHeight(8);
    }

    public TribeInfoPanel(Stage stageGUI) {
        this.stageGUI = stageGUI;
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
        evolutionSubProgressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);
    }

    public void showFor(Team team) {
        clearChildren();
        addSmallCloseButton();

        addLabel("Team: " + team.getName(), Assets.labelStyle14white2);
        if (team.isDefeated()) {
            addLabel("Defeated!", Assets.labelStyle14red);
        }

        addSpaceLine();
        addLabel("Evolution points: " + team.getEvolutionPoints(), Assets.labelStyle14orange);
        addLabel("Evolution point progress: ", Assets.labelStyle14orange);
        int percent = (int) ((team.getEvolutionExperience() / (float) Team.EVOLUTION_EXP_PER_EVOLUTION_POINT) * 100);
        evolutionSubProgressBar.setValue(percent);
        add(evolutionSubProgressBar).width(100).left();

        addSpaceLine();
        addLabel("Faith points (God exp): " + team.godExp, Assets.labelStyle14orange);


        stageGUI.addActor(this);
        pack();
        setY(stageGUI.getHeight() - getHeight());
    }
}
