package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.TrapObject;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;

public class PlaceTrapAction extends AbstractWorldTargetableAction {
    public static final String DESC = "Place trap on cell. It deals " + TrapObject.BASE_DMG + " dmg and stun enemy";

    public PlaceTrapAction() {
        super(Group.DEFENCE);
    }

    @Override
    public String getActionWorldHint() {
        return getName();
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getShortName() {
        return "Trap";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Assets.RED;
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        TrapObject trapObject = AreaObject.create(input, getSquad(), TrapObject.class);
    }
}
