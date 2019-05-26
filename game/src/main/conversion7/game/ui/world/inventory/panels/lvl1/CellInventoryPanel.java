package conversion7.game.ui.world.inventory.panels.lvl1;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.world.inventory.panels.InventoriesPanel;
import conversion7.game.ui.world.inventory.panels.lvl1.lvl2.InventoryItemPanel;

public class CellInventoryPanel extends BaseInventoryPanel {


    public CellInventoryPanel(InventoriesPanel inventoriesPanel) {
        super(inventoriesPanel);
    }

    public void load(AbstractSquad squad) {
        Table table = scrollableTable.table;
        table.clearChildren();
        BasicInventory inventory = squad.getLastCell().getInventory();
        if (inventory.isEmpty()) {
            table.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : inventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemPanel inventoryItemPanel = new InventoryItemPanel(inventoryItem);
                table.add(inventoryItemPanel);
                inventoryItemPanel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (squad.canPickUpItem(inventoryItem)) {
                            squad.getInventoryController().moveItemFromCellToMainInventory(inventoryItem);
                            inventoriesPanel.load(squad);
                        }
                    }
                });
                table.row();
            }
        }
    }
}
