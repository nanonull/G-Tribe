package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class ActionPointsIndicatorsPanel extends HBox {
    private static final float MAGIC_PAD = -7;
    Label moveApLbl;
    Label attackApLbl;

    public ActionPointsIndicatorsPanel() {
        setBackground(new TextureRegionColoredDrawable(UnitIconWithInfoPanel.LIGHT_PANEL_COLOR, Assets.pixel));
        pad(1);
        moveApLbl = new Label("", Assets.labelStyle12_i_whiteAndLittleGreen);
        addLabel(moveApLbl).padTop(UnitIconWithInfoPanel.MAGIC_LABEL_TOP_PAD);
        moveApLbl.setColor(Color.BLUE);

        attackApLbl = new Label("", Assets.labelStyle12_i_whiteAndLittleGreen);
        addLabel(attackApLbl).padTop(UnitIconWithInfoPanel.MAGIC_LABEL_TOP_PAD);
        attackApLbl.setColor(Color.SCARLET);
        hide();
    }

    public void updateAP(int moveAp, int attackAp) {
        moveApLbl.setText(getApText(moveAp, AbstractSquad.START_MOVE_AP));
        attackApLbl.setText(getApText(attackAp, AbstractSquad.START_ATTACK_AP));
        show();
    }

    private String getApText(int ap, int max) {
        if (ap < 0) {
            return "~";
        } else if (ap == 0) {
            return "";
        } else if (ap == 1) {
            return "o";
        } else if (ap == 2) {
            return "oo";
        } else {
            return "oo+";
        }
    }
}
