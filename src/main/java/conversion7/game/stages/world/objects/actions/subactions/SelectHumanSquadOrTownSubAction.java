package conversion7.game.stages.world.objects.actions.subactions;

import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.interfaces.TargetableOnObject;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.ui.UiLogger;

import static org.fest.assertions.api.Assertions.assertThat;

public class SelectHumanSquadOrTownSubAction extends AbstractSubaction
        implements AreaViewerInputResolver {

    private TargetableOnObject targetableAction;

    public SelectHumanSquadOrTownSubAction(AbstractAreaObjectAction action) {
        super(action);
        assertThat(action).isInstanceOf(TargetableOnObject.class);
        targetableAction = (TargetableOnObject) action;
    }

    @Override
    public void execute() {
        World.getAreaViewer().startInputResolving(this);
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        if (input.isSeized() && parentAction.getObject().getCell().isNeighborOf(input)) {
            if ((input.getSeizedBy().isHumanSquad() || input.getSeizedBy().isTownFragment())) {
                return true;
            } else {
                UiLogger.addInfoLabel("HumanSquad/TownFragment could be selected only");
            }
        }
        return false;
    }

    @Override
    public void handleInput(Cell input) {
        targetableAction.setTarget(input.getSeizedBy());
    }

    @Override
    public void onInputHandled() {

    }


}
