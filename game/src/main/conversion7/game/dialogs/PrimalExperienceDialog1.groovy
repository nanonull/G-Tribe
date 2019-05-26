package conversion7.game.dialogs

import conversion7.game.Assets
import conversion7.game.stages.world.objects.PrimalExperienceJewel
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.hero_classes.HeroClass

class PrimalExperienceDialog1 extends AbstractGdxgDialog {

    // STATES:
    static int PRIMAL_JEWEL_DIALOG1_STATE = 0
    static int START_RITUAL_STATE = 1
    static int BECOME_A_SHAMAN_STATE = 2
    static int MORE_EXP_STATE = 3
    static int GET_ABILITY_CHARGE_STATE = 4
    static int YES_GET_CHARGE_STATE = 5
    static int NO_STATE = 6
    static int CHOOSE_GOD_STATE = 7
    static int CHANGE_GOD_STATE = 8

    private Unit unit
    private PrimalExperienceJewel jewel

    PrimalExperienceDialog1(Unit unit, PrimalExperienceJewel jewel) {
        this.jewel = jewel
        this.unit = unit
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(PRIMAL_JEWEL_DIALOG1_STATE):
                        [
                                {
                                    text(" You have found primal experience.   Unit got ${expAmount()} exp.")
                                    PRIMAL_JEWEL_DIALOG1_STATE_CLOSURE()

                                    if (canDoRitual()) {
                                        option(Assets.textResources.get("START_RITUAL"), {
                                            newState(START_RITUAL_STATE)
                                        })
                                    }
                                }
                        ],

                (START_RITUAL_STATE)        :
                        [
                                {
                                    text(" Какой ритуал выполнить?")
                                    START_RITUAL_STATE_CLOSURE()

                                    option(Assets.textResources.get("MORE_EXP"), {
                                        newState(MORE_EXP_STATE)
                                    })
                                    option(Assets.textResources.get("GET_ABILITY_CHARGE"), {
                                        newState(GET_ABILITY_CHARGE_STATE)
                                    })
                                    if (canChangeGod()) {
                                        option(Assets.textResources.get("CHANGE_GOD"), {
                                            newState(CHANGE_GOD_STATE)
                                        })
                                    }
                                    if (canBeShaman()) {
                                        option(Assets.textResources.get("BECOME_A_SHAMAN"), {
                                            newState(BECOME_A_SHAMAN_STATE)
                                        })
                                    }
                                }
                        ],

                (BECOME_A_SHAMAN_STATE)     :
                        [
                                {
                                    text(" Юнит стал шаманом.")
                                    BECOME_A_SHAMAN_STATE_CLOSURE()
                                    option(Assets.textResources.get("CHOOSE_GOD"), {
                                        newState(CHOOSE_GOD_STATE)
                                    })
                                }
                        ],

                (MORE_EXP_STATE)            :
                        [
                                {
                                    MORE_EXP_STATE_CLOSURE()

                                }
                        ],

                (GET_ABILITY_CHARGE_STATE)  :
                        [
                                {
                                    GET_ABILITY_CHARGE_STATE_CLOSURE()

                                    option(Assets.textResources.get("YES_GET_CHARGE"), {
                                        newState(YES_GET_CHARGE_STATE)
                                    })
                                    option(Assets.textResources.get("NO"), {
                                        newState(NO_STATE)
                                    })
                                }
                        ],

                (YES_GET_CHARGE_STATE)      :
                        [
                                {
                                    YES_GET_CHARGE_STATE_CLOSURE()

                                }
                        ],

                (NO_STATE)                  :
                        [
                                {
                                    NO_STATE_CLOSURE()

                                    if (canDoRitual()) {
                                        option(Assets.textResources.get("START_RITUAL"), {
                                            newState(START_RITUAL_STATE)
                                        })
                                    }
                                }
                        ],

                (CHOOSE_GOD_STATE)          :
                        [
                                {
                                    CHOOSE_GOD_STATE_CLOSURE()

                                }
                        ],

                (CHANGE_GOD_STATE)          :
                        [
                                {
                                    CHANGE_GOD_STATE_CLOSURE()

                                    option(Assets.textResources.get("CHOOSE_GOD"), {
                                        newState(CHOOSE_GOD_STATE)
                                    })
                                }
                        ],

        ]
    }

    @Override
    void complete() {
        super.complete()
        unit.squad.team.scenario.sawExpJewel = true
    }

    // ===== CLOSURES:
    void CHOOSE_GOD_STATE_CLOSURE() {
        unit.squad.team.world.godsGlobalStats.gods.values().toList().forEach({ god ->
            option("Select " + god.name, {
                unit.squad.myGod = god
                complete()
            })
        })
    }

    void CHANGE_GOD_STATE_CLOSURE() {
    }

    Boolean canChangeGod() {
        return unit.squad.myGod != null;
    }

    void MORE_EXP_STATE_CLOSURE() {
        jewel.consumeBy(unit.squad);
        text("Unit got ${expAmount()} more exp.")
    }

    void GET_ABILITY_CHARGE_STATE_CLOSURE() {
        text("You can get 100% of ability charge. Now you have: ${unit.squad.inspirationPercent}%")
    }

    void YES_GET_CHARGE_STATE_CLOSURE() {
        unit.squad.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX);
    }

    void NO_STATE_CLOSURE() {
    }

    void PRIMAL_JEWEL_DIALOG1_STATE_CLOSURE() {
        jewel.consumeBy(unit.squad)
        focusOn(unit.squad.lastCell)
    }

    void START_RITUAL_STATE_CLOSURE() {
    }

    void BECOME_A_SHAMAN_STATE_CLOSURE() {
        unit.squad.becomeShaman()
    }

    Boolean canDoRitual() {
        return unit.squad.team.teamSkillsManager.getSkill(SkillType.RITUAL).isLearnStarted()
    }

    Boolean canBeShaman() {
        return unit.squad.heroClass == HeroClass.DRUID && !unit.squad.isShaman();
    }

    String expAmount() {
        return jewel.exp;
    }

}
