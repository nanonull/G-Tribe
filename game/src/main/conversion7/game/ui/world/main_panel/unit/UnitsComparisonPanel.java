package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.ui.ClientUi;

public class UnitsComparisonPanel extends Panel {

    public SelectedUnitPanel selectedUnitTable = new SelectedUnitPanel();
    public MouseOverUnitPanel mouseOverUnitTable = new MouseOverUnitPanel();

    public UnitsComparisonPanel() {
        add(selectedUnitTable).width(UnitParametersBasePanelType1.MAX_WIDTH);
        Image separImage = new Image(Assets.pixel);
        add(separImage).pad(ClientUi.SPACING).expandY().fillY().width(1);
        add(mouseOverUnitTable).width(UnitParametersBasePanelType1.MAX_WIDTH);
    }

}
