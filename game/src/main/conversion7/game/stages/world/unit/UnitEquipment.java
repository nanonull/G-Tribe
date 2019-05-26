package conversion7.game.stages.world.unit;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.types.*;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.skills.SkillType;
import org.slf4j.Logger;

public class UnitEquipment implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AbstractSquad squad;

    private MeleeWeaponItem meleeWeaponItem;
    private RangeWeaponItem rangeWeaponItem;
    private ClothesItem clothesItem;

    public UnitEquipment(AbstractSquad squad) {
        this.squad = squad;
    }

    public MeleeWeaponItem getMeleeWeaponItem() {
        return meleeWeaponItem;
    }

    public void setMeleeWeaponItem(MeleeWeaponItem meleeWeaponItem) {
        this.meleeWeaponItem = meleeWeaponItem;
        equipChangedEvent();
    }

    public String getBulletCostAndActualAmount() {
        return rangeWeaponItem.getBulletCost().toString() + " (has: " +
                squad.team.getInventory().getItemQty(rangeWeaponItem.getBulletCost().getBulletClass()) + ")";
    }

    public RangeWeaponItem getRangeWeaponItem() {
        return rangeWeaponItem;
    }

    public void setRangeWeaponItem(RangeWeaponItem rangeWeaponItem) {
        this.rangeWeaponItem = rangeWeaponItem;
        equipChangedEvent();
    }

    public ClothesItem getClothesItem() {
        return clothesItem;
    }

    public void setClothesItem(ClothesItem clothesItem) {
        this.clothesItem = clothesItem;
        equipChangedEvent();
    }

    public int getMeleeDamage() {
        if (hasMeleeWeap()) {
            return meleeWeaponItem.getParams().getMeleeDamage();
        } else {
            return 0;
        }
    }

    public int getRangeDamage() {
        if (rangeWeaponItem != null) {
            return rangeWeaponItem.getParams().getRangedDamage();
        }
        return 0;
    }

    public int getMeleeHitChance() {
        if (meleeWeaponItem == null) {
            return InventoryItemStaticParams.BASE_HIT_CHANCE_PERC;
        } else {
            return meleeWeaponItem.getParams().getHitChancePerc();
        }
    }

    public int getRangeHitChance() {
        if (rangeWeaponItem != null) {
            return rangeWeaponItem.getParams().getHitChancePerc();
        }
        return 0;
    }

    public int getArmor() {
        if (clothesItem == null) {
            return 0;
        } else {
            return clothesItem.getParams().getArmor();
        }
    }

    public int getHeat() {
        if (clothesItem == null) {
            return 0;
        } else {
            return clothesItem.getParams().getHeat();
        }
    }

    @Override
    public String getHint() {
        StringBuilder builder = new StringBuilder("Equipment:\n");
        boolean noEquip = true;

        if (meleeWeaponItem != null) {
            noEquip = false;
            builder.append(" - melee weapon: ").append(meleeWeaponItem.getName()).append("\n");
        }

        if (rangeWeaponItem != null) {
            noEquip = false;
            builder.append(" - range weapon: ").append(rangeWeaponItem.getName()).append("\n");
        }

        if (clothesItem != null) {
            noEquip = false;
            builder.append(" - clothes: ").append(clothesItem.getName()).append("\n");
        }

        if (noEquip) {
            builder.append("-nothing equipped-");
        }
        return builder.toString();
    }

    public boolean hasMeleeWeap() {
        return meleeWeaponItem != null;
    }

    public boolean hasRangeWeap() {
        return rangeWeaponItem != null;
    }

    public boolean couldEquip(AbstractInventoryItem inventoryItem) {
        if (!inventoryItem.isEquipment() || !squad.canEquipItems()) {
            return false;
        }
        SkillType requiredSkill = inventoryItem.getParams().requiredSkill;
        return squad.canUseItem(inventoryItem, requiredSkill);
    }

    public boolean couldConsume(AbstractInventoryItem item) {
        return item.isUsable() && squad.getMoveAp() >= AbstractSquad.USE_ITEM_AP;
    }

    public void equipMeleeWeaponItem(MeleeWeaponItem meleeWeaponItem) {
        Utils.debug(LOG, "%s equips melee %s", squad, meleeWeaponItem);
        dropMeleeWeapon();
        setMeleeWeaponItem(meleeWeaponItem);
    }

    public void equipRangeWeaponItem(Class<? extends RangeWeaponItem> cls) {
        try {
            RangeWeaponItem rangeWeaponItem = cls.newInstance();
            rangeWeaponItem.setQuantity(1);
            equipRangeWeaponItem(rangeWeaponItem);
        } catch (InstantiationException | IllegalAccessException e) {
            Gdxg.core.addError(e);
        }
    }

    public void equipRangeWeaponItem(RangeWeaponItem rangeWeaponItem) {
        Utils.debug(LOG, "%s equips range %s", squad, rangeWeaponItem);
        dropRangeWeapon();
        setRangeWeaponItem(rangeWeaponItem);
        squad.getActionsController().invalidate();
    }

    public void equipClothesItem(ClothesItem clothesItem) {
        Utils.debug(LOG, "%s equips clothes %s", squad, clothesItem);
        dropClothes();
        setClothesItem(clothesItem);
    }

    public void dropMeleeWeapon() {
        if (meleeWeaponItem != null) {
            squad.getInventory().addItem(meleeWeaponItem);
            setMeleeWeaponItem(null);
        }
    }

    public void dropRangeWeapon() {
        if (rangeWeaponItem != null) {
            squad.getInventory().addItem(rangeWeaponItem);
            setRangeWeaponItem(null);
        }
    }

    public void destroyClothes() {
        if (clothesItem != null) {
            setClothesItem(null);
        }
    }

    public void dropClothes() {
        if (clothesItem != null) {
            squad.getInventory().addItem(clothesItem);
            setClothesItem(null);
        }
    }

    public void drop() {
        dropMeleeWeapon();
        dropRangeWeapon();
        dropClothes();
    }

    public void spendRangeBullet(BasicInventory inventoryWhereBulletWillStay) {
        BulletCost bulletCost = rangeWeaponItem.getBulletCost();
        squad.getInventory().addItem(bulletCost.getBulletClass(), -bulletCost.getQty());
    }

    public void equipByType(AbstractInventoryItem item) {
        if (item instanceof ClothesItem) {
            equipClothesItem((ClothesItem) item);
        } else if (item instanceof MeleeWeaponItem) {
            equipMeleeWeaponItem((MeleeWeaponItem) item);
        } else if (item instanceof RangeWeaponItem) {
            equipRangeWeaponItem((RangeWeaponItem) item);
        } else {
            throw new RuntimeException("");
        }
    }

    public void equip(BasicInventory fromInventory, AbstractInventoryItem item) {
        AbstractInventoryItem newItem = item.splitForEquip();
        equipByType(newItem);
        if (item.quantity <= 0) {
            fromInventory.remove(item);
        }
    }

    private void equipChangedEvent() {
        squad.getUnitParametersValidator().invalidate();
        squad.validate();
        squad.refreshUiPanelInWorld();
    }


    public boolean hasClothes() {
        return clothesItem != null;
    }
}
