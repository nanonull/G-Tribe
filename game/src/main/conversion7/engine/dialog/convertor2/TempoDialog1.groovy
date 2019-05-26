package conversion7.engine.dialog.convertor2

import conversion7.engine.dialog.AbstractDialog
import conversion7.game.Assets

class TempoDialog1 extends AbstractDialog {

    // STATES:
    static int ATAKOVAT_STATE = 1
    static int GOVORIT_STATE = 2

    /** DIALOG STATES DEFINITION MAP */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(INIT_STATE)    :
                        [
                                {
                                    disableSpeaker()
                                    text("You see ${classDescription()}.Relation: ${relationDesc()}")
                                    option(Assets.textResources.get("GOVORIT"), {
                                        newState(GOVORIT_STATE)
                                    })
                                    option(Assets.textResources.get("ATAKOVAT"), {
                                        newState(ATAKOVAT_STATE)
                                    })
                                }
                        ],

                (ATAKOVAT_STATE):
                        [
                                {
                                    attack()
                                }
                        ],

                (GOVORIT_STATE) :
                        [
                                {
                                    talk()
                                }
                        ],

        ]
    }
    // CLOSURES:
    static String relationDesc() {
        return;
    }

    static String attack() {
        return;
    }

    static String talk() {
        return;
    }

    static String classDescription() {
        return;
    }


}
