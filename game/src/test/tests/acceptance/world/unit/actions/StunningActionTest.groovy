package tests.acceptance.world.unit.actions

import conversion7.engine.Gdxg
import conversion7.game.stages.world.objects.actions.items.MeleeAttackAction
import conversion7.game.stages.world.objects.actions.items.MoveAction
import conversion7.game.stages.world.objects.actions.items.RangeAttackAction
import conversion7.game.stages.world.objects.actions.items.StunningAction
import conversion7.game.stages.world.team.skills.items.StunningSkill
import conversion7.game.stages.world.unit.actions.ActionEvaluation
import conversion7.game.stages.world.unit.effects.items.StunActionCooldownEffect
import conversion7.game.stages.world.unit.effects.items.StunnedEffect
import shared.BaseGdxgSpec

class StunningActionTest extends BaseGdxgSpec {

    void 'test couldStun depends on unit level and gender'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        worldSteps.teamLearnsSkill(squad1.team, StunningSkill)

        when:
        worldSteps.setAgeLevel(squad1.unit, 0)
        squad1.unit.gender = true
        then:
        assert !squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 0)
        squad1.unit.gender = false
        then:
        assert !squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 1)
        squad1.unit.gender = true
        then: 'male can stun'
        assert squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 1)
        squad1.unit.gender = false
        then:
        assert !squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 2)
        squad1.unit.gender = true
        then:
        assert squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 2)
        squad1.unit.gender = false
        then: 'female can stun'
        assert squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 3)
        squad1.unit.gender = true
        then:
        assert squad1.unit.canStun()

        when:
        worldSteps.setAgeLevel(squad1.unit, 3)
        squad1.unit.gender = false
        then:
        assert squad1.unit.canStun()
    }

    void 'test perform stun'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        squad1.unit.gender = true
        worldSteps.setAgeLevel(squad1.unit, 1)
        worldSteps.teamLearnsSkill(squad1.team, StunningSkill)

        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())

        Gdxg.core.world.setRelation(-1, squad1.team, squad2.team)

        when:
        def stunningAction = squad1.actionsController.getAction(StunningAction)
        then:
        assert stunningAction
        assert ActionEvaluation.STUNNING.testOwner.evaluate(squad1.unit)
        assert stunningAction.couldAcceptInput(squad2.lastCell)

        when:
        stunningAction.handleAcceptedInput(squad2.lastCell)
        then:
        assert squad1.unit.actionPoints == ActionPoints.UNIT_START_ACTION_POINTS - ActionPoints.STUNNING
        assert squad1.unit.effectManager.getEffect(StunActionCooldownEffect)
        assert !squad1.actionsController.getAction(StunningAction)

        and:
        assert squad2.unit.getEffectManager().containsEffect(StunnedEffect)
        assert !squad2.actionsController.getAction(MoveAction)
        assert !squad2.actionsController.getAction(MeleeAttackAction)
        assert !squad2.actionsController.getAction(RangeAttackAction)
    }

}
