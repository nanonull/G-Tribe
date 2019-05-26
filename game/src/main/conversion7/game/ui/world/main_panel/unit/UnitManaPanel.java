package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

import static conversion7.game.Assets.*;


public class UnitManaPanel extends HBox {

    private final Label label = new Label("", labelStyle14orange2);
    private ProgressBar bar;

    public UnitManaPanel() {
        addLabel("Mana: ", labelStyle14white2);
        addLabel(label).padRight(ClientUi.SPACING);
        PopupHintPanel.assignHintTo(label, "Mana points");
        bar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(bar, ClientUi.SMALL_PROGRESS_BAR_WIDTH);
    }

    public void load(AbstractSquad squad) {
        label.setText(squad.getMana() + "/" + Unit.MANA_MAX);
        bar.setValue(squad.getManaPercent());
        show();
    }

}
