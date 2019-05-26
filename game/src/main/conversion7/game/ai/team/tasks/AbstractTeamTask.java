package conversion7.game.ai.team.tasks;

import conversion7.engine.ai_new.base.AiTask;
import conversion7.game.stages.world.team.Team;

public abstract class AbstractTeamTask extends AiTask<Team> {


    public AbstractTeamTask(Team owner) {
        super(owner);
    }

}
