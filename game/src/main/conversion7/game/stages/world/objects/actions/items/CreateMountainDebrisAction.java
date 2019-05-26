package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.MountainDebris;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;

public class CreateMountainDebrisAction extends AbstractWorldTargetableAction {
    public static final String DESC = "Create mountain debris";

    public CreateMountainDebrisAction() {
        super(Group.DEFENCE);
    }

    @Override
    public String getActionWorldHint() {
        return "CreateMountainDebrisAction";
    }

    @Override
    public int getDistance() {
        return 1;
    }

    @Override
    public String getShortName() {
        return "CreMount";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.CYAN;
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        new MountainDebris(input);
        getSquad().getInventory().remove(ResourceCosts.getCost(MountainDebris.class));
    }
}
