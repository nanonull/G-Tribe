package conversion7.game.stages.world.objects;

import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.UranusItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class UranusResourceObject extends ResourceObject {

    public UranusResourceObject(Cell cell, Team team) {
        super(cell, team);
    }
    @Override
    public boolean givesExpOnHurt() {
        return false;
    }
    @Override
    public void tick() {
        team.getInventory().addItem(RadioactiveIsotopeItem.class, 1, cell);
    }
}
