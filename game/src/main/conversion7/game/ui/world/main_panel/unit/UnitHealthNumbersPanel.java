package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.world.HealthWidgetBar;

import static conversion7.game.Assets.*;

public class UnitHealthNumbersPanel extends HBox {

    public static Label.LabelStyle defLblStyle;
    final Label actHpLbl = new Label("-", Assets.labelStyle14orange2);
    final Label maxHpLbl = new Label("-", Assets.labelStyle14orange2);
    final Label defLbl = new Label("", HealthWidgetBar.DEF_LBL_STYLE);

    static {
        defLblStyle = new Label.LabelStyle(labelStyle14white2);
        defLblStyle.fontColor = Color.BLACK;
        defLblStyle.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixel);
    }

    public UnitHealthNumbersPanel() {
        addLabel(actHpLbl);
        PopupHintPanel.assignHintTo(actHpLbl, "Actual unit power");
        addLabel("/", labelStyle14white2);
        addLabel(maxHpLbl);
        PopupHintPanel.assignHintTo(maxHpLbl, "Max unit power");

        add().width(ClientUi.SPACING);
        addLabel(defLbl);
        PopupHintPanel.assignHintTo(defLbl, "Defence HP");
    }

    public static void updateDefLbl(int armor, Label defLbl) {
        if (armor > 0) {
            defLbl.setText("+" + String.valueOf(armor));
        } else {
            defLbl.setText("");
        }
    }

    public void load(AbstractSquad squad) {
        actHpLbl.setText(String.valueOf(squad.getCurrentPower()));
        maxHpLbl.setText(String.valueOf(squad.getMaxPower()));
        updateDefLbl(squad.getTotalArmor(), defLbl);
        show();
    }

}
