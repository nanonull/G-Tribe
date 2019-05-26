package tests.acceptance.world.unit.effects

import conversion7.engine.utils.MathUtils
import conversion7.game.stages.world.unit.UnitAge
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.MaturityAuraEffect
import org.mockito.Mockito
import shared.BaseGdxgSpec

class MaturityAuraEffectTest extends BaseGdxgSpec {

    void 'test 1'() {
        given:

        lockCore()
        def squad1 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.nextNeighborCell)
        releaseCoreAndWaitNextCoreStep()

        lockCore()
        squad1.unit = Mockito.spy(squad1.unit)
        Mockito.doReturn(0).when(squad1.unit).getArmor()
        releaseCoreAndWaitNextCoreStep()

        lockCore()
        squad1.unit.gender = true
        squad1.unit.baseParams.update(UnitParameterType.VITALITY, 999)
        squad1.unit.baseParams.updateHealthToVitality()

        def squad2 = worldSteps.createUnit(squad1.team,
                worldSteps.nextNeighborCell)
        squad2.unit.baseParams.update(UnitParameterType.STRENGTH, 500)
//        squad2.battleHelper = new BattleHelper(squad2) {
////            @Override
////            void applyCritChance(DamageData damageData) {
////                println 'mock'
////            }
//        }
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.setAgeLevel(squad1.unit, MaturityAuraEffect.STARTS_FROM_LEVEL)
        int dmgPenalty = MathUtils.getPercentValue(MaturityAuraEffect.DAMAGE_PENALTY_PERCENT,
                squad2.unit.getMeleeDamage())
        then:
        assert squad1.unit.age == UnitAge.ADULT
        assert squad1.unit.effectManager.getEffect(MaturityAuraEffect)
        and: 'neighbor affected'
        assert squad2.battleHelper.calcDamage(true, squad1.unit).damage == squad2.unit.getMeleeDamage() - dmgPenalty
        assert squad2.battleHelper.getCriticalDamageChancePercent() == MaturityAuraEffect.CRIT_BOOST_PERCENT

        when:
        worldSteps.setAgeLevel(squad1.unit, MaturityAuraEffect.ENDS_FROM_LEVEL - 1)
        then:
        assert squad1.unit.effectManager.containsEffect(MaturityAuraEffect)

        when:
        worldSteps.setAgeLevel(squad1.unit, MaturityAuraEffect.ENDS_FROM_LEVEL)
        then:
        assert !squad1.unit.effectManager.containsEffect(MaturityAuraEffect)
        assert squad2.battleHelper.calcDamage(true, squad1.unit).damage == squad2.unit.getMeleeDamage()
        assert squad2.battleHelper.getCriticalDamageChancePercent() == 0
    }
}
