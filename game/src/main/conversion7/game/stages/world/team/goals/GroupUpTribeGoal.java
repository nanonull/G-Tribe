package conversion7.game.stages.world.team.goals;

import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class GroupUpTribeGoal extends AbstractTribeGoal {
    private Team team;

    public GroupUpTribeGoal(Team team) {
        this.team = team;
    }

    @Override
    public boolean isValid() {
        return !team.isDefeated();
    }

    @Override
    public void execute(Team executorTeam) {
        Cell tribeCenterPoint = executorTeam.getTribeCenterPoint();
        for (AbstractSquad squad : executorTeam.getSquads()) {
            squad.addAiTask(new MoveTask(squad, tribeCenterPoint, AiTaskType.MOVE_FOR_TRIBE_GOAL));
        }
    }
}
