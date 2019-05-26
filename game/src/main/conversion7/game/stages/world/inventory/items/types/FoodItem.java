package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.UnitClassConstants;

public abstract class FoodItem extends AbstractInventoryItem {
    public static final int FOOD_ORDER = 50;
    private static final int BASE_HEAL = (int) (UnitClassConstants.BASE_POWER / 4.5f);

    public FoodItem(InventoryItemStaticParams params) {
        super(params);
    }

    @Override
    public String getDescription() {
        return "Consume to restore health";
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public int getOrder() {
        return FOOD_ORDER;
    }

    @Override
    public void useBy(Unit unit) {
        unit.squad.heal(BASE_HEAL);
        super.useBy(unit);
    }
}
