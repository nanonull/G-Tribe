package conversion7.game.stages.world.inventory.items.types;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;

public abstract class AbstractInventoryItem {

    public static final int DEFAULT_QTY = 1;
    public int quantity = DEFAULT_QTY;
    public InventoryItemStaticParams params;
    private Texture iconTextrure;

    public AbstractInventoryItem(InventoryItemStaticParams params) {
        this.params = params;
    }

    public String getRequiredSkillDescriptionLine() {
        return params.requiredSkill == null
                ? "- no skills required -"
                : "Required skill: " + params.requiredSkill.skillClass.getSimpleName();
    }

    public abstract int getOrder();

    public InventoryItemStaticParams getParams() {
        return params;
    }

    public int getQuantity() {
        return quantity;
    }

    public AbstractInventoryItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public TextureRegion getIconTexture() {
        return null;
    }

    public String getName() {
        return params.getName();
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    public String getDescription() {
        return getName() + "\n \n" +
                (params.getDescription() == null ? "" : params.getDescription() + "\n \n");
    }

    public boolean isEquipment() {
        return this instanceof ClothesItem ||
                this instanceof MeleeWeaponItem ||
                this instanceof RangeWeaponItem;
    }

    public boolean isFood() {
        return this instanceof FoodItem;
    }

    public abstract boolean isUsable();

    public void updateQuantity(int onValue) {
        quantity += onValue;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append(" qty=")
                .append(quantity).toString();
    }

    public <T extends AbstractInventoryItem> T splitForEquip() {
        AbstractInventoryItem currItem = this;
        if (this instanceof ClothesItem) {
            return currItem.split(1);
        } else if (this instanceof MeleeWeaponItem) {
            return currItem.split(1);
        } else if (this instanceof RangeBulletItem) {
            return currItem.split(currItem.quantity);
        } else if (this instanceof RangeWeaponItem) {
            return currItem.split(1);
        } else {
            throw new RuntimeException("Unknown type for split: " + getClass());
        }
    }

    public <T extends AbstractInventoryItem> T split(int qtyForNewItem) {
        // TODO clear on prod
        if (qtyForNewItem > quantity) {
            Utils.error(String.format("qtyForNewItem > quantity: %d,%d!", qtyForNewItem, quantity));
        }
        if (qtyForNewItem < DEFAULT_QTY) {
            Utils.error(String.format("qtyForNewItem < 1: %d!", qtyForNewItem));
        }


        T newItem;
        try {
            newItem = (T) getClass().newInstance();
            newItem.quantity = 0;
            moveQuantity(qtyForNewItem, newItem);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return newItem;
    }

    /** Returns true if item is empty */
    public boolean moveQuantity(int qtyForNewItem, AbstractInventoryItem target) {
        this.updateQuantity(-qtyForNewItem);
        target.updateQuantity(+qtyForNewItem);
        return isEmpty();
    }

    public void useBy(Unit unit) {
        unit.squad.team.getInventory().addItem(this.getClass(), -1);
        unit.squad.updateMoveAp(-AbstractSquad.USE_ITEM_AP);
        Gdxg.clientUi.getInventoryWindow().refresh(unit.squad);
    }
}
