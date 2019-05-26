package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;

public class UnitHealthBarPanel extends HBox {

    private final ProgressBar hpProgressBar;

    public UnitHealthBarPanel() {
        hpProgressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_GREEN_RED);
        add(hpProgressBar).width(ClientUi.SMALL_PROGRESS_BAR_WIDTH);
    }

    public void load(AbstractSquad squad) {
        hpProgressBar.setValue(squad.getPowerPercent());
        show();
    }

}
