package conversion7.game.dialogs

import com.badlogic.gdx.utils.ObjectSet
import conversion7.engine.CameraController
import conversion7.game.Assets
import conversion7.game.stages.world.World
import conversion7.game.stages.world.WorldRelations
import conversion7.game.stages.world.inventory.items.AppleItem
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.TribeRelationType

import java.util.function.Consumer

class DialogWithUnit extends AbstractGdxgDialog {
    // ===== STATES:
    static int START_STATE = 0
    static int ATTACK_STATE = 1
    static int ASK_TO_JOIN_STATE = 2
    static int EXIT_STATE = 3
    static int ASK_TO_ATTACK_MY_ENEMY_STATE = 4
    static int ASK_ABOUT_ALLY_STATE = 5
    static int ASK_DO_THEY_NEED_HELP_STATE = 6
    static int TO_DIALOG_MENU_STATE = 7
    static int NEXT_TIME_STATE = 8
    static int SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL_STATE = 9
    static int JUST_GIFT_FOOD_ITEM_STATE = 10
    static int ACCEPT_HELP_REQUEST_STATE = 11
    static int DO_NOT_ACCEPT_STATE = 12
    static int YES_STATE = 13

    private AbstractSquad mySquad
    private AbstractSquad talkToSquad
    World world

    DialogWithUnit(AbstractSquad mySquad, AbstractSquad talkToSquad) {
        this.talkToSquad = talkToSquad
        this.mySquad = mySquad
        world = talkToSquad.team.world
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(START_STATE)                                     :
                        [
                                {
                                    START_STATE_CLOSURE()

                                    option(Assets.textResources.get("NEXT_TIME"), {
                                        newState(NEXT_TIME_STATE)
                                    })
                                    if (canPayForTalk()) {
                                        option(Assets.textResources.get("YES_GIVE_THEM_FOOD_TO_START_DIALOG"), {
                                            newState(YES_STATE)
                                        })
                                    }
                                    if (canJUST_GIFT_FOOD_ITEM_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("JUST_GIFT_FOOD_ITEM"), {
                                            newState(JUST_GIFT_FOOD_ITEM_STATE)
                                        })
                                    }
                                    if (canAskAboutPositionSwap()) {
                                        option(Assets.textResources.get("SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL"), {
                                            newState(SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL_STATE)
                                        })
                                    }
                                }
                        ],

                (ATTACK_STATE)                                    :
                        [
                                {
                                    ATTACK_STATE_CLOSURE()

                                }
                        ],

                (ASK_TO_JOIN_STATE)                               :
                        [
                                {
                                    text("  ${tryToJoin()} ")
                                    ASK_TO_JOIN_STATE_CLOSURE()

                                }
                        ],

                (EXIT_STATE)                                      :
                        [
                                {
                                    EXIT_STATE_CLOSURE()

                                }
                        ],

                (ASK_TO_ATTACK_MY_ENEMY_STATE)                    :
                        [
                                {
                                    ASK_TO_ATTACK_MY_ENEMY_STATE_CLOSURE()

                                }
                        ],

                (ASK_ABOUT_ALLY_STATE)                            :
                        [
                                {
                                    ASK_ABOUT_ALLY_STATE_CLOSURE()

                                }
                        ],

