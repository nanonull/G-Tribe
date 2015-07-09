package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;

public abstract class RangeBulletItem extends AbstractWeaponItem {

    public RangeBulletItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    public boolean isFull() {
        return quantity >= params.getEquipQuantityLimit();
    }
}

