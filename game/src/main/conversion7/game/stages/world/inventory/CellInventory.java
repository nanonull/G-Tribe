package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.ResourceItem;
import conversion7.game.stages.world.landscape.Cell;

public class CellInventory extends BasicInventory {
    public Cell cell;
    private boolean containsOnlyResources;

    public CellInventory(Cell cell) {
        this.cell = cell;
        validate();
    }

    public boolean isContainsOnlyResources() {
        return containsOnlyResources;
    }

    @Override
    public void validate() {
        super.validate();
        containsOnlyResources = true;
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, AbstractInventoryItem> entry : items) {
            if (!(entry.value instanceof ResourceItem)) {
                containsOnlyResources = false;
                break;
            }
        }
        cell.validateInventoryItemsIndicator();
    }
}
