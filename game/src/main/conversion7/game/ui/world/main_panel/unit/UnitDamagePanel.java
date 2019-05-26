package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.ui.hint.PopupHintPanel;

public class UnitDamagePanel extends HBox {

    private final Label meleeDmgLbl = new Label("-", Assets.labelStyle14orange2);
    private final Label rangeDmgLbl = new Label("-", Assets.labelStyle14orange2);
    Label splitLabel = new Label("/", Assets.labelStyle14white2);

    public UnitDamagePanel() {
        Label preLbl = addLabel(new Label("Damage: ", Assets.labelStyle14white2)).getActor();
        PopupHintPanel.assignHintTo(preLbl, "Basically damage = unit actual power * " + Power2.BASE_DAMAGE_MLT);
        addLabel(meleeDmgLbl);
        PopupHintPanel.assignHintTo(meleeDmgLbl, "Melee damage");
        addLabel(splitLabel);
        addLabel(rangeDmgLbl);
        PopupHintPanel.assignHintTo(rangeDmgLbl, "Range damage");
    }

    public void load(AbstractSquad squad) {
        meleeDmgLbl.setText(String.valueOf(squad.power.getMeleeDamage()));

        Integer rangeDamage = squad.power.getRangeDamage();
        if (rangeDamage == 0) {
            splitLabel.setText("");
            rangeDmgLbl.setText("");
        } else {
            splitLabel.setText("/");
            rangeDmgLbl.setText(String.valueOf(rangeDamage));
        }
    }
}
