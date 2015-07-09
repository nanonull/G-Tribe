package conversion7.game.stages.world.objects.controllers;

import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.AreaObject;

public class InventoryController extends AbstractObjectController {

    private BasicInventory activeInventory;

    public InventoryController(AreaObject areaObject) {
        super(areaObject);
        setLeftInventoryActive();
    }

    public void setLeftInventoryActive() {
        activeInventory = areaObject.getMilitaryInventory();
    }

    public void setRightInventoryActive() {
        activeInventory = areaObject.getCell().getInventory();
    }

    public boolean isLeftInventoryActive() {
        return activeInventory == areaObject.getMilitaryInventory();
    }

    public void moveItemFromCellToMainInventory(AbstractInventoryItem inventoryItem) {
        areaObject.getCell().getInventory().remove(inventoryItem);
        areaObject.getMainInventory().addItem(inventoryItem);
    }

    public void moveItemFromMainToActiveInventory(AbstractInventoryItem inventoryItem) {
        areaObject.getMainInventory().remove(inventoryItem);
        activeInventory.addItem(inventoryItem);
    }

    public void moveItemFromMilitaryToMainInventory(AbstractInventoryItem inventoryItem) {
        areaObject.getMilitaryInventory().remove(inventoryItem);
        areaObject.getMainInventory().addItem(inventoryItem);
    }

    public void moveAllItemsToCellInventory() {
        BasicInventory cellInventory = areaObject.getCell().getInventory();
        cellInventory.moveItems(areaObject.getMainInventory());
        cellInventory.moveItems(areaObject.getMilitaryInventory());
    }

    public void mergeInventoriesFrom(AreaObject mergeFromAreaObject) {
        areaObject.getMainInventory().moveItems(mergeFromAreaObject.getMainInventory());
        areaObject.getMilitaryInventory().moveItems(mergeFromAreaObject.getMilitaryInventory());
    }

    public void executeCraft(AbstractInventoryItem inventoryItem) {
        areaObject.getCraftInventory().craft(inventoryItem.getClass());
    }

    public void validate() {
        areaObject.getMainInventory().validate();
        areaObject.getMilitaryInventory().validate();
    }
}