                (ASK_DO_THEY_NEED_HELP_STATE)                     :
                        [
                                {
                                    ASK_DO_THEY_NEED_HELP_STATE_CLOSURE()

                                    if (canACCEPT_HELP_REQUEST_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("ACCEPT_HELP_REQUEST"), {
                                            newState(ACCEPT_HELP_REQUEST_STATE)
                                        })
                                    }
                                    if (canDO_NOT_ACCEPT_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("DO_NOT_ACCEPT_OR_BACK"), {
                                            newState(DO_NOT_ACCEPT_STATE)
                                        })
                                    }
                                }
                        ],

                (TO_DIALOG_MENU_STATE)                            :
                        [
                                {
                                    TO_DIALOG_MENU_STATE_CLOSURE()

                                    if (canAskAboutJoin()) {
                                        option(Assets.textResources.get("ASK_TO_JOIN"), {
                                            newState(ASK_TO_JOIN_STATE)
                                        })
                                    }
                                    if (canAskAboutAlly()) {
                                        option(Assets.textResources.get("ASK_ABOUT_ALLY"), {
                                            newState(ASK_ABOUT_ALLY_STATE)
                                        })
                                    }
                                    if (canAttack()) {
                                        option(Assets.textResources.get("ATTACK"), {
                                            newState(ATTACK_STATE)
                                        })
                                    }
                                    if (canAskAboutTheyNeedHelp()) {
                                        option(Assets.textResources.get("ASK_DO_THEY_NEED_HELP"), {
                                            newState(ASK_DO_THEY_NEED_HELP_STATE)
                                        })
                                    }
                                    if (canHelpWithWar()) {
                                        option(Assets.textResources.get("ASK_TO_ATTACK_MY_ENEMY"), {
                                            newState(ASK_TO_ATTACK_MY_ENEMY_STATE)
                                        })
                                    }
                                    option(Assets.textResources.get("EXIT"), {
                                        newState(EXIT_STATE)
                                    })
                                }
                        ],

                (NEXT_TIME_STATE)                                 :
                        [
                                {
                                    NEXT_TIME_STATE_CLOSURE()

                                }
                        ],

                (SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL_STATE):
                        [
                                {
                                    SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL_STATE_CLOSURE()

                                    if (canTO_DIALOG_MENU_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("TO_DIALOG_MENU"), {
                                            newState(TO_DIALOG_MENU_STATE)
                                        })
                                    }
                                }
                        ],

                (JUST_GIFT_FOOD_ITEM_STATE)                       :
                        [
                                {
                                    JUST_GIFT_FOOD_ITEM_STATE_CLOSURE()

                                }
                        ],

                (ACCEPT_HELP_REQUEST_STATE)                       :
                        [
                                {
                                    ACCEPT_HELP_REQUEST_STATE_CLOSURE()

                                }
                        ],

                (DO_NOT_ACCEPT_STATE)                             :
                        [
                                {
                                    DO_NOT_ACCEPT_STATE_CLOSURE()

                                    if (canTO_DIALOG_MENU_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("TO_DIALOG_MENU"), {
                                            newState(TO_DIALOG_MENU_STATE)
                                        })
                                    }
                                }
                        ],

                (YES_STATE)                                       :
                        [
                                {
                                    YES_STATE_CLOSURE()

                                    if (canTO_DIALOG_MENU_STATE_CLOSURE()) {
                                        option(Assets.textResources.get("TO_DIALOG_MENU"), {
                                            newState(TO_DIALOG_MENU_STATE)
                                        })
                                    }
                                }
                        ],

        ]
    }

    def ACCEPT_HELP_REQUEST_STATE_CLOSURE() {
        Collections.shuffle(allyTargets)
        def targ = allyTargets.get(0)

        talkToSquad.team.setGoalObject(targ)
        CameraController.scheduleCameraFocusOn(0, targ.cell)
        text("Unit describes you target and its location")

        targ.addDeathListener(new Consumer<AreaObject>() {
            @Override
            void accept(AreaObject object) {
                mySquad.team.completesGoalFor(talkToSquad.team, object.getLastCell())
            }
        })
    }

    def DO_NOT_ACCEPT_STATE_CLOSURE() {
    }

    def TO_DIALOG_MENU_STATE_CLOSURE() {
    }

    boolean canACCEPT_HELP_REQUEST_STATE_CLOSURE() {
        def allyCanTlk = talkToSquad.hasBrainToTalk()
        if (!allyCanTlk) {
            text("Ally can't explain their problems (Bad talk skill)")
            return false
        }

        calcAllyTargets()
        if (allyTargets.isEmpty()) {
            text("Ally has no problems")
            return false
        }
        return true
    }

    List<AbstractSquad> allyTargets = []

    void calcAllyTargets() {
        allyTargets.clear()

        def visibleCells = talkToSquad.team.calcVisibleCells(new ObjectSet<Cell>())
        for (Cell cell : visibleCells) {
            if (allyTargets.size() >= 5) {
                break
            }
            if (cell.hasSquad() && talkToSquad.canAttack(cell) &&
                    cell.squad.team.isEnemyOf(talkToSquad.team)) {
                allyTargets.add(cell.squad)
            }
        }
    }

    boolean canDO_NOT_ACCEPT_STATE_CLOSURE() {
        true
    }

    boolean canTO_DIALOG_MENU_STATE_CLOSURE() {
        true
    }

    def JUST_GIFT_FOOD_ITEM_STATE_CLOSURE() {
        talkToSquad.team.inventory.addItem(AppleItem, 1)
        mySquad.team.inventory.remove(AppleItem, 1)
        world.addRelationType(TribeRelationType.GIFT, talkToSquad.team, mySquad.team)
        text(talkToSquad.getFullName() + " takes it with pleasure")
        text("Relation +" + TribeRelationType.GIFT.relationValue)
    }

    boolean canJUST_GIFT_FOOD_ITEM_STATE_CLOSURE() {
        return canPayForTalk()
    }

    static boolean hasApple(Team team) {
        team.inventory.getItemQty(AppleItem) > 0
    }

    boolean canAskAboutAlly() {
        def relationBalance = mySquad.team.getRelationBalance(talkToSquad.team)
        if (relationBalance > WorldRelations.NEUTRAL_RELATION_MID &&
                mySquad.hasBrainToTalk() &&
                mySquad.team.canAskAboutAllyAtWorldStep() &&
                talkToSquad.team.canBeAlly() &&
                !talkToSquad.team.isAllyOf(mySquad.team)) {
            return true;
        }
        return false;
    }

