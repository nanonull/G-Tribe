package tests.debug

import conversion7.engine.CameraController
import conversion7.engine.Gdxg
import conversion7.engine.artemis.engine.time.SchedulingSystem
import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.services.WorldServices
import conversion7.game.stages.world.inventory.TeamCraftInventory
import conversion7.game.stages.world.inventory.items.StoneItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitFertilizer2
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect
import conversion7.game.stages.world.unit.effects.items.IncreaseBattleParamsEffect
import conversion7.game.stages.world.unit.effects.items.SelfHealingEffect
import org.mockito.internal.util.reflection.Whitebox

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class Debug2 {

    void 'test memory leaks in world'() {
        given:
        def team1 = worldSteps.createHumanTeam(true)
        WorldServices.nextUnitGender = true
        def sq1 = worldSteps.createUnit(team1,
                worldSteps.getNextNeighborCell())

        WorldServices.nextUnitGender = false
        def sq2 = worldSteps.createUnit(team1,
                worldSteps.getNextNeighborCell())
        Unit unit = sq2.unit
        unit.getEffectManager().addEffect(new IncreaseBattleParamsEffect())
        unit.getEffectManager().addEffect(new SelfHealingEffect())

        UnitFertilizer2.completeFertilization(sq1.unit, sq2.unit)
        def childbearingEffect = sq2.unit.effectManager.getEffect(ChildbearingEffect)
        assert childbearingEffect
        Whitebox.setInternalState(childbearingEffect, "tickCounter", ChildbearingEffect.PREGNANCY_DURATION - 1)

        def sq3 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())

        List<WeakReference> refs = []
        List strRefs = []
        while (true) {
            setup()

            def world = Gdxg.core.world
            WeakReference newRef1 = buildWeakReference(world)
            WeakReference newRef2 = buildWeakReference(world.getCell(0, 0))
            def soil = world.getCell(0, 0).landscape.terrainVertexData.soil
            strRefs.add(soil)
            WeakReference newRef3 = buildWeakReference(soil)

            CameraController.scheduleCameraFocusOnPlayerSquad()
            Utils.sleepThread(1000)

            println "Test references:"
            for (WeakReference reference : refs) {
                println "$reference"
                assert reference.isEnqueued()
            }
            refs.add(newRef1)
            refs.add(newRef2)
            refs.add(newRef3)
        }

        Utils.infinitySleepThread()
    }


    static WeakReference buildWeakReference(Object o) {
        ReferenceQueue queue = new ReferenceQueue()
        return new WeakReference(o, queue)
    }

    //
    //
    //
    //
    void 'main test2'() {

        given:
        GdxgConstants.AREA_OBJECT_AI = true

        lockCore()
        def playerTeam = worldSteps.createHumanTeam(true)
        playerTeam.updateEvolutionPointsOn(+1000, null)

        def cell1 = worldSteps.getNextNeighborCell()
        def playerUnit = worldSteps.createUnit(playerTeam,
                cell1)

        def animalUnit = worldSteps.createUnit(worldSteps.createAnimalTeam(),
                worldSteps.getNextStandaloneCellsInRow(5).last())

        releaseCore()

        CameraController.scheduleCameraFocusOnPlayerSquad()
        sleepThread()
    }

    void 'test one unit'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        lockCore()
        def playerTeam = worldSteps.createHumanTeam(true)
        playerTeam.updateEvolutionPointsOn(+1000, null)
        WorldServices.nextUnitGender = true
        def squad1 = worldSteps.createUnit(playerTeam,
                worldSteps.getNextNeighborCell())
        worldSteps.makeTeamCouldUseRecipes(squad1.getTeam(), TeamCraftInventory.RECIPES_LIST)
        worldSteps.addItemsNeededForCraft(squad1, TeamCraftInventory.RECIPES_LIST)
        squad1.lastCell.getInventory().addItem(new StoneItem())
        releaseCoreAndWaitNextCoreStep()
        CameraController.scheduleCameraFocusOnPlayerSquad()
        Utils.infinitySleepThread()
    }


    void 'test 2 INFINITY SIMULATION'() {
        given:
        GdxgConstants.DEVELOPER_MODE = true
        GdxgConstants.AREA_OBJECT_AI = true

        SchedulingSystem.schedule("test 2 INFINITY SIMULATION: kill player", 450, {
            def armies = Gdxg.core.world.lastActivePlayerTeam.squads.toList()
            for (AbstractSquad squad : armies) {
                worldSteps.defeatKillUnit(squad.unit)
            }
        })

        CameraController.scheduleCameraFocusOnPlayerSquad()
        Utils.infinitySleepThread()
    }

    void 'test 3 human vs animal'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        lockCore()
        def playerTeam = worldSteps.createHumanTeam(true)
        playerTeam.updateEvolutionPointsOn(+1000, null)
        WorldServices.nextUnitGender = true
        def squad1 = worldSteps.createUnit(playerTeam,
                worldSteps.getNextNeighborCell())
        worldSteps.makeTeamCouldUseRecipes(squad1.getTeam(), TeamCraftInventory.RECIPES_LIST)
        worldSteps.addItemsNeededForCraft(squad1, TeamCraftInventory.RECIPES_LIST)

        def cell10 = squad1.lastCell.getCell(0, 7)
        def animalUnit = worldSteps.createUnit(worldSteps.createAnimalTeam(), cell10)
        releaseCore()

        CameraController.scheduleCameraFocusOnPlayerSquad()
        Utils.infinitySleepThread()
    }


    void test_InfinityWorldSimulation() {
        given:
        while (true) {
            worldSteps.rewindTeamsToStartNewWorldStep()
            Utils.sleepThread(1000)
        }
    }

}
