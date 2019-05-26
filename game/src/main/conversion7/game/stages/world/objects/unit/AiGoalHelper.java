package conversion7.game.stages.world.objects.unit;

import com.badlogic.gdx.utils.Predicate;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.team.Team;

public class AiGoalHelper {

    public static boolean canTargetTeam(Team targTeam) {
        return targTeam != null
                && targTeam.getSquads().size > 0;
    }

    public static Predicate<AbstractSquad> moveToAndAttackTribe(Team team) {
        return owner -> {
            if (!canTargetTeam(team)) {
                return true;
            }
            AbstractSquad target = team.getSquads().get(0);
            owner.addAiTask(new MoveTask(owner, target.cell, AiTaskType.MOVE_FOR_TRIBE_GOAL));
            return false;
        };
    }
}
