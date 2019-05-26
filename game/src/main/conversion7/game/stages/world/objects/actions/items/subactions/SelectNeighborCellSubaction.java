package conversion7.game.stages.world.objects.actions.items.subactions;

import conversion7.engine.Gdxg;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.TargetableOnCell;
import conversion7.game.stages.world.actions.AbstractSubaction;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;

import static org.fest.assertions.api.Assertions.assertThat;

@Deprecated
public class SelectNeighborCellSubaction extends AbstractSubaction
        implements AreaViewerInputResolver {

    private TargetableOnCell targetableAction;
    private AbstractAreaObjectAction abstractAreaObjectAction;

    public SelectNeighborCellSubaction(AbstractAreaObjectAction abstractAreaObjectAction) {
        super(abstractAreaObjectAction);
        this.abstractAreaObjectAction = abstractAreaObjectAction;
        assertThat(abstractAreaObjectAction).isInstanceOf(TargetableOnCell.class);
        targetableAction = (TargetableOnCell) abstractAreaObjectAction;
    }

    @Override
    public void execute() {
        Gdxg.getAreaViewer().startInputResolving(this);
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        return input.isNeighborOf(abstractAreaObjectAction.getObject().getLastCell());
    }

    @Override
    public void beforeInputHandle() {

    }

    @Override
    public void handleAcceptedInput(Cell input) {
        targetableAction.setTarget(input);
    }

    @Override
    public void afterInputHandled() {

    }

    @Override
    public boolean hasAcceptableDistanceTo(Cell mouseOverCell) {
        return false;
    }


}
