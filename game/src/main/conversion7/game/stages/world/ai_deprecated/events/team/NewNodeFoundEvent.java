package conversion7.game.stages.world.ai_deprecated.events.team;

import conversion7.game.stages.world.landscape.Cell;

public class NewNodeFoundEvent extends AbstractAiTeamEvent {

    private Cell origin;

    public NewNodeFoundEvent(Cell origin) {
        this.origin = origin;
    }

    @Override
    public boolean execute() {
        if (aiTeamControllerOld.needsMoreNodes()) {
            aiTeamControllerOld.addAiNode(origin);
        }
        return true;
    }
}
