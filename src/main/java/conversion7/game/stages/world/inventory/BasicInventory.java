package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.Batched;
import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;

public class BasicInventory implements Validatable, Batched {

    protected ObjectMap<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> items = PoolManager.OBJECT_MAP_POOL.obtain();
    protected boolean batch;
    protected boolean valid;

    public boolean isEmpty() {
        return items.size == 0;
    }

    public Iterable<? extends ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem>> getItemsIterator() {
        return items.entries();
    }

    public void getItems(ObjectMap<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> intoCollection) {
        intoCollection.putAll(items);
    }

    public void getItems(Array<AbstractInventoryItem> intoCollection) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> item : items) {
            intoCollection.add(item.value);
        }
    }

    public void moveItems(BasicInventory inventory) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry : inventory.items) {
            addItem(itemEntry.value);
        }
        inventory.clearItems();
    }

    public void addItem(AbstractInventoryItem inventoryItem) {
        addItem(inventoryItem.getClass(), inventoryItem.getQuantity());
    }

    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int qty) {
        AbstractInventoryItem inventoryItem = getItem(itemClass);
        if (inventoryItem == null) {
            try {
                inventoryItem = itemClass.newInstance();
                items.put(itemClass, inventoryItem);
            } catch (InstantiationException | IllegalAccessException e) {
                Utils.error(e);
            }
        }
        assert inventoryItem != null;
        inventoryItem.updateQuantity(qty);
        invalidate();
        validate();
        return inventoryItem;
    }

    public AbstractInventoryItem getItem(Class<? extends AbstractInventoryItem> itemClass) {
        return items.get(itemClass);
    }

    public void remove(AbstractInventoryItem inventoryItem) {
        items.remove(inventoryItem.getClass());
        invalidate();
        validate();
    }

    public void clearItems() {
        items.clear();
    }

    @Override
    public void startBatch() {
        this.batch = true;
    }

    @Override
    public void endBatch() {
        this.batch = false;
        validate();
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public void validate() {

    }
}
