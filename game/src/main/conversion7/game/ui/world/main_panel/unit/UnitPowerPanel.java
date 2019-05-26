package conversion7.game.ui.world.main_panel.unit;

import conversion7.engine.custom2d.HBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;


@Deprecated
public class UnitPowerPanel extends HBox {

    public UnitHealthNumbersPanel unitHealthNumbersPanel = new UnitHealthNumbersPanel();
    public UnitHealthBarPanel unitHealthBarPanel = new UnitHealthBarPanel();

    public void load(AbstractSquad squad) {
        clearChildren();

        addLabel("Power: ", Assets.labelStyle14white2);
        add(unitHealthBarPanel).center();
        add(unitHealthNumbersPanel).padRight(ClientUi.SPACING);

        unitHealthNumbersPanel.load(squad);
        unitHealthBarPanel.load(squad);
        show();
    }

}
