package conversion7.game.ui.world.inventory.panels.lvl1;

import conversion7.engine.custom2d.VBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class IconPanel extends VBox {

    public IconPanel() {
    }

    public void load(AbstractSquad squad) {
        clear();
        add(squad.getClassIconImage()).size(100);
    }
}
