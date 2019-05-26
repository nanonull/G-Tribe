package tests.acceptance.world.unit.actions

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.objects.actions.items.ControlUnitAction
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.ControlUnitsEffect
import conversion7.game.stages.world.unit.effects.items.UnderControlEffect
import org.mockito.ArgumentCaptor
import shared.BaseGdxgSpec
import spock.lang.Ignore

import static org.mockito.Mockito.*

class ControlUnitActionTest extends BaseGdxgSpec {

    def unitCaptor = ArgumentCaptor.forClass(Unit)
    def booleanCaptor = ArgumentCaptor.forClass(Boolean)

    @Ignore
    void 'test depends on'() {
    }

    void 'test perform it'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)

        def squad2 = worldSteps.createUnit(
                worldSteps.createAnimalTeam(),
                worldSteps.getNextNeighborCell())
        assert squad1.unit.isEnemyWith(squad2.unit)
        worldSteps.makeUnitWeak(squad2.unit)

        when:
        def action = squad1.actionsController.getAction(ControlUnitAction)
        then:
        assert action
        assert action.actionEvaluation.evaluateOwner(squad1.unit)

        when:
        squad2.unit.getBaseParams().put(UnitParameterType.STRENGTH, 9999)
        def animalPowerModified = squad2.unit.baseMaxPower * ControlUnitAction.TARGET_POWER_CHECK_MOD
        assert squad1.unit.baseMaxPower < animalPowerModified
        then: 'cant apply effect on strong animal'
        assert !action.couldAcceptInput(squad2.lastCell)

        when:
        squad2.unit.getBaseParams().put(UnitParameterType.STRENGTH, 1)
        squad1.unit.getBaseParams().put(UnitParameterType.STRENGTH, 9999)
        then:
        assert action.couldAcceptInput(squad2.lastCell)

        when:
        action.handleAcceptedInput(squad2.lastCell)
        def controlAnimalsEffect = squad1.unit.effectManager.getEffect(ControlUnitsEffect)
        def underControlEffect = squad2.unit.getEffectManager().getEffect(UnderControlEffect)

        then:
        assert squad1.unit.actionPoints == ActionPoints.UNIT_START_ACTION_POINTS - action.actionPoints
        assert controlAnimalsEffect
        assert controlAnimalsEffect.underControl.size() == 1
        assert controlAnimalsEffect.underControl[0] == squad2.unit
        and:
        assert ControlUnitAction.doesControl(squad1.unit, squad2.unit)
        assert !action.couldAcceptInput(squad2.lastCell)
        and:
        assert underControlEffect
        assert underControlEffect.controller == squad1.unit
        and:
        assert !squad1.unit.isEnemyWith(squad2.unit)
    }

    void 'test animal under control AI - moves and attack enemy'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad1.unit)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.getNextNeighborCell())
        squad2animal.unit.baseParams.update(UnitParameterType.STRENGTH, 1)
        squad2animal.unit = spy(squad2animal.unit)

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        assert controlAnimalAction.couldAcceptInput(squad2animal.lastCell)

        def cell3 = worldSteps.nextNeighborCell
        def cell4 = worldSteps.nextNeighborCell
        def cell5enemy = worldSteps.nextNeighborCell

        when:
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        squad1.moveOn(cell4)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert squad1.lastCell == cell4
        and: 'animal moves 1 cell to controller'
        assert squad2animal.lastCell == cell3

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then: 'animal waits'
        assert squad2animal.lastCell == cell3
        and: 'not attack controller'
        assert !squad1.unit.isEnemyWith(squad2animal.unit)

        when: 'enemy of player team created'
        def squad3enemy = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell5enemy)
        squad3enemy.setAiEnabled(false)
        worldSteps.makeUnitWeaker(squad3enemy.unit, squad2animal.unit)
        worldSteps.makeEnemies(squad1, squad3enemy)
        assert squad1.unit.isEnemyWith(squad3enemy.unit)
        assert squad2animal.unit.isEnemyWith(squad3enemy.unit)
        and:
        def unitCaptor = ArgumentCaptor.forClass(Unit)
        def booleanCaptor = ArgumentCaptor.forClass(Boolean)
        worldSteps.rewindTeamsToStartNewWorldStep()
        def cellNearCell4 = cell4.getCell(0, -1)
        then: 'animal moves to enemy'
        assert squad2animal.lastCell == cellNearCell4
        and: 'animal hits enemy'
        verify(squad2animal.unit, times(1))
                .hit(unitCaptor.capture(), booleanCaptor.capture())
        assert booleanCaptor.getAllValues().size() == 1
        assert booleanCaptor.getAllValues().last()
        assert unitCaptor.getAllValues().size() == 1
        assert unitCaptor.getAllValues().last() == squad3enemy.unit
        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: 'animal the final/prev cell'
        assert squad2animal.lastCell == cellNearCell4
        and: 'animal hits enemy 2nd time'
        verify(squad2animal.unit, times(2))
                .hit(unitCaptor.capture(), booleanCaptor.capture())
        assert booleanCaptor.getAllValues().last()
        assert unitCaptor.getAllValues().last() == squad3enemy.unit
    }

    void 'test animal under control AI - moves and attack enemy animal'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad1.unit)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.getNextNeighborCell())
        squad2animal.unit = spy(squad2animal.unit)

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        assert controlAnimalAction.couldAcceptInput(squad2animal.lastCell)

        def cell3 = worldSteps.nextNeighborCell
        def cell4 = worldSteps.nextNeighborCell
        def cell5enemy = worldSteps.nextNeighborCell

        when:
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        squad1.moveOn(cell4)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert squad1.lastCell == cell4
        and: 'animal moves 1 cell to controller'
        assert squad2animal.lastCell == cell3

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then: 'animal waits'
        assert squad2animal.lastCell == cell3
        and: 'not attack controller'
        assert !squad1.unit.isEnemyWith(squad2animal.unit)

        when: 'enemy animal is created'
        def squad3enemyAnimal = worldSteps.createUnit(
                animalTeam,
                cell5enemy)
        worldSteps.makeUnitWeaker(squad3enemyAnimal.unit, squad2animal.unit)
        squad3enemyAnimal.setAiEnabled(false)
        assert squad1.unit.isEnemyWith(squad3enemyAnimal.unit)
        assert squad2animal.unit.isEnemyWith(squad3enemyAnimal.unit)
        worldSteps.rewindTeamsToStartNewWorldStep()
        then: 'animal moves to enemy'
        assert squad2animal.lastCell == cell4.getCell(0, -1)
        and: 'animal hits enemy'
        verify(squad2animal.unit, times(1))
                .hit(unitCaptor.capture(), booleanCaptor.capture())
        assert booleanCaptor.getAllValues().size() == 1
        assert booleanCaptor.getAllValues().last()
        assert unitCaptor.getAllValues().size() == 1
        assert unitCaptor.getAllValues().last() == squad3enemyAnimal.unit
        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: 'animal the final/prev cell'
        assert squad2animal.lastCell == cell4.getCell(0, -1)
        and: 'animal hits enemy 2nd time'
        verify(squad2animal.unit, times(2))
                .hit(unitCaptor.capture(), booleanCaptor.capture())
        assert booleanCaptor.getAllValues().last()
        assert unitCaptor.getAllValues().last() == squad3enemyAnimal.unit
    }

    void 'test animal under control AI - doesnt attack my ally'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        def playerTeam = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createUnit(
                playerTeam,
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad1.unit)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.getNextNeighborCell())

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        assert controlAnimalAction.couldAcceptInput(squad2animal.lastCell)

        def cell3nextMove = worldSteps.nextNeighborCell
        def cell4 = worldSteps.nextNeighborCell
        def cell5myAlly = worldSteps.nextNeighborCell

        when:
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        squad1.moveOn(cell4)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert squad1.lastCell == cell4
        and: 'animal moves 1 cell to controller'
        assert squad2animal.lastCell == cell3nextMove

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then: 'animal waits'
        assert squad2animal.lastCell == cell3nextMove
        and: 'not attack controller'
        assert !squad1.unit.isEnemyWith(squad2animal.unit)

        when: 'my ally is created'
        def squad3 = worldSteps.createUnit(
                playerTeam,
                cell5myAlly)
        then:
        assert !squad2animal.unit.isEnemyWith(squad3.unit)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then: 'animal doesnt move'
        assert squad2animal.lastCell == cell3nextMove
    }

    void 'test animal under control is removed from controller when dies'() {
        given:
        def playerTeam = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createUnit(
                playerTeam,
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.getNextNeighborCell())
        worldSteps.makeUnitWeak(squad2animal.unit)

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        assert controlAnimalAction.couldAcceptInput(squad2animal.lastCell)

        when:
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        then:
        assert !squad1.unit.isEnemyWith(squad2animal.unit)
        assert squad1.unit.effectManager.getEffect(ControlUnitsEffect)
        assert squad2animal.unit.effectManager.getEffect(UnderControlEffect)

        when:
        worldSteps.defeatKillUnit(squad2animal.unit)
        then:
        assert !squad1.unit.effectManager.getEffect(ControlUnitsEffect)
        assert !squad2animal.unit.effectManager.getEffect(UnderControlEffect)
    }

    void 'test animal under control is free if controller dies'() {
        given:
        def playerTeam = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createUnit(
                playerTeam,
                worldSteps.getNextNeighborCell())
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.getNextNeighborCell())
        worldSteps.makeUnitWeak(squad2animal.unit)

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        assert controlAnimalAction.couldAcceptInput(squad2animal.lastCell)

        when:
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        then:
        assert !squad1.unit.isEnemyWith(squad2animal.unit)
        assert squad1.unit.effectManager.getEffect(ControlUnitsEffect)
        assert squad2animal.unit.effectManager.getEffect(UnderControlEffect)

        when:
        worldSteps.defeatKillUnit(squad1.unit)
        then:
        assert !squad1.unit.effectManager.getEffect(ControlUnitsEffect)
        assert !squad2animal.unit.effectManager.getEffect(UnderControlEffect)
    }

    void 'test animal attacks controlled animal'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        def playerTeam = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createUnit(
                playerTeam,
                worldSteps.nextNeighborCell)
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)

        def animalTeam = worldSteps.createAnimalTeam()
        def squad2animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextStandaloneCell)
        worldSteps.makeUnitWeak(squad2animal.unit)

        def squad3animal = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextNeighborCell)
        squad3animal.unit = spy(squad3animal.unit)

        def controlAnimalAction = squad1.actionsController.getAction(ControlUnitAction)
        controlAnimalAction.handleAcceptedInput(squad2animal.lastCell)
        assert squad3animal.unit.isEnemyWith(squad2animal.unit)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then:
        verify(squad3animal.unit, times(1))
                .hit(unitCaptor.capture(), booleanCaptor.capture())
        assert unitCaptor.getAllValues().size() == 1
        assert unitCaptor.getAllValues().last() == squad2animal.unit

    }

    void 'test two controlled animals by one controller'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = false

        def team1 = worldSteps.createHumanTeam()
        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad1.unit)

        def animalTeam = worldSteps.createAnimalTeam()
        def squadAnimal1 = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextNeighborCell)
        worldSteps.makeUnitWeak(squadAnimal1.unit)
        def squadAnimal2 = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextNeighborCell)
        worldSteps.makeUnitWeak(squadAnimal2.unit)

        assert squad1.unit.isEnemyWith(squadAnimal1.unit)
        assert squad1.unit.isEnemyWith(squadAnimal2.unit)
        assert !squadAnimal1.unit.isEnemyWith(squadAnimal2.unit)

        when:
        ControlUnitAction.takeControl(squad1.unit, squadAnimal1.unit)
        squad1.actionsController.getAction(ControlUnitAction).handleAcceptedInput(squadAnimal2.lastCell)
        then:
        assert squad1.unit.effectManager.getEffect(ControlUnitsEffect).underControl.collect({
            it.id
        }).sort() ==
                [squadAnimal1.unit, squadAnimal2.unit].collect({ it.id }).sort()
        assert !squad1.unit.isEnemyWith(squadAnimal1.unit)
        assert !squad1.unit.isEnemyWith(squadAnimal2.unit)
        assert !squadAnimal1.unit.isEnemyWith(squadAnimal2.unit)
        assert !squadAnimal2.unit.isEnemyWith(squadAnimal1.unit)

        when: 'AI runs no inf. loop in is-enemy check'
        GdxgConstants.AREA_OBJECT_AI = true
        worldSteps.rewindTeamsToStartNewWorldStep()
        then:
        assert !squad1.unit.isEnemyWith(squadAnimal1.unit)
        assert !squad1.unit.isEnemyWith(squadAnimal2.unit)
        assert !squadAnimal1.unit.isEnemyWith(squadAnimal2.unit)

        when:
        worldSteps.defeatKillUnit(squadAnimal1.unit)
        then:
        assert squad1.unit.getEffectManager().getEffect(ControlUnitsEffect).underControl.size == 1
        assert squad1.unit.getEffectManager().getEffect(ControlUnitsEffect).underControl.first() == squadAnimal2.unit

        when:
        worldSteps.defeatKillUnit(squadAnimal2.unit)
        then:
        assert !squad1.unit.getEffectManager().getEffect(ControlUnitsEffect)
    }

    void 'test two controlled animals by two enemy teams'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()
        def animalTeam = worldSteps.createAnimalTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        squad1.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad1.unit)

        def squad2 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
        squad2.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
        worldSteps.makeUnitStrong(squad2.unit)

        def squadAnimal1 = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextNeighborCell)
        worldSteps.makeUnitWeak(squadAnimal1.unit)
        squadAnimal1.unit = spy(squadAnimal1.unit)
        def squadAnimal2 = worldSteps.createUnit(
                animalTeam,
                worldSteps.nextNeighborCell)
        worldSteps.makeUnitWeak(squadAnimal2.unit)
        squadAnimal2.unit = spy(squadAnimal2.unit)

        and: 'units are no enemies from start'
        assert !squad1.unit.isEnemyWith(squad2.unit)
        squad1.actionsController.getAction(ControlUnitAction).handleAcceptedInput(squadAnimal1.lastCell)
        squad2.actionsController.getAction(ControlUnitAction).handleAcceptedInput(squadAnimal2.lastCell)
        assert !squad1.unit.isEnemyWith(squadAnimal2.unit)
        assert !squad2.unit.isEnemyWith(squadAnimal1.unit)

        when: 'controlled animals attacks enemies of my team'
        worldSteps.makeEnemies(squad1, squad2)
        then:
        assert squad1.unit.isEnemyWith(squad2.unit)
        assert squad1.unit.isEnemyWith(squadAnimal2.unit)
        assert squad2.unit.isEnemyWith(squadAnimal1.unit)
        assert squadAnimal1.unit.isEnemyWith(squadAnimal2.unit)
    }

}
