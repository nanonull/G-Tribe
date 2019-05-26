package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.objects.actions.items.CriticalDamageChanceBoostAction
import conversion7.game.stages.world.unit.effects.items.CriticalDamageChanceBoostEffect
import shared.BaseGdxgSpec

class CriticalDamageChanceBoostActionTest extends BaseGdxgSpec {

    void 'test boost another unit'() {
        given:
        def team1 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        assert !squad1.actionsController.getAction(CriticalDamageChanceBoostAction)

        when: 'unit around'
        def squad2 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)

        then:
        assert squad1.actionsController.getAction(CriticalDamageChanceBoostAction)

        when: "apply action"
        squad1.actionsController.getAction(CriticalDamageChanceBoostAction).handleAcceptedInput(squad2.lastCell)
        def criticalDamageChanceBoostEffect2 = squad2.unit.effectManager.getEffect(CriticalDamageChanceBoostEffect)
        then: "has effect"
        assert criticalDamageChanceBoostEffect2
        assert criticalDamageChanceBoostEffect2.chanceBoostPercent == CriticalDamageChanceBoostEffect.PERCENT_BOOST_1
        assert criticalDamageChanceBoostEffect2.expiresIn == CriticalDamageChanceBoostEffect.EXPIRES_IN_1
//        assert squad2.unit.battleHelper.getCriticalDamageChancePercent() == CriticalDamageChanceBoostEffect.PERCENT_BOOST_1

        when:
        criticalDamageChanceBoostEffect2.chanceBoostPercent = 100
        def damageData = squad2.battleHelper.calcDamage(true, squad1.unit)
        then:
        assert damageData.damage == squad2.unit.getMeleeDamage() * 2
    }

}
