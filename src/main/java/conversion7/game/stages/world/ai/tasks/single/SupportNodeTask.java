package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.utils.PropertiesLoader;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.objects.AreaObject;

public class SupportNodeTask extends MoveTask {

    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.SupportNodeTask");

    private AiNode nodeMoveTo;

    public SupportNodeTask(AreaObject owner, AiNode nodeMoveTo) {
        super(owner, nodeMoveTo.origin, DEFAULT_PRIORITY);
        this.nodeMoveTo = nodeMoveTo;
    }

}
