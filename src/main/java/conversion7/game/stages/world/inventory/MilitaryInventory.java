package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.utils.collections.Comparators;
import conversion7.game.utils.collections.IterationRegistrators;
import org.slf4j.Logger;

import java.util.Iterator;

public class MilitaryInventory extends BasicInventory {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AreaObject areaObject;
    private boolean equippingInProgress;
    private boolean bulletsSpreadingInProgress;
    private Array<AbstractInventoryItem> inventoryItemsSortedByValue = PoolManager.ARRAYS_POOL.obtain();
    private Array<RangeBulletItem> bulletItemsWip = PoolManager.ARRAYS_POOL.obtain();
    private Array<Unit> rangeUnitsQueueWip = PoolManager.ARRAYS_POOL.obtain();

    public MilitaryInventory(AreaObject areaObject) {
        super();
        this.areaObject = areaObject;
    }

    public Array<AbstractInventoryItem> getInventoryItemsSortedByValue() {
        return inventoryItemsSortedByValue;
    }

    @Override
    public void remove(AbstractInventoryItem inventoryItem) {
        super.remove(inventoryItem);
        inventoryItemsSortedByValue.removeValue(inventoryItem, true);
    }

    @Override
    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int qty) {
        AbstractInventoryItem addedItem = super.addItem(itemClass, qty);
        reequip(addedItem);
        return addedItem;
    }

    @Override
    public void validate() {
        if (!valid) {
            reequip();
        }
    }

    public void reequip() {
        reequip(null);
    }

    private void reequip(AbstractInventoryItem itemAddedDuringEquipping) {
        validateItemsSortedByValue();

        if (!equippingInProgress) {
            LOG.info("reequip due to new item: " + itemAddedDuringEquipping);
            equippingInProgress = true;
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.assertNotStarted();
            WorldThreadLocalSort.instance().sort(areaObject.getUnits(), Comparators.UNIT_EQUIPPING_PRIORITY_COMPARATOR);

            spreadItems();
            spreadBullets();

            equippingInProgress = false;
        } else if (itemAddedDuringEquipping != null && bulletsSpreadingInProgress && itemAddedDuringEquipping instanceof RangeBulletItem) {
            bulletItemsWip.add((RangeBulletItem) itemAddedDuringEquipping);
            validateBullets();
        }
        areaObject.getReadyRangeUnitsController().validate();
    }

    private void spreadItems() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.safeIteration();
        for (int i = 0; i < areaObject.getUnits().size; i++) {
            areaObject.getUnits().get(i).getEquipment().chooseBetterEquipment(this);
        }
    }

    private void spreadBullets() {
        bulletsSpreadingInProgress = true;
        loadBullets();

        rangeUnitsQueueWip.clear();
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < areaObject.getUnits().size; i++) {
            Unit unit = areaObject.getUnits().get(i);
            if (unit.getSpecialization().equals(Unit.UnitSpecialization.RANGE)) {
                rangeUnitsQueueWip.add(unit);
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

        /**If anyone doesn't took new bullet - stop spreading */
        boolean dummySpreadingIteration = false;
        while (!dummySpreadingIteration && bulletItemsWip.size > 0) {
            dummySpreadingIteration = true;
            Iterator<Unit> unitsIterator = rangeUnitsQueueWip.iterator();
            while (unitsIterator.hasNext()) {
                Unit rangeUnit = unitsIterator.next();

                RangeBulletItem foundBulletItemInInventoryForUnit = searchBulletFor(rangeUnit);
                if (foundBulletItemInInventoryForUnit == null) {
                    // unit will wait for bullets drop
                } else {
                    //  bullet was found
                    dummySpreadingIteration = false;
                    RangeBulletItem unitBulletsItem = rangeUnit.getEquipment().getRangeBulletsItem();
                    if (unitBulletsItem == null ||
                            !unitBulletsItem.getClass().equals(foundBulletItemInInventoryForUnit.getClass())) {
                        unitBulletsItem = foundBulletItemInInventoryForUnit.split(1);
                        rangeUnit.getEquipment().equipRangeBulletsItem(unitBulletsItem);
                    } else if (!unitBulletsItem.isFull()) {
                        if (LOG.isDebugEnabled())
                            Utils.debug(LOG, "+1 bullet %s for %s", unitBulletsItem.getClass().getSimpleName(), rangeUnit);
                        foundBulletItemInInventoryForUnit.moveQuantity(1, unitBulletsItem);
                    }

                    if (foundBulletItemInInventoryForUnit.isEmpty()) {
                        // remove item from inventory
                        bulletItemsWip.removeValue(foundBulletItemInInventoryForUnit, true);
                        remove(foundBulletItemInInventoryForUnit);
                    }

                    if (unitBulletsItem.isFull()) {
                        // remove unit from bullets queue
                        unitsIterator.remove();
                    }

                    if (bulletItemsWip.size == 0) {
                        // stop spreading
                        break;
                    }
                }
            }
        }

        // sync quantities to inventory
        for (AbstractInventoryItem item : bulletItemsWip) {
            getItem(item.getClass()).setQuantity(item.getQuantity());
        }

        bulletsSpreadingInProgress = false;
    }

    private RangeBulletItem searchBulletFor(Unit rangeUnit) {
        Class<? extends RangeBulletItem> needBulletClass = null;
        RangeWeaponItem unitRangeWeapon = rangeUnit.getEquipment().getRangeWeaponItem();
        if (unitRangeWeapon == null) {
            RangeBulletItem bestBulletInInventory = bulletItemsWip.get(0);
            RangeBulletItem unitBulletsItem = rangeUnit.getEquipment().getRangeBulletsItem();
            if (unitBulletsItem != null && unitBulletsItem.getParams().getValue() >= bestBulletInInventory.getParams().getValue()) {
                // there are no better items
                needBulletClass = unitBulletsItem.getClass();
            } else {
                // take the topmost available bullet
                for (RangeBulletItem rangeBulletItem : bulletItemsWip) {
                    if (rangeUnit.getEquipment().readyToEquipItem(rangeBulletItem)) {
                        return rangeBulletItem;
                    }
                }
                // no skills to wear
                return null;
            }
        } else {
            needBulletClass = unitRangeWeapon.getParams().getBulletClass();
        }

        for (RangeBulletItem rangeBulletItem : bulletItemsWip) {
            if (needBulletClass.equals(rangeBulletItem.getClass())) {
                return rangeBulletItem;
            }
        }
        return null;
    }

    private void validateItemsSortedByValue() {
        inventoryItemsSortedByValue.clear();
        items.values().toArray(inventoryItemsSortedByValue);
        WorldThreadLocalSort.instance().sort(inventoryItemsSortedByValue, Comparators.INVENTORY_ITEM_VALUE_COMPARATOR);
    }

    private void loadBullets() {
        bulletItemsWip.clear();
        for (AbstractInventoryItem item : inventoryItemsSortedByValue) {
            if (item instanceof RangeBulletItem) {
                bulletItemsWip.add((RangeBulletItem) item);
            }
        }
    }

    private void validateBullets() {
        WorldThreadLocalSort.instance().sort(bulletItemsWip, Comparators.INVENTORY_ITEM_VALUE_COMPARATOR);

    }

}
