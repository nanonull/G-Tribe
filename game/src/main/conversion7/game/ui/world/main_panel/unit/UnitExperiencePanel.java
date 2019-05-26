package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

import static conversion7.game.Assets.*;


public class UnitExperiencePanel extends HBox {

    private final Label label = new Label("", labelStyle14orange2);
    private ProgressBar progressBar;

    public UnitExperiencePanel() {
        addLabel("Experience: ", labelStyle14white2);
        addLabel(label).padRight(ClientUi.SPACING);
        PopupHintPanel.assignHintTo(this, "Experience to next Exp.level");
        progressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(progressBar, ClientUi.SMALL_PROGRESS_BAR_WIDTH);
    }

    public void load(AbstractSquad squad) {
        label.setText(squad.getCurrentLevelExperience() + "/" + squad.getExperienceForNextLevel());
        progressBar.setValue(squad.getExpLevelProgressPercent());
        show();
    }

}
