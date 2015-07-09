package conversion7.game.stages.world.inventory.items.types;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;

public abstract class AbstractInventoryItem {

    protected int quantity;
    protected InventoryItemStaticParams params;

    public AbstractInventoryItem(InventoryItemStaticParams params) {
        this.params = params;
    }

    public InventoryItemStaticParams getParams() {
        return params;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateQuantity(int onValue) {
        quantity += onValue;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append(" qty=")
                .append(quantity).toString();
    }

    public <T extends AbstractInventoryItem> T split(int qtyForNewItem) {
        // TODO clear on iteration end
        if (qtyForNewItem > quantity) {
            Utils.error(String.format("qtyForNewItem > quantity: %d,%d!", qtyForNewItem, quantity));
        }
        if (qtyForNewItem < 1) {
            Utils.error(String.format("qtyForNewItem < 1: %d!", qtyForNewItem));
        }


        T newItem = null;
        try {
            newItem = (T) getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Utils.error(e);
        }

        moveQuantity(qtyForNewItem, newItem);
        return newItem;
    }

    /** Returns true if item is empty */
    public boolean moveQuantity(int qtyForNewItem, AbstractInventoryItem target) {
        this.updateQuantity(-qtyForNewItem);
        target.updateQuantity(+qtyForNewItem);
        return isEmpty();
    }

    public String getName() {
        return params.getName();
    }

    public boolean isEmpty() {
        return quantity == 0;
    }
}