// ===== CLOSURES:
    @Deprecated
    Boolean canAttack() {
        false
//        return ActionEvaluation.MELEE_ATTACK.testMeVsTargetFull(mySquad, talkToSquad.cell)
    }

    Boolean canAskAboutJoin() {
        return mySquad.hasBrainToTalk() &&
                mySquad.team.canAskToJoinAtWorldStep() &&
                mySquad.canJoin(talkToSquad)
    }

    Boolean canHelpWithWar() {
        return false;
    }

    String tryToJoin() {
        def joined = mySquad.team.tryToJoin(talkToSquad)
        def resp = "Join chance: ${Team.getJoinChance(mySquad.team, talkToSquad.team)}%\n"
        if (joined) {
            return resp + "Unit has joined your tribe"
        } else {
            return resp + "Unit has rejected your proposal"
        }
    }

    void ASK_TO_JOIN_STATE_CLOSURE() {
    }

    String getOtherTeamName() {
        return talkToSquad.team.getName();
    }

    void START_STATE_CLOSURE() {
        text(talkToSquad.name + " from ${getOtherTeamName()} team looks on you.")
        if (mySquad.hasBrainToTalk()) {
            if (talkToSquad.hasBrainToTalk()) {
                text("You can have a talk (Both units have skill)")
            } else {
                text("${talkToSquad.name} has no skill to talk")
            }
        } else {
            text("You can't talk yet (Requires skill)")
        }

        if (shouldPayForTalk()) {
            text("Dialog initiation costs 1 food item")
            if (canPayForTalk()) {
                text("Will you pay?")
            } else {
                text("You have not enough resources to talk")
            }
        }
    }

    boolean shouldPayForTalk() {
        return !mySquad.team.isAllyOf(talkToSquad.team)
    }

    Boolean canAskAboutPositionSwap() {
        if (mySquad.team == talkToSquad.team && mySquad.hasBrainToTalk()) {
            if (mySquad.canMove() && talkToSquad.canMove()) {
                return true
            }
        };
        return false
    }


    void ATTACK_STATE_CLOSURE() {
        mySquad.attackByBestWeapon(talkToSquad);
        complete()
    }

    void ASK_ABOUT_ALLY_STATE_CLOSURE() {
        if (mySquad.team.tryToAlly(talkToSquad.team)) {
            text(mySquad.team.name + " is your ally now")
        } else {
            def allyChance = mySquad.team.getAllyChance(talkToSquad.team)
            text(mySquad.team.name + " rejects ally request with ${allyChance}% chance")
        }

    }

    void EXIT_STATE_CLOSURE() {
        complete()
    }

    void SWAP_OUR_POSITIONS_CAN_YOU_MOVE_ON_MY_CELL_STATE_CLOSURE() {
        def cell1 = mySquad.lastCell
        def cell2 = talkToSquad.lastCell
        mySquad.freeCell()

        talkToSquad.moveOn(cell1)
        mySquad.moveOn(cell1, cell2)

        complete()
        CameraController.scheduleCameraFocusOn(50, mySquad.getLastCell())
    }

    void ASK_TO_ATTACK_MY_ENEMY_STATE_CLOSURE() {
        text("TODO")
    }

    void ASK_DO_THEY_NEED_HELP_STATE_CLOSURE() {
        calcAllyTargets()
        text("Ally has ${allyTargets.size()} problems")
    }

    Boolean canPayForTalk() {
        return shouldPayForTalk() && hasApple(mySquad.team);
    }

    void YES_STATE_CLOSURE() {
        mySquad.team.inventory.remove(AppleItem.class, 1)
        talkToSquad.team.inventory.addItem(AppleItem.class, 1)
        text(talkToSquad.getFullName() + " listens to you")
    }

    Boolean canAskAboutTheyNeedHelp() {
        calcAllyTargets()
        return allyTargets.size() > 0 && talkToSquad.team.goalObject == null;
    }

    void NEXT_TIME_STATE_CLOSURE() {
        complete()
    }

}

