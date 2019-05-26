package conversion7.game.stages.world.inventory.items.weapons;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.FusionCellItem;
import conversion7.game.stages.world.inventory.items.types.BulletCost;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;

public class FusionBlasterItem extends RangeWeaponItem {

    private static final BulletCost COST = new BulletCost(FusionCellItem.class, 1);

    public FusionBlasterItem() {
        super(InventoryItemStaticParams.FUSION_BLASTER);
    }

    @Override
    public BulletCost getBulletCost() {
        return COST;
    }
}
