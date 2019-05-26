package conversion7.game.stages.world.ai_deprecated.events.node;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.ai_deprecated.AiNode;
import conversion7.game.stages.world.ai_deprecated.tasks.single.SupportNodeTaskOld;
import conversion7.game.stages.world.objects.unit.WorldSquad;

public class SomeNodeAskedSupportEvent extends AbstractAiNodeEvent {

    private AiNode aiNodeNeedsSupport;

    public SomeNodeAskedSupportEvent(AiNode aiNodeNeedsSupport) {
        this.aiNodeNeedsSupport = aiNodeNeedsSupport;
    }

    @Override
    public boolean execute() {
        if (aiNodeOwner.couldSendSupport()) {
            Array<WorldSquad> armiesInNodeArea = aiNodeOwner.getOwnObjectsInNodeArea(WorldSquad.class);
            for (WorldSquad army : armiesInNodeArea) {
                army.addTaskToWipSet(new SupportNodeTaskOld(army, aiNodeNeedsSupport));
            }
        }
        return true;
    }
}
