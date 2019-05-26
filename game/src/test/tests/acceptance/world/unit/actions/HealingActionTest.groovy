package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.objects.actions.items.HealingAction
import conversion7.game.stages.world.team.skills.items.StunningSkill
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.actions.ActionEvaluation
import conversion7.game.stages.world.unit.effects.items.HealActionCooldownEffect
import shared.BaseGdxgSpec

class HealingActionTest extends BaseGdxgSpec {

    void 'test couldHeal depends on unit level and gender'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        worldSteps.teamLearnsSkill(squad1.team, StunningSkill)

        when:
        worldSteps.setAgeLevel(squad1.unit, 0)
        squad1.unit.gender = true
        then:
        assert !squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 0)
        squad1.unit.gender = false
        then:
        assert !squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 1)
        squad1.unit.gender = true
        then:
        assert !squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 1)
        squad1.unit.gender = false
        then: 'female starts heal'
        assert squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 2)
        squad1.unit.gender = true
        then:
        assert !squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 2)
        squad1.unit.gender = false
        then:
        assert squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 3)
        squad1.unit.gender = true
        then:
        assert !squad1.unit.canHeal()

        when:
        worldSteps.setAgeLevel(squad1.unit, 3)
        squad1.unit.gender = false
        then:
        assert squad1.unit.canHeal()
    }

    void 'test perform healing'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        squad1.unit.gender = false
        worldSteps.setAgeLevel(squad1.unit, 1)
        squad1.actionsController.invalidate()
        squad1.validate()

        def squad2 = worldSteps.createUnit(
                squad1.team,
                worldSteps.getNextNeighborCell())
        squad2.unit.hurt(HealingAction.HEALTH_AMOUNT + 1)
        def health1 = squad2.unit.baseParams.get(UnitParameterType.HEALTH)

        when:
        def healingAction = squad1.actionsController.getAction(HealingAction)
        then:
        assert healingAction
        assert ActionEvaluation.HEALING.testOwner.evaluate(squad1.unit)
        assert healingAction.couldAcceptInput(squad2.lastCell)

        when:
        healingAction.handleAcceptedInput(squad2.lastCell)
        then:
        assert squad1.unit.actionPoints == ActionPoints.UNIT_START_ACTION_POINTS - ActionPoints.HEALING
        assert squad1.unit.effectManager.getEffect(HealActionCooldownEffect)
        assert !squad1.actionsController.getAction(HealingAction)
        and:
        assert squad2.unit.baseParams.get(UnitParameterType.HEALTH) == health1 + HealingAction.HEALTH_AMOUNT
    }

}
