package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.hint.PopupHintPanel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class UnitHitChancePanel extends HBox {

    public static final NumberFormat PERCENT_FORMATTER = new DecimalFormat("#0");
    private final Label meleeDmgLbl = new Label("-", Assets.labelStyle14orange2);
    private final Label rangeDmgLbl = new Label("-", Assets.labelStyle14orange2);
    Label splitLabel = new Label("/", Assets.labelStyle14white2);

    public UnitHitChancePanel() {
        addLabel(new Label("Hit chance: ", Assets.labelStyle14white2));
        addLabel(meleeDmgLbl);
        PopupHintPanel.assignHintTo(meleeDmgLbl, "Melee hit chance");
        addLabel(splitLabel);
        addLabel(rangeDmgLbl);
        PopupHintPanel.assignHintTo(rangeDmgLbl, "Range hit chance");
    }

    public void load(AbstractSquad squad) {
        int unitHitChance = squad.getMeleeWeaponHitChance();
        meleeDmgLbl.setText(hitChanceToPercent(unitHitChance));

        if (squad.canRangeAttack()) {
            Integer rangeChance = squad.getRangeWeaponHitChance();
            splitLabel.setText("/");
            rangeDmgLbl.setText(hitChanceToPercent(rangeChance));
        } else {
            splitLabel.setText("");
            rangeDmgLbl.setText("");
        }
    }

    private String hitChanceToPercent(int chance) {
        return UnitHitChancePanel.PERCENT_FORMATTER.format(chance) + "%";
    }
}
