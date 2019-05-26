package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class UnitAgePanel extends HBox {

    private final Label label = new Label("-", Assets.labelStyle14orange2);
    private final ProgressBar progressBar;

    public UnitAgePanel() {
        addLabel("Age: ", Assets.labelStyle14white2);
        addLabel(label).padRight(ClientUi.SPACING);
        progressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(progressBar, ClientUi.SMALL_PROGRESS_BAR_WIDTH).padRight(ClientUi.SPACING);
        PopupHintPanel.assignHintTo(this, "Unit age level\n \nIt grows with world turns." +
                "\nThere are 4 levels: Young, Adult, Mature and Old." +
                "\nUnit has chance to die of age after " + Unit.DIES_AT_AGE_STEP + " turns");
    }

    public void load(AbstractSquad squad) {
        label.setText(getAgeProgressLabel(squad.unit) + squad.getAge().name());
        progressBar.setValue(squad.getAgePercent());
        show();
    }

    private String getAgeProgressLabel(Unit unit) {
        if (unit.squad.willDieOfAge) {
            return "";
        } else {
            return unit.squad.getAgeStep() + "/" + Unit.DIES_AT_AGE_STEP + " ";
        }
    }

}
