package conversion7.game.ui.world.inventory.panels.lvl1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.list_menu.ListMenu;
import conversion7.game.ui.list_menu.MenuItem;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.ui.world.inventory.panels.InventoriesPanel;
import conversion7.game.ui.world.inventory.panels.lvl1.lvl2.InventoryItemPanel;

import java.util.ArrayList;
import java.util.List;

public class TeamInventoryPanel extends BaseInventoryPanel {


    private static final Color A_RED = UiUtils.alpha(0.2f, Color.RED);
    private static final Color A_GREEN = UiUtils.alpha(0.1f, Color.GREEN);

    public TeamInventoryPanel(InventoriesPanel inventoriesPanel) {
        super(inventoriesPanel);
    }

    public void load(AbstractSquad squad) {
        Table table = scrollableTable.table;
        table.clear();
        BasicInventory inventory = squad.getInventory();
        if (inventory.isEmpty()) {
            table.add(new Label(" - inventory is empty - ", Assets.labelStyle14_lightGreen));

        } else {
            for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                    : inventory.getItemsIterator()) {
                final AbstractInventoryItem inventoryItem = itemEntry.value;
                InventoryItemPanel inventoryItemPanel = new InventoryItemPanel(inventoryItem);
                table.add(inventoryItemPanel);

                boolean couldEquip = squad.getEquipment().couldEquip(inventoryItem);
                boolean couldConsume = squad.getEquipment().couldConsume(inventoryItem);
                if (couldEquip || couldConsume) {
                    inventoryItemPanel.setBackground(new TextureRegionColoredDrawable(A_GREEN, Assets.pixel));
                } else {
                    if (inventoryItem.isEquipment() || inventoryItem.isFood()) {
                        inventoryItemPanel.setBackground(new TextureRegionColoredDrawable(A_RED, Assets.pixel));
                    }
                }
                inventoryItemPanel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        List<MenuItem> menuItems = new ArrayList<>();
                        menuItems.add(new MenuItem("Exit", () -> {
                        }));
                        if (couldEquip) {
                            menuItems.add(new MenuItem("Equip", () -> {
                                squad.getEquipment().equip(inventory, inventoryItem);
                                inventoriesPanel.inventoryWindow.refresh(squad);

                            }));
                        }
                        if (couldConsume) {
                            menuItems.add(new MenuItem("Use", () -> {
                                inventoryItem.useBy(squad.unit);
                            }));
                        }
                        menuItems.add(new MenuItem("Drop", () -> {
                            squad.getInventoryController().moveItemFromMainToActiveInventory(inventoryItem);
                            inventoriesPanel.inventoryWindow.refresh(squad);
                        }));
                        ListMenu.show(menuItems);

                    }
                });
                table.row();
            }
        }
    }
}
