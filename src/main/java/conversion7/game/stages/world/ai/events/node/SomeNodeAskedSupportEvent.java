package conversion7.game.stages.world.ai.events.node;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.ai.tasks.single.SupportNodeTask;
import conversion7.game.stages.world.objects.HumanSquad;

public class SomeNodeAskedSupportEvent extends AbstractAiNodeEvent {

    private AiNode aiNodeNeedsSupport;

    public SomeNodeAskedSupportEvent(AiNode aiNodeNeedsSupport) {
        this.aiNodeNeedsSupport = aiNodeNeedsSupport;
    }

    @Override
    public boolean execute() {
        if (aiNodeOwner.couldSendSupport()) {
            Array<HumanSquad> armiesInNodeArea = aiNodeOwner.getOwnObjectsInNodeArea(HumanSquad.class);
            for (HumanSquad army : armiesInNodeArea) {
                army.addTaskToWipSet(new SupportNodeTask(army, aiNodeNeedsSupport));
            }
        }
        return true;
    }
}
