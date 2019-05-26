package conversion7.game.dialogs

import com.badlogic.gdx.utils.Array
import conversion7.game.Assets
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.buildings.SpaceShip
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.quest.items.SendSosQuest
import conversion7.game.stages.world.team.Team

class DeliverSatelliteToOrbitDialog extends AbstractGdxgDialog {

    // ===== STATES:
    static int DELIVER_SATELLITE_TO_ORBIT_STATE = 0
    static int CHECK_SPACESHIP_STATUS_STATE = 1
    static int CHECK_SATELLITE_STATUS_STATE = 2
    static int CHECK_CREW_STATUS_STATE = 3
    static int WE_NEED_PREPARE_BETTER_STATE = 4
    static int START_MISSION_STATE = 5
    private AbstractSquad archonSquad
    private SpaceShip spaceShip
    boolean shipReady
    boolean satelliteReady
    boolean crewReady
    public static final int URAN_QTY_REQ = 10;

    DeliverSatelliteToOrbitDialog(AbstractSquad archonSquad, SpaceShip spaceShip) {
        this.spaceShip = spaceShip
        this.archonSquad = archonSquad
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(DELIVER_SATELLITE_TO_ORBIT_STATE):
                        [
                                {
                                    text(" Make sure you are ready for mission.")
                                    DELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE()

                                    if (canCHECK_SPACESHIP_STATUS_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("CHECK_SPACESHIP_STATUS"), {
                                            newState(CHECK_SPACESHIP_STATUS_STATE)
                                        })
                                    }
                                    if (canCHECK_SATELLITE_STATUS_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("CHECK_SATELLITE_STATUS"), {
                                            newState(CHECK_SATELLITE_STATUS_STATE)
                                        })
                                    }
                                    if (canCHECK_CREW_STATUS_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("CHECK_CREW_STATUS"), {
                                            newState(CHECK_CREW_STATUS_STATE)
                                        })
                                    }
                                    if (canWE_NEED_PREPARE_BETTER_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("WE_NEED_PREPARE_BETTER"), {
                                            newState(WE_NEED_PREPARE_BETTER_STATE)
                                        })
                                    }
                                    if (allSystemsReady()) {
                                        option(Assets.textResources.get("START_MISSION"), {
                                            newState(START_MISSION_STATE)
                                        })
                                    }
                                }
                        ],

                (CHECK_SPACESHIP_STATUS_STATE)    :
                        [
                                {
                                    CHECK_SPACESHIP_STATUS_STATE_CLOSURE()

                                    if (canDELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("DELIVER_SATELLITE_TO_ORBIT"), {
                                            newState(DELIVER_SATELLITE_TO_ORBIT_STATE)
                                        })
                                    }
                                }
                        ],

                (CHECK_SATELLITE_STATUS_STATE)    :
                        [
                                {
                                    CHECK_SATELLITE_STATUS_STATE_CLOSURE()

                                    if (canDELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("DELIVER_SATELLITE_TO_ORBIT"), {
                                            newState(DELIVER_SATELLITE_TO_ORBIT_STATE)
                                        })
                                    }
                                }
                        ],

                (CHECK_CREW_STATUS_STATE)         :
                        [
                                {
                                    CHECK_CREW_STATUS_STATE_CLOSURE()

                                    if (canDELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("DELIVER_SATELLITE_TO_ORBIT"), {
                                            newState(DELIVER_SATELLITE_TO_ORBIT_STATE)
                                        })
                                    }
                                }
                        ],

                (WE_NEED_PREPARE_BETTER_STATE)    :
                        [
                                {
                                    WE_NEED_PREPARE_BETTER_STATE_CLOSURE()

                                }
                        ],

                (START_MISSION_STATE)             :
                        [
                                {
                                    text("SOS was sent! To be continued...")
                                    START_MISSION_STATE_CLOSURE()

                                }
                        ],

        ]
    }

    boolean canDELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE() {
        true
    }

    boolean canWE_NEED_PREPARE_BETTER_STATE_CLOSURE() {
        true
    }

    boolean canCHECK_CREW_STATUS_STATE_CLOSURE() {
        !crewReady
    }

    boolean canCHECK_SATELLITE_STATUS_STATE_CLOSURE() {
        !satelliteReady
    }
// ===== CLOSURES:
    void DELIVER_SATELLITE_TO_ORBIT_STATE_CLOSURE() {
        // menu
    }

    Boolean allSystemsReady() {
        return shipReady && satelliteReady && crewReady
    }

    boolean canCHECK_SPACESHIP_STATUS_STATE_CLOSURE() {
        !shipReady
    }

    void CHECK_SPACESHIP_STATUS_STATE_CLOSURE() {
        shipReady = hasEnoughUran(archonSquad.team)
        if (shipReady) {
            text("Ship ready")
        } else {
            text("Not ready: requires ${URAN_QTY_REQ} ${RadioactiveIsotopeItem.class.simpleName} ")
        }
    }

    public static boolean hasEnoughUran(Team team) {
        return team.inventory.getItemQty(RadioactiveIsotopeItem.class) >= URAN_QTY_REQ

    }

    void CHECK_SATELLITE_STATUS_STATE_CLOSURE() {
        satelliteReady = archonSquad.team.hasSatellite()
        if (satelliteReady) {
            text("Satellite ready")
        } else {
            text("Not ready: Satellite built is required")
        }
    }

    void CHECK_CREW_STATUS_STATE_CLOSURE() {
        Array<AbstractSquad> squads = new Array<>()
        for (Cell cell : spaceShip.cell.cellsAround) {
            if (cell.hasSquad() && cell.squad.isAlive() && cell.squad.team == archonSquad.team) {
                squads.add(cell.squad)
            }
        }

        text("Units around space ship: ${squads.size}")
        squads.each { squad -> text("LVL ${squad.expLevelUi} ${squad.fullName} ") }
        crewReady = true
    }

    void WE_NEED_PREPARE_BETTER_STATE_CLOSURE() {
        complete()
    }

    void START_MISSION_STATE_CLOSURE() {
        archonSquad.team.world.lastActivePlayerTeam.journal.getOrCreate(SendSosQuest.class).complete(SendSosQuest.State.S5);
    }


}
