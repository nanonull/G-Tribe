package conversion7.game.dialogs

import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem
import conversion7.game.Assets
import conversion7.game.stages.world.unit.Unit

// see PrimalExperienceDialog1
@Deprecated
class RitualDialog extends AbstractGdxgDialog {

    // STATES:
    static int RITUAL_STATE = 0
    static int INCREASE_EXP_LEVEL_STATE = 1
    static int MAKE_SHAMAN_STATE = 2
    static int EXIT_STATE = 3
    private Unit unit

    RitualDialog(Unit unit) {
        this.unit = unit
    }

    @Override
    protected void initQuestState() {
        focusOn(unit.squad.getLastCell())
    }

/** DIALOG STATES DEFINITION MAP */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(RITUAL_STATE)            :
                        [
                                {
                                    text(" Ritual started for unit ${getUnitDescription()}. Choose ritual options.")
                                    option(Assets.textResources.get("MAKE_SHAMAN"), {
                                        newState(MAKE_SHAMAN_STATE)
                                    })
                                    option(Assets.textResources.get("INCREASE_EXP_LEVEL"), {
                                        newState(INCREASE_EXP_LEVEL_STATE)
                                    })
                                }
                        ],

                (INCREASE_EXP_LEVEL_STATE):
                        [
                                {
                                    increaseExpLevel()
                                    option(Assets.textResources.get("EXIT"), {
                                        newState(EXIT_STATE)
                                    })
                                }
                        ],

                (MAKE_SHAMAN_STATE)       :
                        [
                                {
                                    makeShaman()
                                    option(Assets.textResources.get("EXIT"), {
                                        newState(EXIT_STATE)
                                    })
                                }
                        ],
                (EXIT_STATE)              :
                        [
                                {
                                    exit()
                                }
                        ],

        ]
    }
    // CLOSURES:
    String exit() {
        complete()
        focusOn(unit.squad.getLastCell())
        FloatingStatusOnCellSystem.scheduleMessage(unit.squad.lastCell,
                unit.squad.team, "Ritual completed")
    }

    String getUnitDescription() {
        return unit.name
    }

    String makeShaman() {
        if (unit.squad.team.hasShaman()) {
            text("Team already has shaman.")
        } else {
            text("Unit becomes a shaman!")
            unit.becomeShaman()
        }
    }

    String increaseExpLevel() {
        text("Unit gets 1 more experience level.")
        unit.updateExperience(unit.getExperienceForNextLevelLeft())
    }
}
