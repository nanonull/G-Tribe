package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.TargetableOnCell;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;

import static org.fest.assertions.api.Assertions.assertThat;

public class SelectNeighborCellForUnitsSplitMergeSubaction extends SelectNeighborCellSubaction
        implements AreaViewerInputResolver {

    private TargetableOnCell targetableAction;

    public SelectNeighborCellForUnitsSplitMergeSubaction(AbstractAreaObjectAction action) {
        super(action);
        assertThat(action).isInstanceOf(TargetableOnCell.class);
        targetableAction = (TargetableOnCell) action;
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        if (!super.couldAcceptInput(input)) {
            return false;
        }

        if (input.isSeized()) {
            return !input.getSeizedBy().isAnimalHerd();
        } else {
            return input.hasLandscapeAvailableForMove() && World.getAreaViewer().selectedObject.getCell() != input;
        }
    }

}
