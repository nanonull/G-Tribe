package conversion7.game.stages.world.ai.events.team;

import conversion7.game.stages.world.landscape.Cell;

public class NewNodeFoundEvent extends AbstractAiTeamEvent {

    private Cell origin;

    public NewNodeFoundEvent(Cell origin) {
        this.origin = origin;
    }

    @Override
    public boolean execute() {
        if (aiTeamController.needsMoreNodes()) {
            aiTeamController.addAiNode(origin);
        }
        return true;
    }
}
