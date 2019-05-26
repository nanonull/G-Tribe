package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.unit_classes.UnitClassConstants;

public abstract class ClothesItem extends AbstractInventoryItem {

    public int hp = UnitClassConstants.BASE_POWER;

    public ClothesItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    @Override
    public String getDescription() {
        return super.getDescription() +
                "Heat: " + params.getHeat() + "\n" +
                "Durab.: " + hp + "\n" +
                getRequiredSkillDescriptionLine();
    }


    @Override
    public int getOrder() {
        return 300;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    private boolean isDestroyed() {
        return hp <= 0;
    }

    public boolean updateHp(int on) {
        hp += on;
        return isDestroyed();
    }
}
