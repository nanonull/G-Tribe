package conversion7.game.stages.world.inventory.items.types;

import conversion7.game.stages.world.inventory.InventoryItemStaticParams;

public abstract class RangeBulletItem extends AbstractInventoryItem {

    public RangeBulletItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    @Override
    public String getDescription() {
        return super.getDescription() +
//                "Ranged Damage: " + params.getRangedDamage() + "\n" +
//                "Attack Chance: " + params.getHitChance() + "\n" +
                getRequiredSkillDescriptionLine();
    }

    @Override
    public int getOrder() {
        return 800;
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}

