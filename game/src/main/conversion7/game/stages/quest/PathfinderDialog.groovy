package conversion7.game.stages.quest

import conversion7.engine.quest_old.AbstractQuest
import conversion7.engine.quest_old.QuestOption
import conversion7.game.Assets
import conversion7.game.PictureAsset
import conversion7.game.services.WorldServices
import conversion7.game.stages.world.objects.actions.items.PathfinderAction

class PathfinderDialog extends AbstractQuest{
    private PathfinderAction pathfinderAction

    final QuestOption EXIT = new QuestOption("Exit",
            { ->
                completeAndEnableInteraction()
            })

    PathfinderDialog(PathfinderAction pathfinderAction) {
        this.pathfinderAction = pathfinderAction
    }

    @Override
    protected void initQuestState() {
        addDescriptionRow(pathfinderAction.getEnvironmentDescription())
        addChoiceOption(EXIT)
    }

    @Override
    protected void runPostClosureGlobalPhase() {

    }
}
