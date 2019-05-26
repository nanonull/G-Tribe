package conversion7.game.stages.world.inventory.items.weapons;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.types.BulletCost;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;

public class AtlatlItem extends RangeWeaponItem {
    private static final BulletCost COST = new BulletCost(JavelinItem.class, 1);

    public AtlatlItem() {
        super(InventoryItemStaticParams.ATLATL);
    }

    @Override
    public BulletCost getBulletCost() {
        return COST;
    }
}
