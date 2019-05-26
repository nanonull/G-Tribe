package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;

public abstract class ResourceItem extends AbstractInventoryItem {
    public ResourceItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    @Override
    public int getOrder() {
        return isUsable() ? FoodItem.FOOD_ORDER - 1 : 1000;
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
