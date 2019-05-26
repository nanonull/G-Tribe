package conversion7.game.stages.world.inventory.items.weapons;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem;

public class SpearItem extends MeleeWeaponItem {
    public SpearItem() {
        super(InventoryItemStaticParams.SPEAR);
    }

    @Override
    public boolean canPierce() {
        return true;
    }
}
