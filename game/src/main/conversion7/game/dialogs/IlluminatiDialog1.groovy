package conversion7.game.dialogs

import com.badlogic.gdx.graphics.Color
import conversion7.game.Assets
import conversion7.game.stages.world.adventure.IlluminatiCampaign
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.quest.items.IlluminatiCampaignQuest
import conversion7.game.stages.world.team.Team

public class IlluminatiDialog1 extends AbstractGdxgDialog {

    // ===== STATES:
    static int START_STATE = 0
    static int HE_DEAD_LIE_STATE = 1
    static int HE_DEAD_TRUTH_STATE = 2
    static int GIVE_UNIT_STATE = 3
    static int GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE_STATE = 4
    static int MY_NE_OTDADIM_STATE = 5
    static int MY_EGO_NE_VIDELI_STATE = 6
    static int NICHEGO_NE_GOVORIT_STATE = 7

    private Team playerTeam
    private Team illumTeam
    AbstractSquad archon
    private Cell onCell

    IlluminatiDialog1(Team playerTeam, Team illumTeam, Cell onCell) {
        this.onCell = onCell
        this.illumTeam = illumTeam
        this.playerTeam = playerTeam

        def unitControlsTribe = playerTeam.getUnitControlsTribe()
        if (unitControlsTribe != null && unitControlsTribe.squad.isArchon() && unitControlsTribe.squad.isAlive()) {
            archon = unitControlsTribe.squad
        }
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [
                (START_STATE)                           :
                        [
                                {
                                    text(Assets.textResources.get("ILLUM_GREETING_1"))
                                    START_STATE_CLOSURE()

                                    if (isArchonAlive()) {
                                        option(Assets.textResources.get("HE_DEAD_LIE"), {
                                            newState(HE_DEAD_LIE_STATE)
                                        })
                                    }
                                    if (isArchonDead()) {
                                        option(Assets.textResources.get("HE_DEAD_TRUTH"), {
                                            newState(HE_DEAD_TRUTH_STATE)
                                        })
                                    }
                                    if (isArchonAlive()) {
                                        option(Assets.textResources.get("GIVE_UNIT"), {
                                            newState(GIVE_UNIT_STATE)
                                        })
                                    }
                                    if (canGiveAnotherUnit()) {
                                        option(Assets.textResources.get("GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE"), {
                                            newState(GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE_STATE)
                                        })
                                    }
                                    option(Assets.textResources.get("MY_NE_OTDADIM"), {
                                        newState(MY_NE_OTDADIM_STATE)
                                    })
                                    option(Assets.textResources.get("MY_EGO_NE_VIDELI"), {
                                        newState(MY_EGO_NE_VIDELI_STATE)
                                    })
                                    option(Assets.textResources.get("NICHEGO_NE_GOVORIT"), {
                                        newState(NICHEGO_NE_GOVORIT_STATE)
                                    })
                                }
                        ],

                (HE_DEAD_LIE_STATE)                     :
                        [
                                {
                                    HE_DEAD_LIE_STATE_CLOSURE()

                                }
                        ],

                (HE_DEAD_TRUTH_STATE)                   :
                        [
                                {
                                    HE_DEAD_TRUTH_STATE_CLOSURE()

                                }
                        ],

                (GIVE_UNIT_STATE)                       :
                        [
                                {
                                    GIVE_UNIT_STATE_CLOSURE()

                                }
                        ],

                (GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE_STATE):
                        [
                                {
                                    GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE_STATE_CLOSURE()

                                }
                        ],

                (MY_NE_OTDADIM_STATE)                   :
                        [
                                {
                                    MY_NE_OTDADIM_STATE_CLOSURE()

                                }
                        ],

                (MY_EGO_NE_VIDELI_STATE)                :
                        [
                                {
                                    MY_EGO_NE_VIDELI_STATE_CLOSURE()

                                }
                        ],

                (NICHEGO_NE_GOVORIT_STATE)              :
                        [
                                {
                                    NICHEGO_NE_GOVORIT_STATE_CLOSURE()

                                }
                        ],

        ]
    }

    // ===== CLOSURES:
    void UNTITLED_LAYER_STATE_CLOSURE() {
        return;
    }

    void START_STATE_CLOSURE() {
        focusOn(onCell)
        playerTeam.startQuest(IlluminatiCampaignQuest.class)
    }

    Boolean isArchonAlive() {
        return archon != null;
    }

    Boolean isArchonDead() {
        return archon == null;

    }

    Boolean canGiveAnotherUnit() {
        return playerTeam.squads.size > 1
    }

    void HE_DEAD_LIE_STATE_CLOSURE() {
        startWorldWar()
    }

    void startWorldWar() {
        text("War is the only way than!")
        IlluminatiCampaign.startWorldWar()
    }

    void HE_DEAD_TRUTH_STATE_CLOSURE() {
        text("Nothing interesting for us than!")
        IlluminatiCampaign.failed(playerTeam.world)
    }

    void GIVE_UNIT_STATE_CLOSURE() {
        text("Very right choice!")
        giveUnit(archon)
    }

    void giveUnit(AbstractSquad squad) {
        illumTeam.joinSquad(squad)
        squad.lastCell.addFloatLabel("Unit captured", Color.GREEN);
    }

    void GIVE_ANOTHER_UNIT_NOT_ARCHON_LIE_STATE_CLOSURE() {
        for (AbstractSquad squad : playerTeam.squads) {
            if (squad != archon && squad.isAlive()) {
                giveUnit(squad)
                break;
            }
        }
        text("You'd better not lie!")
        startWorldWar()
    }

    void MY_NE_OTDADIM_STATE_CLOSURE() {
        startWorldWar()

    }

    void MY_EGO_NE_VIDELI_STATE_CLOSURE() {
        startWorldWar()

    }

    void NICHEGO_NE_GOVORIT_STATE_CLOSURE() {
        text("Don't interfere us than!")
    }

}
