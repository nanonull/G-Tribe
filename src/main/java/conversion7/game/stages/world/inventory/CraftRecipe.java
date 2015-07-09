package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import org.slf4j.Logger;

public class CraftRecipe {

    private static final Logger LOG = Utils.getLoggerForClass();

    private Class<? extends AbstractInventoryItem> finalItemClass;
    private int finalItemQuantityPerCraft;
    private Array<Consumable> consumables = new Array<>();

    public CraftRecipe(Class<? extends AbstractInventoryItem> finalItemClass, Consumable... consumables) {
        this(1, finalItemClass, consumables);
    }

    public CraftRecipe(int finalItemQuantityPerCraft, Class<? extends AbstractInventoryItem> finalItemClass, Consumable... consumables) {
        this.finalItemQuantityPerCraft = finalItemQuantityPerCraft;
        this.finalItemClass = finalItemClass;
        for (Consumable consumable : consumables) {
            this.consumables.add(consumable);
        }
        CraftInventory.addRecipe(this);
    }

    public Class<? extends AbstractInventoryItem> getFinalItemClass() {
        return finalItemClass;
    }

    public int getFinalItemQuantityPerCraft() {
        return finalItemQuantityPerCraft;
    }

    public Array<Consumable> getConsumables() {
        if (LOG.isDebugEnabled()) Utils.debug(LOG, "getConsumables %s", this);
        return consumables;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append(": ")
                .append(finalItemClass.getSimpleName()).toString();
    }

    public AbstractInventoryItem getPossibleCraftItemWithin(BasicInventory inventory) {
        if (LOG.isDebugEnabled()) Utils.debug(LOG, "getPossibleCraftItemWithin %s", this);
        int successfullCraftsQuantity = -1;
        int maxQtyFromThisConsumable;
        for (Consumable consumable : consumables) {
            AbstractInventoryItem item = inventory.getItem(consumable.getItemClass());
            if (item == null) {
                // recipe impossible
                return null;
            }

            maxQtyFromThisConsumable = item.getQuantity() / consumable.getQuantity();
            if (successfullCraftsQuantity == -1 || maxQtyFromThisConsumable < successfullCraftsQuantity) {
                successfullCraftsQuantity = maxQtyFromThisConsumable;
            }
        }

        if (successfullCraftsQuantity == 0) {
            // recipe impossible
            return null;
        }

        AbstractInventoryItem inventoryItemCrafted = null;
        try {
            inventoryItemCrafted = finalItemClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Utils.error(e);
        }
        assert inventoryItemCrafted != null;
        inventoryItemCrafted.setQuantity(successfullCraftsQuantity * finalItemQuantityPerCraft);
        return inventoryItemCrafted;
    }

    public void useConsumables(BasicInventory inventory, int craftedQuantity) {
        if (LOG.isDebugEnabled()) Utils.debug(LOG, "useConsumables %s", this);
        for (Consumable consumable : consumables) {
            AbstractInventoryItem itemToConsume = inventory.getItem(consumable.getItemClass());
            itemToConsume.updateQuantity(-consumable.getQuantity() * craftedQuantity / finalItemQuantityPerCraft);
            if (itemToConsume.isEmpty()) {
                inventory.remove(itemToConsume);
            }
        }

    }

    public static class Consumable {
        private Class<? extends AbstractInventoryItem> itemClass;
        private int quantity;

        public Consumable(Class<? extends AbstractInventoryItem> itemClass, int qty) {

            this.itemClass = itemClass;
            this.quantity = qty;
        }

        public Class<? extends AbstractInventoryItem> getItemClass() {
            return itemClass;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return new StringBuilder(getClass().getSimpleName()).append(": ")
                    .append(itemClass.getSimpleName()).toString();
        }
    }
}
