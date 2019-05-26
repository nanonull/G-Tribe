package conversion7.game.dialogs

import conversion7.game.Assets
import conversion7.game.stages.world.objects.unit.AbstractSquad

import static conversion7.game.stages.world.objects.actions.items.SelectHeroClassAction.makeHero

class SelectHeroDialog extends AbstractGdxgDialog {

    // ===== STATES:
    static int SELECT_HERO_DIALOG_STATE = 0
    static int EXIT_STATE = 1
    private AbstractSquad squad

    SelectHeroDialog(AbstractSquad squad) {
        this.squad = squad
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(SELECT_HERO_DIALOG_STATE):
                        [
                                {
                                    text("Select hero class for unit.  ${showHeroOptions()} ")
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
        squad.team.getAvaiableHeroClasses().each { cls ->
            option(cls.getNameRoleDescription(), {
                makeHero(squad, cls)
                complete()
            })
        }
        return squad.team.getAvaiableHeroClasses()
    }

    void SELECT_HERO_DIALOG_STATE_CLOSURE() {
        return;
    }


}
