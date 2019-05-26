package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;

public abstract class RangeWeaponItem extends AbstractInventoryItem {

    public RangeWeaponItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    @Override
    public String getDescription() {
        return super.getDescription() +
                "Range Damage: " + params.getRangedDamage() + "\n" +
                "Hit Chance: " + params.getHitChancePerc() +
                "\n" + getRequiredSkillDescriptionLine() +
                "\n" + getBulletCostHint();
    }

    public String getBulletCostHint() {
        return "Shot cost: " + getBulletCost().toString();
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    public abstract BulletCost getBulletCost();
}
