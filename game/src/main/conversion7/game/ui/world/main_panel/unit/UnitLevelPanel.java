package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class UnitLevelPanel extends HBox {

    public static final String EXP_HINT = "Unit experience level\n \nIt grows with experience." +
            "\nUnit can be promoted to hero on 2nd level" +
            "\nEach level increases unit power" +
            "\nExperience to get new level = " + Unit.BASE_EXP_FOR_LEVEL + " * Unit-level";
    private final Label classLvlLbl = new Label("-", Assets.labelStyle14orange2);
    private final Label unitLvlLbl = new Label("-", Assets.labelStyle14orange2);

    public UnitLevelPanel() {
        addLabel("Class lvl: ", Assets.labelStyle14white2);
        addLabel(classLvlLbl).padRight(ClientUi.DOUBLE_SPACING);
        PopupHintPanel.assignHintTo(classLvlLbl, "Class level\n \nIt means level of evolution." +
                "\nCan not be changed on unit. Fertilize units to create new unit classes.");
        addLabel(new Label("Exp lvl:", Assets.labelStyle14white2));
        addLabel(unitLvlLbl);
        PopupHintPanel.assignHintTo(unitLvlLbl, EXP_HINT);
    }

    public void load(AbstractSquad squad) {
        classLvlLbl.setText(String.valueOf(squad.getClassLevel()));
        unitLvlLbl.setText(String.valueOf(squad.getExpLevelUi()));
        show();
    }
}
