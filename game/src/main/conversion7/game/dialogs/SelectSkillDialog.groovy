package conversion7.game.dialogs

import conversion7.engine.dialog.QuestOption
import conversion7.game.Assets
import conversion7.game.stages.world.objects.unit.AbstractSquad

// no dia!
class SelectSkillDialog extends AbstractGdxgDialog {

    // ===== STATES:
    static int SELECT_HERO_DIALOG_STATE = 0
    static int EXIT_STATE = 1
    private AbstractSquad squad

    SelectSkillDialog(AbstractSquad squad) {
        this.squad = squad
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(SELECT_HERO_DIALOG_STATE):
                        [
                                {
                                    text("Choose unit skill.")
                                    SELECT_HERO_DIALOG_STATE_CLOSURE()
                                    option(Assets.textResources.get("RETURN_TO_WORLD"), {
                                        newState(EXIT_STATE)
                                    })
                                }
                        ],

                (EXIT_STATE)              :
                        [
                                {
                                    complete()
                                }
                        ],

        ]
    }

    // ===== CLOSURES:

    void SELECT_HERO_DIALOG_STATE_CLOSURE() {
        squad.getAvailableSkillsToLearn().each { skill ->
            def name = skill.isPassive() ? skill.getName() + " [PASSIVE]" : skill.getName();
            QuestOption opt = option(name, {
                skill.learn(squad)
                complete()
            })
            opt.setDescription(skill.getDescription())
        }
    }


}
