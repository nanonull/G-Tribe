package tests.acceptance.world.unit.effects

import conversion7.engine.utils.MathUtils
import conversion7.game.stages.world.unit.effects.items.CommanderAuraEffect
import shared.BaseGdxgSpec

class CommanderAuraEffectTest extends BaseGdxgSpec {

    void 'test 1'() {
        given:
        def team = worldSteps.createHumanTeam()
        def male1 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())
        male1.unit.gender = true

        def squad2 = worldSteps.createUnit(team,
                worldSteps.getNextNeighborCell())

        when:
        worldSteps.setAgeLevel(male1.unit, CommanderAuraEffect.STARTS_FROM - 1)
        then:
        assert !male1.unit.effectManager.containsEffect(CommanderAuraEffect)

        when: 'has level'
        lockCore()
        worldSteps.setAgeLevel(male1.unit, CommanderAuraEffect.STARTS_FROM)

        then:
        assert male1.unit.effectManager.containsEffect(CommanderAuraEffect)

        when:
        def preExp = squad2.unit.experience
        def expectExp = preExp + 10 + MathUtils.getPercentValue(CommanderAuraEffect.EXP_ADD_PERCENT, 10)
        squad2.unit.updateExperience(10)

        then:
        assert squad2.unit.experience == expectExp
    }
}
