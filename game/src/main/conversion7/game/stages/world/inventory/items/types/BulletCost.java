package conversion7.game.stages.world.inventory.items.types;

public class BulletCost {
    private final Class<? extends AbstractInventoryItem> bulletClass;
    private final int qty;
    private final String hint;

    public BulletCost(Class<? extends AbstractInventoryItem> bulletClass, int qty) {

        this.bulletClass = bulletClass;
        this.qty = qty;
        hint = bulletClass.getSimpleName() + ":" + qty;
    }

    public int getQty() {
        return qty;
    }

    public Class<? extends AbstractInventoryItem> getBulletClass() {
        return bulletClass;
    }

    @Override
    public String toString() {
        return hint;
    }
}
