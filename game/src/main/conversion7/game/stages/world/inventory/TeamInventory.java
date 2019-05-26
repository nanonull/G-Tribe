package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.types.BulletCost;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class TeamInventory extends BasicInventory {

    private Team team;

    public TeamInventory(Team team) {
        this.team = team;
    }

    @Override
    public void validate() {
        if (!valid && !batch) {
            super.validate();

            team.getCraftInventory().update();


            for (AbstractSquad squad : team.getSquadsIter()) {
                squad.getActionsController().invalidate();
                squad.getActionsController().runTreeValidation();
                squad.validate();
            }
            if (team.isHumanActivePlayer()) {
                Gdxg.clientUi.getInventoryWindow().refresh();
                Gdxg.clientUi.getTribeResorcesPanel().showFor(team);
            }
        }
    }

    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int addQty, Cell notifyCell) {
        if (addQty > 0 && notifyCell != null) {
            notifyCell.addFloatLabel(itemClass.getSimpleName() + " +" + addQty, Color.CYAN);
        }
        return addItem(itemClass, addQty);
    }

    @Override
    public AbstractInventoryItem addItem(Class<? extends AbstractInventoryItem> itemClass, int addQty) {
        AbstractInventoryItem item = super.addItem(itemClass, addQty);
        invalidate();
        return item;
    }

    public boolean hasEnoughResources(ObjectMap.Entries<Class<? extends AbstractInventoryItem>, Integer> cost) {
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, Integer> entry : cost) {
            AbstractInventoryItem item = getItem(entry.key);
            if (item == null || item.quantity < entry.value) {
                return false;
            }
        }
        return true;
    }


    public boolean hasResourcesToShot(RangeWeaponItem rangeWeaponItem) {
        BulletCost bulletCost = rangeWeaponItem.getBulletCost();
        AbstractInventoryItem bullet = getItem(bulletCost.getBulletClass());
        if (bullet == null) {
            return false;
        }
        return bullet.quantity >= bulletCost.getQty();
    }
}
