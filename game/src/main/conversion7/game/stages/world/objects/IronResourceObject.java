package conversion7.game.stages.world.objects;

import conversion7.game.stages.world.inventory.items.IronOreItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;

public class IronResourceObject extends ResourceObject {

    public IronResourceObject(Cell cell, Team team) {
        super(cell, team);
    }

    @Override
    public void tick() {
        team.getInventory().addItem(IronOreItem.class, 1, cell);
    }
}
