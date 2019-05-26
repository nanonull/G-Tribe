package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.items.BallistaShotAction;
import conversion7.game.stages.world.objects.unit.WorldSquad;

public abstract class AbstractHumanSquadAction extends AbstractWorldTargetableAction {

    public AbstractHumanSquadAction(Group group) {
        super(group);
    }

    protected Array<Cell> getAcceptableCells() {
        return BallistaShotAction.getTeamSightCells(getSquad(), getDistance());
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public WorldSquad getSquad() {
        return (WorldSquad) super.getObject();
    }

}
