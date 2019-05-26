package tests.debug

import conversion7.engine.CameraController
import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.services.WorldServices
import conversion7.game.stages.world.adventure.WorldAdventure
import conversion7.game.stages.world.elements.SoulType
import conversion7.game.stages.world.inventory.InventoryItemStaticParams
import conversion7.game.stages.world.inventory.TeamCraftInventory
import conversion7.game.stages.world.inventory.items.*
import conversion7.game.stages.world.inventory.items.weapons.BowItem
import conversion7.game.stages.world.inventory.items.weapons.JavelinItem
import conversion7.game.stages.world.inventory.items.weapons.SpearItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.AreaObject
import conversion7.game.stages.world.objects.MountainDebris
import conversion7.game.stages.world.objects.ScorpionObject
import conversion7.game.stages.world.objects.actions.items.SelectHeroClassAction
import conversion7.game.stages.world.objects.buildings.CommunicationSatellite
import conversion7.game.stages.world.objects.buildings.IronFactory
import conversion7.game.stages.world.objects.buildings.SpaceShip
import conversion7.game.stages.world.objects.buildings.UranusFactory
import conversion7.game.stages.world.objects.composite.SandWorm
import conversion7.game.stages.world.objects.totem.DefenceTotem
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.team.skills.items.FlayingSkill
import conversion7.game.stages.world.team.skills.items.HealingSkill
import conversion7.game.stages.world.team.skills.items.WitcherHeroSkill
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.DisableHealingEffect
import conversion7.game.stages.world.unit.effects.items.PanicEffect
import conversion7.game.stages.world.unit.hero_classes.HeroClass
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper
import conversion7.game.unit_classes.animals.oligocene.Pyrotherium
import shared.BaseGdxgSpec

// beaten by gradle 2.4
@Deprecated
class WorldDebugSuiteOld extends BaseGdxgSpec {

    def setup() {
        core.clientUi.getTestBar().show()
    }


    void 'main test 1'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = false
        AreaViewerAnimationsHelper.showAnimation = true

        lockCore()
        def playerTeam = Gdxg.core.world.lastActivePlayerTeam

        def playerMaleUnit1Cell = worldSteps.getNextNeighborCell()
//        def totem = new AttackTotem(playerMaleUnit1Cell.getCell(1, 1), playerTeam)
        def totem2 = new DefenceTotem(playerMaleUnit1Cell.getCell(2, 1), playerTeam)
        def scorpionObject = AreaObject.create(playerMaleUnit1Cell.getCell(1, 1), playerTeam, ScorpionObject)

        WorldServices.nextUnitGender = true
        def playerMaleUnit1 = worldSteps.createUnit(playerTeam,
                playerMaleUnit1Cell, WorldServices.HUMAN_FIRST_CLASS)
        playerMaleUnit1.setName("playerMaleUnit1")
        playerMaleUnit1.getMainParams().update(UnitParameterType.VITALITY, 101)
        playerMaleUnit1.getMainParams().updateHealthToVitality()
        playerMaleUnit1.updateExperience(Unit.BASE_EXP_FOR_LEVEL)
        playerMaleUnit1.updateExperience(Unit.BASE_EXP_FOR_LEVEL * 2 - 5)
//        playerMaleUnit1.unit.updateExperience(Unit.BASE_EXP_FOR_LEVEL * 3)
//        playerMaleUnit1.unit.updateExperience(Unit.BASE_EXP_FOR_LEVEL * 4)
        worldSteps.setAgeLevel(playerMaleUnit1.unit, 1)
        playerMaleUnit1.equipment.equipByType(new SpearItem().setQuantity(1))
        playerMaleUnit1.equipment.equipByType(new SkinItem().setQuantity(1))

        WorldServices.nextUnitGender = true
        def playerMaleUnit2 = worldSteps.createUnit(playerTeam,
                playerMaleUnit1Cell.getCell(1, 0), WorldServices.HUMAN_FIRST_CLASS)
        playerMaleUnit2.setName("playerMaleUnit2")
        playerMaleUnit2.updateExperience(Unit.BASE_EXP_FOR_LEVEL)
        worldSteps.setAgeLevel(playerMaleUnit2.unit, 1)
        playerMaleUnit2.effectManager.getOrCreate(PanicEffect)

        WorldServices.nextUnitGender = true
        def playerMaleUnit3 = worldSteps.createUnit(playerTeam,
                playerMaleUnit2.getLastCell().getCell(1, 0), WorldServices.HUMAN_FIRST_CLASS)
        playerMaleUnit3.setName("playerMaleUnit3")
        playerMaleUnit3.updateExperience(Unit.BASE_EXP_FOR_LEVEL - 1)
        worldSteps.setAgeLevel(playerMaleUnit3.unit, 1)

        WorldServices.nextUnitGender = false
        def playerMaleUnit4 = worldSteps.createUnit(playerTeam,
                playerMaleUnit3.getLastCell().getCell(1, 0), WorldServices.HUMAN_FIRST_CLASS)
        playerMaleUnit4.setName("playerMaleUnit4")
        SelectHeroClassAction.makeHero(playerMaleUnit4, HeroClass.DRUID)
        worldSteps.setAgeLevel(playerMaleUnit4.unit, 1)


