package conversion7.game.ui.world.inventory.panels;

import conversion7.game.ui.world.main_panel.unit.UnitParametersBasePanelType1;

public class UnitAllParametersPanel extends UnitParametersBasePanelType1 {
    public UnitAllParametersPanel() {
        showHitChance = true;
        showUnitLevel = true;
        showExp = true;
        showAge = true;
        showSoul = true;
        showBloodline = true;
        init();
    }
}
