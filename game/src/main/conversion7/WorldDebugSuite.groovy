package conversion7

import com.badlogic.gdx.utils.Array
import conversion7.engine.ClientCore
import conversion7.engine.utils.MathUtils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.adventure.WorldAdventure
import conversion7.game.stages.world.inventory.items.AppleItem
import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.landscape.Biom
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.landscape.Landscape
import conversion7.game.stages.world.landscape.LandscapeController
import conversion7.game.stages.world.objects.buildings.CommunicationSatellite
import conversion7.game.stages.world.objects.buildings.IronFactory
import conversion7.game.stages.world.objects.buildings.SpaceShip
import conversion7.game.stages.world.objects.buildings.UranusFactory
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.objects.unit.WorldSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper

class WorldDebugSuite {


    private static ClientCore core

    static void runSuite1(ClientCore core) {
        WorldDebugSuite.core = core
        GdxgConstants.AREA_OBJECT_AI = false
        AreaViewerAnimationsHelper.showAnimation = true

        def playerTeam = core.world.lastActivePlayerTeam
        playerTeam.inventory.addItem(AppleItem, 100)
        playerTeam.inventory.addItem(ArrowItem, 100)
        playerTeam.inventory.addItem(BowItem, 5)

        def squad1 = playerTeam.squads.get(0)
//        UnitSkills.Skill find = UnitSkills.LEARNABLE_SKILLS.find { it.actionOrEffect == ActionEvaluation.SLEEP }
//        find.learn(squad1)
        def squad2 = playerTeam.squads.get(1)
//        squad2.learnedSkills.addAll(UnitSkills.LEARNABLE_SKILLS)
//        squad2.effectManager.getOrCreate(ScaredEffect)
//        AreaObject.create(squad1.getLastCell(), squad1.team, BallistaObject.class);
//        AreaObject.create(squad2.getLastCell(), squad1.team, ScorpionObject.class);
        playerTeam.squads.each { it.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX) }

        squad2.cell.area.createBaseSpawn(squad2.cell)


//        killOthers()
        core.world.animalSpawns.each { it.setSpawnEvery(9999) }

    }

    static void runSuite2_battleTest(ClientCore core) {
        WorldDebugSuite.core = core
        def world = core.world
        GdxgConstants.AREA_OBJECT_AI = true
        AreaViewerAnimationsHelper.showAnimation = true

        def playerTeam = world.lastActivePlayerTeam
        playerTeam.inventory.addItem(AppleItem, 100)
        playerTeam.inventory.addItem(ArrowItem, 100)
        playerTeam.inventory.addItem(BowItem, 5)

        def squad1 = playerTeam.squads.get(0)
//        UnitSkills.Skill find = UnitSkills.LEARNABLE_SKILLS.find { it.actionOrEffect == ActionEvaluation.SLEEP }
//        find.learn(squad1)
        def squad2 = playerTeam.squads.get(1)
//        squad2.learnedSkills.addAll(UnitSkills.LEARNABLE_SKILLS)
//        squad2.effectManager.getOrCreate(ScaredEffect)
//        AreaObject.create(squad1.getLastCell(), squad1.team, BallistaObject.class);
//        AreaObject.create(squad2.getLastCell(), squad1.team, ScorpionObject.class);
        playerTeam.squads.each { it.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX) }

        killOthers()
        world.animalSpawns.each { it.setSpawnEvery(9999) }

//        cleanLandscape(squad1.cell)
        WorldAdventure.placeAiTeam(squad1.cell.getCell(3, 0))
    }

    static void cleanLandscape(aroundCell) {
        Array<Cell> cells = new Array<>()
        aroundCell.getCellsAround(0, 12, cells)
        for (Cell cell : cells) {
            Landscape landscape = cell.getLandscape()
            landscape.setType(Landscape.Type.COMMON)

            if (landscape.getLevel() <= Landscape.WATER_LEVEL) {
                landscape.setLevel(MathUtils.testPercentChance(LandscapeController.HILL_CHANCE)
                        ? Landscape.HILL_LEVEL : 0)
            }

            if (cell.biomOrigin != null && cell.biomOrigin.type == Biom.Type.WATER) {
                cell.biomOrigin.type = Biom.Type.DIRT
            }
        }
    }


    static def killOthers() {

        core.world.teams.each {
            if (it.isHumanAiTribe() || it.isAnimals()) {
                def sqs = new Array(it.getSquads())
                for (int i = 0; i < sqs.size; i++) {
                    AbstractSquad sq = sqs.get(i)
                    if (!sq.isQuest()) {
                        WorldSquad.killUnit(sq)
                    }
                }
            }
        }

    }

    def createAllForMainQuest(Team playerTeam, Cell onCell) {
        def ironFactory = IronFactory.create(onCell, playerTeam, IronFactory)
        def uranusFactory = UranusFactory.create(onCell.getCell(-1, 0), playerTeam, UranusFactory)
        def spaceShip = SpaceShip.create(onCell.getCell(-2, 0), playerTeam, SpaceShip)
        def communicationSatellite = CommunicationSatellite.create(onCell.getCell(-3, 0), playerTeam, CommunicationSatellite)
    }
}