        def ufo1Cell = playerMaleUnit1Cell.getCell(-1, 0)
        def playerUfo = WorldAdventure.placePlayerUfo(team, ufo1Cell)
        worldSteps.teamLearnsSkill(playerTeam, FlayingSkill)
        worldSteps.teamLearnsSkill(playerTeam, HealingSkill)
        worldSteps.teamLearnsSkill(playerTeam, WitcherHeroSkill)

        playerMaleUnit1Cell.getInventory().addItem(StoneItem, 1)
        playerMaleUnit1.inventory.addItem(AppleItem, 10)
        playerMaleUnit1.soul.type = SoulType.DAY
        playerMaleUnit1.effectManager.addEffect(new DisableHealingEffect())

        createAllForMainQuest(playerTeam, ufo1Cell.getCell(-3, 0))

        // PLAYER MISC
        //        ufo1Cell.resourcesGenerator.generateIsotop()
        playerTeam.inventory.addItem(RadioactiveIsotopeItem.class, 3)
        playerTeam.inventory.addItem(BowItem.class, 3)
        //        worldSteps.addItemsToBuild(playerUfo.squad.team, CommunicationSatellite.class)
        playerTeam.updateEvolutionPointsOn(+1000, null)
        playerTeam.teamSkillsManager.getSkill(SkillType.FIRE).learn()
        playerTeam.teamSkillsManager.getSkill(SkillType.BUILD_CAMP).learn()
        playerTeam.inventory.addItem(StickItem.class, 3)
        playerTeam.inventory.addItem(StoneItem.class, 3)
        playerTeam.inventory.addItem(StringItem.class, 3)
        playerTeam.inventory.addItem(JavelinItem.class, 3)
        playerTeam.inventory.addItem(ArrowItem.class, 100)
        playerTeam.inventory.addItem(SkinRobeItem.class, 3)
        playerTeam.inventory.addItem(SkinItem.class, 3)
        worldSteps.makeTeamCouldUseRecipes(playerMaleUnit1.getTeam(), TeamCraftInventory.RECIPES_LIST)
        worldSteps.addItemsNeededForCraft(playerMaleUnit1, InventoryItemStaticParams.ARROW.craftRecipe)
//        worldSteps.addItemsNeededForCraft(playerMaleUnit1, TeamCraftInventory.RECIPES_LIST)

        // team 3
//        def team3StartCell = playerMaleUnit1Cell.getCell(2, 2)
//
//        def team3 = worldSteps.createHumanTeam(false)
//        team3.inventory.addItem(ArrowItem, 5)
//        team3.addRelation(TribeRelationType.JUST_NICE, playerTeam)
//        team3.teamSkillsManager.getSkill(SkillType.BRAIN).learn()
//
//        def t3u1 = worldSteps.createUnit(team3,
//                team3StartCell, WorldServices.HUMAN_FIRST_CLASS)
//        t3u1.setName("t3u1")
//        SelectHeroClassAction.makeHero(t3u1)
//        t3u1.equipment.equipRangeWeaponItem((RangeWeaponItem) new BowItem().setQuantity(1))

        //
        //
        //
        //
        def animalTeam = worldSteps.createAnimalTeam()
        def animalCell = playerMaleUnit1Cell.getCell(2, -2)
        def animalUnit = worldSteps.createUnit(animalTeam,
                animalCell, Pyrotherium)
//        animalUnit.power.updateMaxValue(-39)

        //
        //
        //
        def wormCell = playerMaleUnit1Cell.getCell(2, 4)
//        def parts = [SandWorm.createPart(wormCell, animalTeam),
//                     SandWorm.createPart(wormCell, animalTeam),
//                     SandWorm.createPart(wormCell, animalTeam)]
//        Array<AreaObject> partsA = new Array<>()
//        partsA.addAll(parts.toArray())
//        def sandWorm = SandWorm.create(SandWorm, partsA)
        def sandWorm = SandWorm.create(animalTeam)
        sandWorm.placeAt(wormCell)

        MountainDebris.create(wormCell.getCell(0, 1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-1, 1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-2, 1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-3, 1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-4, 0), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(1, 0), animalTeam, MountainDebris)

        MountainDebris.create(wormCell.getCell(0, -1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-1, -1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-2, -1), animalTeam, MountainDebris)
        MountainDebris.create(wormCell.getCell(-3, -1), animalTeam, MountainDebris)


        releaseCoreAndWaitNextCoreStep()
        CameraController.scheduleCameraFocusOnPlayerSquad()


        worldSteps.revalidateUnits()
        releaseCoreAndWaitNextCoreStep()
        Gdxg.core.world.totalViewReload2()
        sleep()

        Utils.infinitySleepThread()
    }

    def createAllForMainQuest(Team playerTeam, Cell onCell) {

        def ironFactory = IronFactory.create(onCell, playerTeam, IronFactory)
        def uranusFactory = UranusFactory.create(onCell.getCell(-1, 0), playerTeam, UranusFactory)
        def spaceShip = SpaceShip.create(onCell.getCell(-2, 0), playerTeam, SpaceShip)
        def communicationSatellite = CommunicationSatellite.create(onCell.getCell(-3, 0), playerTeam, CommunicationSatellite)
    }
}
