package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.effects.items.TestIncreaseParamsEffect
import shared.BaseGdxgSpec

class TestIncreaseParamsEffectTest extends BaseGdxgSpec {

    public void 'test1'() {
        given:
        def team1 = worldSteps.createHumanTeam(true)
        def humanSquad1 = worldSteps.createUnit(team1,
                worldSteps.getNextStandaloneCell());
        Unit unit = humanSquad1.unit
        float inTotalPower = unit.getCurrentPower()
        def damage1 = unit.getMeleeDamage()

        when:
        unit.getEffectManager().addEffect(new TestIncreaseParamsEffect());

        then:
        assert unit.getCurrentPower() > inTotalPower
        assert unit.getMeleeDamage() > damage1
    }
}
