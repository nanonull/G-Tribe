package tests.acceptance.world.unit

import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.UnitFertilizer
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect
import shared.BaseGdxgSpec

class FertilizeTest extends BaseGdxgSpec {

    void 'test fertilize'() {
        given:
        Team team = worldSteps.createHumanTeam()
        def cell = worldSteps.getNextNeighborCell()
        AbstractSquad squad1 = worldSteps.createUnit(team, cell)
        AbstractSquad squad2 = worldSteps.createUnit(team, worldSteps.nextNeighborCell)
        squad2.unit.gender = false

        when:
        UnitFertilizer.completeFertilization(squad1.unit, squad2.unit)
        assert squad2.unit.effectManager.containsEffect(ChildbearingEffect)
        def childbearingEffect = squad2.unit.effectManager.getEffect(ChildbearingEffect)
        println childbearingEffect.child.getMainParams

        then:
        assert childbearingEffect.child.getMainParams.get(UnitParameterType.STRENGTH) > 0
        assert childbearingEffect.child.getMainParams.get(UnitParameterType.AGILITY) > 0
        assert childbearingEffect.child.getMainParams.get(UnitParameterType.VITALITY) > 0
    }

    void 'test fertilization age'() {
        given:
        Team team = worldSteps.createHumanTeam()
        AbstractSquad squad1 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())
        squad1.unit.gender = true
        AbstractSquad squad2 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())
        squad2.unit.gender = false

        when: 'young'
        worldSteps.setAgeLevel(squad1.unit, 0)
        worldSteps.setAgeLevel(squad2.unit, 0)
        then:
        assert !squad1.unit.canFertilize()
        assert !squad2.unit.canBeFertilized()

        when: 'adult'
        worldSteps.setAgeLevel(squad1.unit, 1)
        worldSteps.setAgeLevel(squad2.unit, 1)
        then:
        assert squad1.unit.canFertilize()
        assert squad2.unit.canBeFertilized()

        when: 'mature'
        worldSteps.setAgeLevel(squad1.unit, 2)
        worldSteps.setAgeLevel(squad2.unit, 2)
        then:
        assert squad1.unit.canFertilize()
        assert squad2.unit.canBeFertilized()

        when: 'old'
        worldSteps.setAgeLevel(squad1.unit, 3)
        worldSteps.setAgeLevel(squad2.unit, 3)
        then:
        assert !squad1.unit.canFertilize()
        assert !squad2.unit.canBeFertilized()
    }

}
