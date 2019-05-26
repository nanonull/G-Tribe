package conversion7.game.stages.world.ai_deprecated.events.team;

import conversion7.game.interfaces.Executable;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;

public abstract class AbstractAiTeamEvent implements Executable {

    protected AiTeamControllerOld aiTeamControllerOld;

    public void setController(AiTeamControllerOld aiTeamControllerOld) {
        this.aiTeamControllerOld = aiTeamControllerOld;
    }
}
