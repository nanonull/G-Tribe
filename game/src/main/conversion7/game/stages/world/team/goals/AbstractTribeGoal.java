package conversion7.game.stages.world.team.goals;

import conversion7.game.stages.world.team.Team;

public abstract class AbstractTribeGoal {
    public abstract boolean isValid();

    public abstract void execute(Team executorTeam);
}
