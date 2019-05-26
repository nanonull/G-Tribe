package conversion7.game.dialogs

import conversion7.game.Assets
import conversion7.game.stages.world.objects.actions.items.SelectUnitSpecAction
import conversion7.game.stages.world.objects.unit.AbstractSquad

// no dia!
class SelectSpecDialog extends AbstractGdxgDialog {

    // ===== STATES:
    static int SELECT_HERO_DIALOG_STATE = 0
    static int EXIT_STATE = 1
    private AbstractSquad squad

    SelectSpecDialog(AbstractSquad squad) {
        this.squad = squad
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(SELECT_HERO_DIALOG_STATE):
                        [
                                {
                                    text("Choose unit specialization.")
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
    String showHeroOptions() {
        squad.team.getAvaiableUnitSpecs().each { cls ->
            option(cls.getFullDescription(), {
                SelectUnitSpecAction.makeSpec(squad, cls)
                complete()
            })
        }
        return squad.team.getAvaiableUnitSpecs()
    }

    void SELECT_HERO_DIALOG_STATE_CLOSURE() {
        showHeroOptions()
    }


}
