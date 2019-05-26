package tests.acceptance.world.unit.actions

import shared.BaseGdxgSpec
import spock.lang.Ignore

@Ignore
class PostFertilizationMaleEffectTest extends BaseGdxgSpec {

//    void 'test 1'() {
//        given:
//        lockCore()
//        def team1 = worldSteps.createHumanTeam()
//
//        WorldServices.nextUnitGender = true
//        def squad1 = worldSteps.createUnit(
//                team1,
//                worldSteps.nextNeighborCell)
//        worldSteps.setAgeLevel(squad1.unit, FertilizeAction.AGE_FROM)
//
//        WorldServices.nextUnitGender = false
//        def squad2 = worldSteps.createUnit(
//                team1,
//                worldSteps.nextNeighborCell)
//        worldSteps.setAgeLevel(squad2.unit, FertilizeAction.AGE_FROM)
//        assert squad1.actionsController.getAction(FertilizeAction)
//        releaseCoreAndWaitNextCoreStep()
//
//        when: "apply action"
//        UnitFertilizer.overrideNextFertilizationChance = 100
//        UnitFertilizer.ignoreConditionsOnNextFertilization = true
//        squad1.actionsController.getAction(FertilizeAction).handleAcceptedInput(squad2.cell)
//        def postFertilizationMaleEffect = squad1.unit.getEffectManager().getEffect(PostFertilizationMaleEffect)
//
//        then: "has effect"
//        assert squad2.unit.getEffectManager().containsEffect(ChildbearingEffect)
//        assert postFertilizationMaleEffect
//        and: 'effect value for unit and ally'
//        assert squad1.unit.battleHelper.getCriticalDamageChancePercent() == postFertilizationMaleEffect.critBoostPercent
//        assert squad2.unit.battleHelper.getCriticalDamageChancePercent() == postFertilizationMaleEffect.critBoostPercent
//
//    }

}
