package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.ai_deprecated.AiNode;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class SupportNodeTaskOld extends MoveTaskOld {

    private AiNode nodeMoveTo;

    public SupportNodeTaskOld(AbstractSquad owner, AiNode nodeMoveTo) {
        super(owner, nodeMoveTo.origin);
        this.nodeMoveTo = nodeMoveTo;
    }

}
