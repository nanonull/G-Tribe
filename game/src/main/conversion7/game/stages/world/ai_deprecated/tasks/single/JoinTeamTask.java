package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class JoinTeamTask extends AbstractSquadTaskSingle {

    private Team target;

    public JoinTeamTask(AbstractSquad owner, Team target) {
        super(owner);
        this.target = target;
    }

    @Override
    public boolean execute() {
        if (owner.getTeam() != target) {
            target.joinSquad(owner);
            return true;
        } else {
            return false;
        }
    }
}
