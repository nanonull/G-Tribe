package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class MoveToAttackTaskOld extends MoveTaskOld {

    public MoveToAttackTaskOld(AbstractSquad owner, Cell moveTo) {
        super(owner, moveTo);
    }

}
