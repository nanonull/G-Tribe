package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.HealingAuraEffect
import shared.BaseGdxgSpec

class HealingAuraEffectTest extends BaseGdxgSpec {

    void 'test 1'() {
        given:
        def team = worldSteps.createHumanTeam()
        def female = worldSteps.createUnit(team,
                worldSteps.getNextStandaloneCell())
        female.unit.gender = false

        def squad2 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())
        squad2.unit.hurt(HealingAuraEffect.HEAL_AMOUNT * 2)

        when:
        worldSteps.setAgeLevel(female.unit, HealingAuraEffect.STARTS_FROM_LEVEL - 1)
        then:
        assert !female.unit.effectManager.containsEffect(HealingAuraEffect)

        when: 'has 2 levels'
        worldSteps.setAgeLevel(female.unit, HealingAuraEffect.STARTS_FROM_LEVEL)
        then:
        assert female.unit.effectManager.containsEffect(HealingAuraEffect)

        when:
        def preHp = squad2.unit.getTotalParam(UnitParameterType.HEALTH)
        worldSteps.rewindTeamsToStartNewWorldStep()
        then:
        assert squad2.unit.getTotalParam(UnitParameterType.HEALTH) == preHp + HealingAuraEffect.HEAL_AMOUNT

        when:
        worldSteps.setAgeLevel(female.unit, HealingAuraEffect.ENDS_FROM_LEVEL)
        then:
        assert !female.unit.effectManager.containsEffect(HealingAuraEffect)
    }
}
