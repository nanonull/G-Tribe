package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.InjuryEffect
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

import static org.fest.assertions.api.Assertions.assertThat

class InjuryEffectTest extends BaseGdxgSpec {

    // till new battle design
    @Ignore
    public void test_unitParamsAreDecreasedByInjury() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit1 = humanSquad1.unit
        worldSteps.makeUnitInvincible(unit1);

        def humanSquad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit2 = humanSquad2.unit

        when:
        Integer inStr = unit2.getBaseParams().get(UnitParameterType.STRENGTH);
        battleSteps.setResurrectUnitInBattleIfResistFailed(true);
        battleSteps.startBattle(humanSquad1, humanSquad2, false);

        then:
        WorldAsserts.assertUnitAlive(unit2);
        assertThat(unit2.getBaseParams().get(UnitParameterType.HEALTH)).isEqualTo(Unit.HEALTH_AFTER_BACK_TO_LIFE);
        WorldAsserts.assertUnitHasEffect(unit2, InjuryEffect.class);
        assertThat(unit2.getTotalParam(UnitParameterType.STRENGTH)).isLessThan(inStr);
    }

    public void test_removeInjuryWhenHealsAreFull() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit = humanSquad1.unit
        worldSteps.makePerfectConditionsOnCell(humanSquad1.getLastCell());

        unit.getEffectManager().addEffect(new InjuryEffect());

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        WorldAsserts.assertUnitHasFullHealth(unit);
        WorldAsserts.assertUnitHasNoEffect(unit, InjuryEffect.class);
    }

}
