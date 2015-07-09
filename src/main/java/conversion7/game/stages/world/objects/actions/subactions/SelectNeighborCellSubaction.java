package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.TargetableOnCell;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;

import static org.fest.assertions.api.Assertions.assertThat;

public class SelectNeighborCellSubaction extends AbstractSubaction
        implements AreaViewerInputResolver {

    private TargetableOnCell targetableAction;

    public SelectNeighborCellSubaction(AbstractAreaObjectAction action) {
        super(action);
        assertThat(action).isInstanceOf(TargetableOnCell.class);
        targetableAction = (TargetableOnCell) action;
    }

    @Override
    public void execute() {
        World.getAreaViewer().startInputResolving(this);
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        return input.isNeighborOf(parentAction.getObject().getCell());
    }

    @Override
    public void handleInput(Cell input) {
        targetableAction.setTarget(input);
    }

    @Override
    public void onInputHandled() {

    }


}
