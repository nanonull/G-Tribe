package conversion7.game.stages.world.team.goals;

import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.AttackObjectTask;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class AttackObjectGoal extends AbstractTribeGoal {
    private AreaObject target;

    public AttackObjectGoal(AreaObject target) {
        this.target = target;
    }

    public static void attackObject(Team active, AreaObject target) {
        for (AbstractSquad squad : active.getSquads()) {
            if (AttackObjectTask.isApplicable(squad, target)) {
                squad.addAiTask(new AttackObjectTask(squad, target)
                        .setPriority(AiTaskType.MOVE_FOR_TRIBE_GOAL.priority));
            } else {
                squad.addAiTask(new MoveTask(squad, target.getLastCell(), AiTaskType.MOVE_FOR_TRIBE_GOAL));
            }
        }

    }

    @Override
    public boolean isValid() {
        return !target.isRemovedFromWorld();
    }

    @Override
    public void execute(Team executorTeam) {
        attackObject(executorTeam, target);
    }
}
