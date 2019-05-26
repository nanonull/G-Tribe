package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.CellInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class InventoryValidator extends AbstractSquadValidator {

    BasicInventory tempInventory = new BasicInventory();

    public InventoryValidator(AbstractSquad obj) {
        super(obj);
        invalidate();
    }

    public void moveItemFromCellToMainInventory(AbstractInventoryItem inventoryItem) {
        squad.getLastCell().getInventory().remove(inventoryItem);
        squad.team.getInventory().addItem(inventoryItem.getClass(), inventoryItem.quantity, squad.cell);
    }

    public void moveItemFromMainToActiveInventory(AbstractInventoryItem inventoryItem) {
        squad.getInventory().remove(inventoryItem);
        squad.getLastCell().getInventory().addItem(inventoryItem);
    }

    public void moveAllItemsToCellInventory() {
        BasicInventory cellInventory = squad.getLastCell().getInventory();
        cellInventory.moveItems(squad.getInventory());
    }

    @Deprecated
    public void mergeInventoriesFrom(AbstractSquad mergeFrom) {
        squad.getInventory().moveItems(mergeFrom.getInventory());
    }

    public void executeCraft(Class<? extends AbstractInventoryItem> inventoryItemClass) {
        squad.getCraftInventory().craft(inventoryItemClass, 1);
    }

    @Override
    public void validate() {
        CellInventory cellInventory = squad.getLastCell().getInventory();
        tempInventory.clearItems();
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry :
                cellInventory.getItemsIterator()) {
            if (squad.canPickUpItem(itemEntry.value)) {
                Integer newItemQty = itemEntry.value.quantity;
                Integer currentStatQty = squad.team.getGatheringStatistic().items.get(itemEntry.key);
                squad.team.getGatheringStatistic().items.put(itemEntry.key,
                        currentStatQty + newItemQty);
                tempInventory.addItem(itemEntry.value);
            }
        }

        // tmp remove from cell
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry
                : tempInventory.getItemsIterator()) {
            AbstractInventoryItem inventoryItem = itemEntry.value;
            cellInventory.remove(inventoryItem);
            squad.team.getInventory().addItem(inventoryItem.getClass(), inventoryItem.quantity, squad.cell);
        }
    }
}
