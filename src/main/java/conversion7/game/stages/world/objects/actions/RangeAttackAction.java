package conversion7.game.stages.world.objects.actions;

import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AreaObject;

public class RangeAttackAction extends AbstractAreaObjectAction implements AreaViewerInputResolver {

    public RangeAttackAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        World.getAreaViewer().startInputResolving(this);
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        return input.isNeighborOf(getObject().getCell()) && input.isSeized() && !input.isSeizedByTeam(getObject().getTeam());
    }

    @Override
    public void handleInput(Cell input) {
        ((AbstractSquad) getObject()).executeRangeAttack(input.getSeizedBy());
    }

    @Override
    public void onInputHandled() {
        World.getAreaViewer().unhideSelection();
    }
}
