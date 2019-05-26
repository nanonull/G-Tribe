package conversion7.game.stages.world.team;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;

public class GatheringStatistic {
    public Integer food;
    public Integer water;
    public ObjectMap<Class<? extends AbstractInventoryItem>, Integer> items = new ObjectMap<>();
    public int evolutionExp;
    public int evolutionPoints;

    public GatheringStatistic() {
        reset();
    }

    public void reset() {
        food = 0;
        water = 0;
        evolutionExp = 0;
        evolutionPoints = 0;
        for (Class<? extends AbstractInventoryItem> itemClass : PackageReflectedConstants.INVENTORY_ITEM_CLASSES) {
            items.put(itemClass, 0);
        }
    }
}
