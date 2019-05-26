package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class WaitForAttackTask extends AbstractSquadTaskSingle {

    public WaitForAttackTask(AbstractSquad owner) {
        super(owner);
    }

    @Override
    public boolean execute() {
        // just wait
        return false;
    }
}
