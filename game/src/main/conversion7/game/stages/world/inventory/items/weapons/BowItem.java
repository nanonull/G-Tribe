package conversion7.game.stages.world.inventory.items.weapons;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.types.BulletCost;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;

public class BowItem extends RangeWeaponItem {
    private static final BulletCost COST = new BulletCost(ArrowItem.class, 1);

    public BowItem() {
        super(InventoryItemStaticParams.BOW);
    }

    @Override
    public BulletCost getBulletCost() {
        return COST;
    }
}
