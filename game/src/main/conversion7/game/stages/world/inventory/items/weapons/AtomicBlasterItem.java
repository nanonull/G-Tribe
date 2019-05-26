package conversion7.game.stages.world.inventory.items.weapons;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.types.BulletCost;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;

public class AtomicBlasterItem extends RangeWeaponItem {

    private static final BulletCost COST = new BulletCost(RadioactiveIsotopeItem.class, 1);

    public AtomicBlasterItem() {
        super(InventoryItemStaticParams.ATOMIC_BLASTER);
    }

    @Override
    public BulletCost getBulletCost() {
        return COST;
    }
}
