package conversion7.game.dialogs

import conversion7.engine.CameraController
import conversion7.engine.Gdxg
import conversion7.game.Assets
import conversion7.game.PictureAsset
import conversion7.game.stages.world.WorldPlayerSave
import conversion7.game.stages.world.WorldSettings
import conversion7.game.ui.utils.UiUtils

class WorldIntroDialog extends AbstractGdxgDialog {

    // ===== STATES:
    static int WORLD_INTRO_STATE = 0
    static int NEXT_STATE = 1

    private WorldSettings worldSettings


    WorldIntroDialog(WorldSettings worldSettings) {
        this.worldSettings = worldSettings
    }

    /** ===== DIALOG STATES DEFINITION MAP ===== */
    @Override
    protected Map<Object, List> getQuestStateMap() {
        return [(WORLD_INTRO_STATE):
                        [
                                {
                                    text(Assets.textResources.get("WORLD_INTRO_YOUR_ARE"))
                                    WORLD_INTRO_STATE_CLOSURE()

                                    option(Assets.textResources.get("NEXT"), {
                                        newState(NEXT_STATE)
                                    })

                                    if (WorldPlayerSave.getPlayerTeamProgress() > 0) {
                                        if (canBlessRes()) {
                                            option(Assets.textResources.get("WORLD_BLESSING_MORE_RESOURCES"), {
                                                WORLD_BLESSING_MORE_RESOURCES_STATE_CLOSURE()
                                            })
                                        }
                                        if (canCurseRes()) {
                                            option(Assets.textResources.get("WORLD_CURSE_LESS_RESOURCES"), {
                                                WORLD_CURSE_LESS_RESOURCES_STATE_CLOSURE()
                                            })
                                        }

                                        if (canBlessPower()) {
                                            option(Assets.textResources.get("WORLD_BLESSING_MORE_POWER_FOR_PLAYER_UNITS"), {
                                                WORLD_BLESSING_MORE_POWER_FOR_PLAYER_UNITS_STATE_CLOSURE()
                                            })
                                        }
                                        if (canCursePower()) {
                                            option(Assets.textResources.get("WORLD_CURSE_LESS_POWER_FOR_PLAYER_UNITS"), {
                                                WORLD_CURSE_LESS_POWER_FOR_PLAYER_UNITS_STATE_CLOSURE()
                                            })
                                        }

                                        if (canBlessAnimals()) {
                                            option(Assets.textResources.get("WORLD_BLESSING_LESS_ANIMALS"), {
                                                WORLD_BLESSING_LESS_ANIMALS_STATE_CLOSURE()
                                            })
                                        }
                                        if (canCurseAnimals()) {
                                            option(Assets.textResources.get("WORLD_CURSE_MORE_ANIMALS"), {
                                                WORLD_CURSE_MORE_ANIMALS_STATE_CLOSURE()
                                            })
                                        }

                                        if (canBlessPlace()) {
                                            option(Assets.textResources.get("WORLD_BLESSING_BETTER_START_PLACE"), {
                                                WORLD_BLESSING_BETTER_START_PLACE_CLOSURE()
                                            })
                                        }
                                        if (canCursePlace()) {
                                            option(Assets.textResources.get("WORLD_CURSE_WORSE_START_PLACE"), {
                                                WORLD_CURSE_WORSE_START_PLACE_CLOSURE()
                                            })
                                        }
                                    }
                                }
                        ],

                (NEXT_STATE)       :
                        [
                                {
                                    text(Assets.textResources.get("WORLD_INTRO_CREATE_RACE_AND_GO"))
                                    NEXT_STATE_CLOSURE()

                                }
                        ],

        ]
    }


// ===== CLOSURES:
    void WORLD_INTRO_STATE_CLOSURE() {
        setPicture(Assets.getPicture(PictureAsset.SHIP_CRASH))
    }

    boolean canCursePlace() {
        worldSettings.placeBalance != -1
    }

    boolean canBlessPlace() {
        worldSettings.placeBalance != 1
    }

    Boolean canBlessRes() {
        return worldSettings.resBalance != 1
    }

    Boolean canBlessPower() {
        return worldSettings.powerBalance != 1

    }

    Boolean canBlessAnimals() {
        return worldSettings.animalsBalance != 1

    }

    Boolean canCurseRes() {
        return worldSettings.resBalance != -1

    }

    Boolean canCursePower() {
        return worldSettings.powerBalance != -1

    }

    Boolean canCurseAnimals() {
        return worldSettings.animalsBalance != -1

    }

    def WORLD_BLESSING_BETTER_START_PLACE_CLOSURE() {
        worldSettings.placeBalance = 1
        text("World Blessing Better Start Place")
    }

    def WORLD_CURSE_WORSE_START_PLACE_CLOSURE() {
        worldSettings.placeBalance = -1
        text("World Curse Worse Start Place")
    }


    void WORLD_BLESSING_MORE_RESOURCES_STATE_CLOSURE() {
        worldSettings.resBalance = 1
        text("World Blessing More Resources")
    }

    void WORLD_BLESSING_MORE_POWER_FOR_PLAYER_UNITS_STATE_CLOSURE() {
        worldSettings.powerBalance = 1
        text("World Blessing More Power")
    }

    void WORLD_BLESSING_LESS_ANIMALS_STATE_CLOSURE() {
        worldSettings.animalsBalance = 1
        text("World Blessing Less Animals")
    }

    void WORLD_CURSE_LESS_RESOURCES_STATE_CLOSURE() {
        worldSettings.resBalance = -1
        text("World Curse Less Resources")
    }

    void WORLD_CURSE_LESS_POWER_FOR_PLAYER_UNITS_STATE_CLOSURE() {
        worldSettings.powerBalance = -1
        text("World Curse Less Power")
    }

    void WORLD_CURSE_MORE_ANIMALS_STATE_CLOSURE() {
        worldSettings.animalsBalance = -1
        text("World Curse More Animals")
    }

    void NEXT_STATE_CLOSURE() {

        def core = Gdxg.core
        core.createNewWorld(worldSettings)

        // focus in quest
        setPictureViewActive(false)
        CameraController.scheduleCameraFocusOnPlayerSquad()

        core.activateStage(Gdxg.getAreaViewer())

        text("")
        def playerTeam = core.world.lastActivePlayerTeam
        text("World difficulty: " + UiUtils.getNumberWithSign(
                worldSettings.totalBalance() * -1))
        if (playerTeam.evolutionPoints > 0) {
            text("Starting Evolution points: " + playerTeam.evolutionPoints)
        }
        if (playerTeam.playerUnitProgress > 0) {
            text("Bonus starting Experience for one unit of your tribe: " + playerTeam.playerUnitProgress)
        }
    }

}


