package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;

public class RideAction extends AbstractWorldTargetableAction {

    public RideAction() {
        super(Group.ATTACK);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "ride";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName();
    }

    @Override
    public void handleAcceptedInput(Cell input) {
//        AnimalHerd animalHerd = (AnimalHerd) input.getSquad();
//        getSquad().ride(animalHerd)
    }

}
