package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.hint.PopupHintPanel;

public class PowerPanel extends Panel {
    Label powerLbl;

    public PowerPanel() {
        addLabel(new Label("Power: ", Assets.labelStyle14orange));

        row();
        powerLbl = new Label("-", Assets.labelStyle14orange2);
        addLabel(powerLbl);
        PopupHintPanel.assignHintTo(powerLbl, "Current/Max power.");
    }

    public void load(AbstractSquad squad) {
        powerLbl.setText("Power: " + squad.getCurrentPower() + "/" + squad.getMaxPower());
        show();
    }
}
