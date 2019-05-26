package conversion7.game.ui.world.main_panel.unit;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class MouseOverUnitPanel extends UnitParametersBasePanelType1 {

    public MouseOverUnitPanel() {
        showHitChance = true;
        init();
    }

    @Override
    public void load(AbstractSquad squad) {
        if (squad == null) {
            hide();
        } else {
            super.load(squad);
        }
    }
}
