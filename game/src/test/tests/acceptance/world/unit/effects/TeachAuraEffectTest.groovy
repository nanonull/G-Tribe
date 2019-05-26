package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.unit.effects.items.TeachAuraEffect
import shared.BaseGdxgSpec

class TeachAuraEffectTest extends BaseGdxgSpec {

    void 'test 1'() {
        given:
        lockCore()
        def team = worldSteps.createHumanTeam()
        def s1 = worldSteps.createUnit(team,
                worldSteps.getNextStandaloneCell())
        s1.unit.gender = false

        def s2 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())
        s2.lastCell.food = 0
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.setAgeLevel(s1.unit, TeachAuraEffect.STARTS_FROM_LEVEL)
        then:
        assert s1.unit.effectManager.containsEffect(TeachAuraEffect)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert s2.unit.experience == TeachAuraEffect.EXP_AMOUNT


        when:
        def preExp = s2.unit.experience
        worldSteps.setAgeLevel(s1.unit, TeachAuraEffect.EFFECT_DOUBLED_ON_LEVEL)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert s2.unit.experience == preExp + TeachAuraEffect.EXP_AMOUNT * 2
    }
}
