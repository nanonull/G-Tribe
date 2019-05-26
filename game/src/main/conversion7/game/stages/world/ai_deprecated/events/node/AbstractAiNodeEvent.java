package conversion7.game.stages.world.ai_deprecated.events.node;

import conversion7.game.interfaces.Executable;
import conversion7.game.stages.world.ai_deprecated.AiNode;

public abstract class AbstractAiNodeEvent implements Executable {

    AiNode aiNodeOwner;

    public void setNode(AiNode aiNode) {
        this.aiNodeOwner = aiNode;
    }
}
