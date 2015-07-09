package conversion7.game.stages.quest.dialog

import conversion7.engine.Gdxg
import conversion7.engine.quest.AbstractQuest
import conversion7.engine.quest.QuestOption
import conversion7.game.run.RunAndScheduleLibrary
import conversion7.game.stages.world.objects.AbstractSquad
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.strings.ResourceKey

class DialogWithSquadQuest extends AbstractQuest {

    final QuestOption ATTACK = new QuestOption(ResourceKey.ATTACK.getValue(),
            { ->
                completeAndEnableInteraction()
                initiatorObject.attack(targetObject);

            });

    final QuestOption RETURN_BACK = new QuestOption(ResourceKey.RETURN_BACK.getValue(),
            { ->
                completeAndEnableInteraction()
                RunAndScheduleLibrary.scheduleCameraFocusOn(50, initiatorObject)
            });

    final QuestOption SPLIT_MERGE = new QuestOption(ResourceKey.DWSQ_SPLIT_MERGE.getValue(),
            { ->
                complete()
                Gdxg.clientUi.getSplitMergeUnitsWindow().showFor(initiatorObject, targetObject.cell);
            });

    final QuestOption DWSQ_JOIN = new QuestOption(ResourceKey.DWSQ_JOIN.getValue(),
            { ->
                joinAsked = true
                if (initiatorObject.couldJoinToTeam(targetObject)) {
                    initiatorObject.getTeam().joinSquad((AbstractSquad) targetObject)
                    addDescriptionRow(ResourceKey.DWSQ_SQUAD_JOINED)
                } else {
                    addDescriptionRow(ResourceKey.DWSQ_SQUAD_JOIN_FAILED)
                }
            });

    private AreaObject initiatorObject
    private AreaObject targetObject
    boolean joinAsked

    DialogWithSquadQuest(AreaObject initiatorObject, AreaObject targetObject) {
        this.targetObject = targetObject
        this.initiatorObject = initiatorObject
    }

    @Override
    protected void initQuestState() {
        setPictureViewActive(false)

        addCommonOptionsAndDescription();

        RunAndScheduleLibrary.scheduleCameraFocusOn(50, targetObject)
    }

    def addCommonOptionsAndDescription() {
        addDescriptionRow(ResourceKey.DWSQ_DESCRIPTION)

        if (initiatorObject.team.equals(targetObject.team)) {
            addChoiceOption(SPLIT_MERGE)
        } else {
            addChoiceOption(ATTACK)
            if (targetObject.isHumanSquad() && !joinAsked) {
                addChoiceOption(DWSQ_JOIN)
            }
        }
        addChoiceOption(RETURN_BACK)
    }

    @Override
    protected void runPostClosureGlobalPhase() {
        addCommonOptionsAndDescription()
    }
}
