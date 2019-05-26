package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.IncreaseBattleParamsEffect
import shared.BaseGdxgSpec

class IncreaseBattleParamsEffectTest extends BaseGdxgSpec {

    void testIncreaseBattleParametersEffect() {
        given:
        def squad1 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        squad1.unit.getEffectManager().addEffect(new IncreaseBattleParamsEffect())
        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())

        squad1.unit.baseParams.update(UnitParameterType.STRENGTH, 50)
        int inStr = squad1.unit.getTotalParam(UnitParameterType.STRENGTH)
        int inAgi = squad1.unit.getTotalParam(UnitParameterType.AGILITY)
        int inVit = squad1.unit.getTotalParam(UnitParameterType.VITALITY)
        int inDefence = squad1.unit.getDefence()
        int inMeleeDamage = squad1.unit.getMeleeDamage()

        when: "battle started"
        squad1.unit.switchBattleEffects(true)
        then:
        assert squad1.unit.getTotalParam(UnitParameterType.STRENGTH) ==
                ((int) (inStr * IncreaseBattleParamsEffect.PARAMS_MULTIPLIER))
        assert (squad1.unit.getTotalParam(UnitParameterType.AGILITY)) ==
                ((int) (inAgi * IncreaseBattleParamsEffect.PARAMS_MULTIPLIER))
        assert (squad1.unit.getTotalParam(UnitParameterType.VITALITY)) ==
                ((int) (inVit * IncreaseBattleParamsEffect.PARAMS_MULTIPLIER))
        assert (squad1.unit.getDefence()) > (inDefence)
        assert (squad1.unit.getMeleeDamage()) > (inMeleeDamage)

        when: "battle finished - effect removed"
        squad1.unit.switchBattleEffects(false)
        squad1.unit.executeMeleeAttack(squad2)

        then:
        assert squad1.unit.getTotalParam(UnitParameterType.STRENGTH) == inStr
        assert squad1.unit.getTotalParam(UnitParameterType.AGILITY) == inAgi
        assert squad1.unit.getTotalParam(UnitParameterType.VITALITY) == inVit
        assert squad1.unit.getDefence() == (inDefence)
        assert squad1.unit.getMeleeDamage() == (inMeleeDamage)
    }


}
