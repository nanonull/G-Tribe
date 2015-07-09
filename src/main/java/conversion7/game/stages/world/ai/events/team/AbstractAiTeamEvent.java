package conversion7.game.stages.world.ai.events.team;

import conversion7.game.interfaces.Executable;
import conversion7.game.stages.world.ai.AiTeamController;

public abstract class AbstractAiTeamEvent implements Executable {

    protected AiTeamController aiTeamController;

    public void setController(AiTeamController aiTeamController) {
        this.aiTeamController = aiTeamController;
    }
}
