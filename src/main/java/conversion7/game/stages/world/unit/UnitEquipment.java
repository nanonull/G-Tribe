package conversion7.game.stages.world.unit;

import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.stages.world.inventory.BasicInventory;
import conversion7.game.stages.world.inventory.MilitaryInventory;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.ClothesItem;
import conversion7.game.stages.world.inventory.items.types.MeleeWeaponItem;
import conversion7.game.stages.world.inventory.items.types.RangeBulletItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import org.slf4j.Logger;

public class UnitEquipment implements HintProvider {

    private static final Logger LOG = Utils.getLoggerForClass();

    private Unit unit;

    private MeleeWeaponItem meleeWeaponItem;
    private RangeWeaponItem rangeWeaponItem;
    private RangeBulletItem rangeBulletsItem;
    private ClothesItem clothesItem;

    public UnitEquipment(Unit unit) {
        this.unit = unit;
    }

    public void setMeleeWeaponItem(MeleeWeaponItem meleeWeaponItem) {
        this.meleeWeaponItem = meleeWeaponItem;
    }

    public void setRangeWeaponItem(RangeWeaponItem rangeWeaponItem) {
        this.rangeWeaponItem = rangeWeaponItem;
    }

    public void setRangeBulletsItem(RangeBulletItem rangeBulletsItem) {
        this.rangeBulletsItem = rangeBulletsItem;
        unit.getAreaObject().getReadyRangeUnitsController().invalidate();
    }

    public void setClothesItem(ClothesItem clothesItem) {
        this.clothesItem = clothesItem;
    }

    public void equipMeleeWeaponItem(MeleeWeaponItem meleeWeaponItem) {
        Utils.debug(LOG, "%s equips melee %s", unit, meleeWeaponItem);
        dropMeleeWeapon();
        setMeleeWeaponItem(meleeWeaponItem);
    }

    public void equipRangeWeaponItem(RangeWeaponItem rangeWeaponItem) {
        Utils.debug(LOG, "%s equips range %s", unit, rangeWeaponItem);
        dropRangeWeapon();
        if (rangeBulletsItem != null) {
            dropRangeBulletsIfNotSupportedByWeapon(rangeWeaponItem.getParams().getBulletClass());
        }
        setRangeWeaponItem(rangeWeaponItem);
    }

    public void equipRangeBulletsItem(RangeBulletItem rangeBulletsItem) {
        Utils.debug(LOG, "%s equips bullet %s", unit, rangeBulletsItem);
        dropRangeBullets();
        setRangeBulletsItem(rangeBulletsItem);
    }

    public void equipClothesItem(ClothesItem clothesItem) {
        Utils.debug(LOG, "%s equips clothes %s", unit, clothesItem);
        dropClothes();
        setClothesItem(clothesItem);
    }

    public MeleeWeaponItem getMeleeWeaponItem() {
        return meleeWeaponItem;
    }

    public RangeWeaponItem getRangeWeaponItem() {
        return rangeWeaponItem;
    }

    public RangeBulletItem getRangeBulletsItem() {
        return rangeBulletsItem;
    }

    public ClothesItem getClothesItem() {
        return clothesItem;
    }

    public int getMeleeDamage() {
        if (meleeWeaponItem == null) {
            return 1;
        } else {
            return meleeWeaponItem.getParams().getMeleeDamage();
        }
    }

    public int getRangeDamage() {
        int bulletDamage = rangeBulletsItem.getParams().getRangedDamage();
        if (rangeWeaponItem != null) {
            bulletDamage += rangeWeaponItem.getParams().getRangedDamageModifier();
        }
        return bulletDamage;
    }

