package conversion7.game.stages.world.ai.events.team;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.ai.AiNode;
import conversion7.game.stages.world.ai.events.node.SomeNodeAskedSupportEvent;
import conversion7.game.stages.world.ai.tasks.group.AttackTaskGroup;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;

public class StrangerNearNodeEvent extends AbstractAiTeamEvent {


    private AiNode aiNodeWarned;
    private Array<AreaObject> strangers;
    boolean supportAsked = false;

    public StrangerNearNodeEvent(Array<AreaObject> strangers, AiNode aiNode) {
        this.strangers = strangers;
        this.aiNodeWarned = aiNode;
    }

    @Override
    public boolean execute() {
        Array<HumanSquad> nodeArmies = aiNodeWarned.getOwnObjectsInNodeArea(HumanSquad.class);
        checkIfNeedSupport(nodeArmies);
        createOrRefreshNodeDefenceTask(nodeArmies);
        return true;
    }

    private void createOrRefreshNodeDefenceTask(Array<HumanSquad> nodeArmies) {
        if (aiNodeWarned.defenceTask == null) {
            aiNodeWarned.defenceTask = new AttackTaskGroup(aiTeamController, strangers);
        } else {
            aiNodeWarned.defenceTask.refreshTargets(strangers);
        }
        aiNodeWarned.defenceTask.refreshActors(nodeArmies);
    }

    private void checkIfNeedSupport(Array<HumanSquad> nodeArmies) {
        float strangersPower = AreaObject.getObjectsPower(strangers);
        float nodePower = AreaObject.getObjectsPower(nodeArmies);

        if (!supportAsked) {
            if (nodePower < strangersPower && aiTeamController.nodes.size > 1) {
                aiNodeWarned.origin.sortNodesByDistance(aiTeamController);
                for (int i = 1; i < aiTeamController.nodes.size; i++) {
                    AiNode neighborNode = aiTeamController.nodes.get(i);
                    if (aiNodeWarned.origin.distanceTo(neighborNode.origin) < 20) {
                        neighborNode.addEvent(new SomeNodeAskedSupportEvent(aiNodeWarned));
                    }
                }
                supportAsked = true;
            }
        }
    }
}
