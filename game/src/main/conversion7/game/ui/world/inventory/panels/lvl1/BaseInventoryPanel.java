package conversion7.game.ui.world.inventory.panels.lvl1;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import conversion7.engine.custom2d.VBox;
import conversion7.engine.custom2d.table.Panel;
import conversion7.engine.custom2d.table.ScrollableTable;
import conversion7.game.Assets;
import conversion7.game.ui.world.inventory.panels.InventoriesPanel;

public abstract class BaseInventoryPanel extends VBox {

    InventoriesPanel inventoriesPanel;
    public ScrollableTable<Panel> scrollableTable;

    public BaseInventoryPanel(InventoriesPanel inventoriesPanel) {
        this.inventoriesPanel = inventoriesPanel;
        Label label;
        label = new Label(getClass().getSimpleName(), Assets.labelStyle14orange);
        label.setAlignment(Align.center);
        add(label).fill();

        scrollableTable = new ScrollableTable<>(new Panel(), Assets.uiSkin);
        add(scrollableTable).height(200).center();
        scrollableTable.setScrollingDisabled(true, false);
    }

}
