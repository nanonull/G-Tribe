package conversion7.game.stages.world.inventory;

import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.AreaObject;

public class MainInventory extends BasicInventory {

    private AreaObject areaObject;

    public MainInventory(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

    @Override
    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int qty) {
        return super.addItem(itemClass, qty);
    }

    @Override
    public void validate() {
        if (!valid && !batch) {
            areaObject.getCraftInventory().update();
        }
    }
}
