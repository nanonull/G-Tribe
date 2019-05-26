package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import conversion7.engine.validators.NodeValidator;
import conversion7.game.interfaces.Batched;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;

import java.util.Comparator;

public class BasicInventory extends NodeValidator implements Batched {

    private static final Comparator<AbstractInventoryItem>
            SORT_BY_ITEM_TYPE_AND_NAME = (o1, o2) -> {
        int compareI = Integer.compare(o1.getOrder(), o2.getOrder());
        if (compareI == 0) {
            return o1.getName().compareTo(o2.getName());
        } else return compareI;
    };
    protected OrderedMap<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> items = new OrderedMap<>();
    protected boolean batch;
    protected boolean valid;

    public boolean isEmpty() {
        return items.size == 0;
    }

    public ObjectMap.Entries<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> getItemsIteratorNew() {
        return new ObjectMap.Entries<>(items);
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

    public void moveItems(BasicInventory fromInventory) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> itemEntry :
                new ObjectMap.Entries<>(fromInventory.items)) {
            addItem(itemEntry.value);
        }
        fromInventory.clearItems();
        refresh();
    }

    public void addItem(AbstractInventoryItem inventoryItem) {
        addItem(inventoryItem.getClass(), inventoryItem.getQuantity());
    }

    public void clearItems() {
        items.clear();
        refresh();
    }

    public AbstractInventoryItem remove(Class<? extends AbstractInventoryItem> itemClass, int addQty) {
        return addItem(itemClass, -addQty);
    }

    /** @param addQty can be negative */
    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int addQty) {
        AbstractInventoryItem inventoryItem = getItem(itemClass);
        if (inventoryItem == null) {
            try {
                inventoryItem = itemClass.newInstance();
                items.put(itemClass, inventoryItem);
                inventoryItem.setQuantity(addQty);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GdxRuntimeException(e);
            }
        } else {
            inventoryItem.updateQuantity(addQty);
        }
        if (inventoryItem.quantity < 0) {
            throw new GdxRuntimeException("Item addQty can't be less than 0 (actual: " + inventoryItem.quantity + ")");
        }
        if (inventoryItem.quantity == 0) {
            remove(inventoryItem);
        } else {
            refresh();
        }
        return inventoryItem;
    }

    public int getItemQty(Class<? extends AbstractInventoryItem> itemClass) {
        AbstractInventoryItem item = getItem(itemClass);
        return item == null ? 0 : item.quantity;
    }

    public AbstractInventoryItem getItem(Class<? extends AbstractInventoryItem> itemClass) {
        return items.get(itemClass);
    }

    @Override
    public void validate() {
        Array<AbstractInventoryItem> values = this.items.values().toArray();
        values.sort(SORT_BY_ITEM_TYPE_AND_NAME);
        for (int i = 0; i < items.orderedKeys().size; i++) {
            AbstractInventoryItem inventoryItem = values.get(i);
            this.items.orderedKeys().set(i, inventoryItem.getClass());
        }
    }

    public void remove(AbstractInventoryItem inventoryItem) {
        items.remove(inventoryItem.getClass());
        refresh();
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

    public void remove(ObjectMap.Entries<Class<? extends AbstractInventoryItem>, Integer> entries) {
        startBatch();
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, Integer> entry : entries) {
            addItem(entry.key, -entry.value);
        }
        endBatch();
    }
}