    public int getRangeAttackChance() {
        int attackChance = rangeBulletsItem.getParams().getAttackChance();
        if (rangeWeaponItem != null) {
            attackChance += rangeWeaponItem.getParams().getAttackChanceModifier();
        }
        return attackChance;
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

        if (rangeBulletsItem != null) {
            noEquip = false;
            builder.append(" - range bullets: ").append(rangeBulletsItem.getName())
                    .append(" x").append(rangeBulletsItem.getQuantity()).append("\n");
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

    public void chooseBetterEquipment(MilitaryInventory militaryInventory) {
        equipMelee(militaryInventory);
        if (unit.getSpecialization().equals(Unit.UnitSpecialization.RANGE)) {
            equipRange(militaryInventory);
        }
        equipClothes(militaryInventory);
    }

    private void equipMelee(MilitaryInventory militaryInventory) {
        for (AbstractInventoryItem inventoryItem : militaryInventory.getInventoryItemsSortedByValue()) {
            if (meleeWeaponItem != null && meleeWeaponItem.getParams().getValue() >= inventoryItem.getParams().getValue()) {
                // there are no better items
                break;
            }
            if (inventoryItem instanceof MeleeWeaponItem && readyToEquipItem(inventoryItem)) {
                MeleeWeaponItem newItem = ((MeleeWeaponItem) inventoryItem).split(1);
                if (inventoryItem.isEmpty()) {
                    militaryInventory.remove(inventoryItem);
                }

                equipMeleeWeaponItem(newItem);
                break;
            }
        }
    }

    public boolean readyToEquipItem(AbstractInventoryItem inventoryItem) {
        Class<? extends AbstractSkill> requiredSkillLearned = inventoryItem.getParams().getRequiredSkillLearned();
        return requiredSkillLearned == null ||
                unit.getAreaObject().getTeam().getTeamSkillsManager().getSkill(requiredSkillLearned).isLearnStarted();
    }

    /** Equip better weapon (no check for bullets availability) */
    private void equipRange(MilitaryInventory militaryInventory) {
        for (AbstractInventoryItem inventoryItem : militaryInventory.getInventoryItemsSortedByValue()) {
            if (rangeWeaponItem != null && rangeWeaponItem.getParams().getValue() >= inventoryItem.getParams().getValue()) {
                // there are no better items
                break;
            }
            if (inventoryItem instanceof RangeWeaponItem && readyToEquipItem(inventoryItem)) {
                RangeWeaponItem newRangeWeapon = ((RangeWeaponItem) inventoryItem).split(1);
                if (inventoryItem.isEmpty()) {
                    militaryInventory.remove(inventoryItem);
                }

                equipRangeWeaponItem(newRangeWeapon);
                break;
            }
        }
    }

    private void equipClothes(MilitaryInventory militaryInventory) {
        for (AbstractInventoryItem inventoryItem : militaryInventory.getInventoryItemsSortedByValue()) {
            if (clothesItem != null && clothesItem.getParams().getValue() >= inventoryItem.getParams().getValue()) {
                // there are no better items
                break;
            }
            if (inventoryItem instanceof ClothesItem && readyToEquipItem(inventoryItem)) {
                ClothesItem newItem = ((ClothesItem) inventoryItem).split(1);
                if (inventoryItem.isEmpty()) {
                    militaryInventory.remove(inventoryItem);
                }

                equipClothesItem(newItem);
                break;
            }
        }
    }

    public void drop() {
        dropMeleeWeapon();
        dropRangeWeapon();
        dropRangeBullets();
        dropClothes();
    }

    public void dropMeleeWeapon() {
        if (meleeWeaponItem != null) {
            unit.getAreaObject().getMilitaryInventory().addItem(meleeWeaponItem);
            setMeleeWeaponItem(null);
        }
    }

    public void dropRangeWeapon() {
        if (rangeWeaponItem != null) {
            unit.getAreaObject().getMilitaryInventory().addItem(rangeWeaponItem);
            setRangeWeaponItem(null);
        }
    }

    public void dropRangeBullets() {
        if (rangeBulletsItem != null) {
            unit.getAreaObject().getMilitaryInventory().addItem(rangeBulletsItem);
            setRangeBulletsItem(null);
        }
    }

    private void dropRangeBulletsIfNotSupportedByWeapon(Class<? extends RangeBulletItem> supportedBulletClass) {
        if (!rangeBulletsItem.getClass().equals(supportedBulletClass)) {
            dropRangeBullets();
        }
    }

    public void dropClothes() {
        if (clothesItem != null) {
            unit.getAreaObject().getMilitaryInventory().addItem(clothesItem);
            setClothesItem(null);
        }
    }

    public void spendRangeBullet(BasicInventory inventoryWhereBulletWillStay) {
        AbstractInventoryItem splitItem = rangeBulletsItem.split(1);
        inventoryWhereBulletWillStay.addItem(splitItem);
        if (rangeBulletsItem.isEmpty()) {
            setRangeBulletsItem(null);
        }
    }
}
