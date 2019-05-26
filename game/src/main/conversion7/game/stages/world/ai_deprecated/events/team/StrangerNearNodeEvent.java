package conversion7.game.stages.world.ai_deprecated.events.team;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.ai_deprecated.AiNode;
import conversion7.game.stages.world.ai_deprecated.events.node.SomeNodeAskedSupportEvent;
import conversion7.game.stages.world.ai_deprecated.tasks.group.AttackTaskGroup;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;

public class StrangerNearNodeEvent extends AbstractAiTeamEvent {


    boolean supportAsked = false;
    private AiNode aiNodeWarned;
    private Array<AbstractSquad> strangers;

    public StrangerNearNodeEvent(Array<AbstractSquad> strangers, AiNode aiNode) {
        this.strangers = strangers;
        this.aiNodeWarned = aiNode;
    }

    @Override
    public boolean execute() {
        Array<WorldSquad> nodeArmies = aiNodeWarned.getOwnObjectsInNodeArea(WorldSquad.class);
        checkIfNeedSupport(nodeArmies);
        createOrRefreshNodeDefenceTask(nodeArmies);
        return true;
    }

    private void checkIfNeedSupport(Array<WorldSquad> nodeArmies) {
        float strangersPower = AbstractSquad.getObjectsPower(strangers);
        float nodePower = AbstractSquad.getObjectsPower(nodeArmies);

        if (!supportAsked) {
            if (nodePower < strangersPower && aiTeamControllerOld.nodes.size > 1) {
                aiNodeWarned.origin.sortNodesByDistance(aiTeamControllerOld);
                for (int i = 1; i < aiTeamControllerOld.nodes.size; i++) {
                    AiNode neighborNode = aiTeamControllerOld.nodes.get(i);
                    if (aiNodeWarned.origin.distanceTo(neighborNode.origin) < 20) {
                        neighborNode.addEvent(new SomeNodeAskedSupportEvent(aiNodeWarned));
                    }
                }
                supportAsked = true;
            }
        }
    }

    private void createOrRefreshNodeDefenceTask(Array<WorldSquad> nodeArmies) {
        if (aiNodeWarned.defenceTask == null) {
            aiNodeWarned.defenceTask = new AttackTaskGroup(aiTeamControllerOld, strangers);
        } else {
            aiNodeWarned.defenceTask.refreshTargets(strangers);
        }
        aiNodeWarned.defenceTask.refreshActors(nodeArmies);
    }
}
