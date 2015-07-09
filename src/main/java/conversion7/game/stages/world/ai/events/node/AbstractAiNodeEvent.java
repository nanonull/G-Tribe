package conversion7.game.stages.world.ai.events.node;

import conversion7.game.interfaces.Executable;
import conversion7.game.stages.world.ai.AiNode;

public abstract class AbstractAiNodeEvent implements Executable {

    AiNode aiNodeOwner;

    public void setNode(AiNode aiNode) {
        this.aiNodeOwner = aiNode;
    }
}
