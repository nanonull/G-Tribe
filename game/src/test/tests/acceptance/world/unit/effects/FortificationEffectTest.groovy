package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.objects.actions.items.FortifyAction
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.FortificationEffect
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.powermock.api.mockito.PowerMockito.spy

class FortificationEffectTest extends BaseGdxgSpec {

    public void 'test Activate FortifyEffects For Defender'() {
        given:
        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        squad1.unit = spy(squad1.unit)
        Unit unit1 = squad1.unit

        and: "store input"
        def preAGI = 100
        unit1.baseParams.put(UnitParameterType.AGILITY, preAGI)
        assert unit1.getTotalParam(UnitParameterType.AGILITY) == preAGI
        int preActionPoints = unit1.getActionPoints();
        assert preActionPoints == ActionPoints.UNIT_START_ACTION_POINTS

        when:
        worldSteps.doFortify(squad1)
        def fortifyEffect = unit1.effectManager.getEffect(FortificationEffect)

        then: "effect is added, but disabled by default"
        WorldAsserts.assertUnitHasEffect(unit1, FortificationEffect.class);
        assert fortifyEffect.fortificationLevel == 1
        assert !fortifyEffect.enabled
        WorldAsserts.assertUnitActionPointsIs(unit1, preActionPoints - ActionPoints.FORTIFY);
        WorldAsserts.assertAreaObjectHasNoAction(squad1, FortifyAction.class);
        assert unit1.getTotalParam(UnitParameterType.AGILITY) == preAGI

        when: "unit is under attack"
        unit1.switchToDefendingMode(true)

        then: "fortification is activated"
        assert fortifyEffect.enabled
        assert unit1.getTotalParam(UnitParameterType.AGILITY) == preAGI + FortificationEffect.FORTIFICATION_PERCENT_PER_LEVEL

        when: "unit was under attack"
        unit1.switchToDefendingMode(false)

        Mockito.reset(unit1)
        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        squad2.unit.executeMeleeAttack(squad1)
        def booleanCaptor = ArgumentCaptor.forClass(Boolean);

        then: "unit.switchToDefendingMode is called twice during attack"
        verify(unit1, times(2)).switchToDefendingMode(booleanCaptor.capture())
        assert booleanCaptor.getAllValues().get(0)
        assert !booleanCaptor.getAllValues().get(1)

        and: "fortification is deactivated after attack"
        assert !fortifyEffect.enabled
        assert unit1.getTotalParam(UnitParameterType.AGILITY) == preAGI
    }

    // prev. test garants
    @Ignore
    public void 'test DoNotActivateFortifyEffectForAttacker'() {
        given:
        lockCore()
        AbstractSquad squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        AbstractSquad attacker = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit = attacker.unit;
        worldSteps.makeUnitInvincible(unit);
        int preStr = unit.getTotalParam(UnitParameterType.STRENGTH);
        FortificationEffect fortifyEffect = new FortificationEffect();
        Assert.assertEquals(fortifyEffect.getFortificationLevel(), 1);
        unit.getEffectManager().addEffect(fortifyEffect);
        releaseCore()
        waitForNextCoreStep()

        when:
        attacker.meleeAttack(squad1)

        then: "effect not active"
        Assert.assertEquals(unit.getTotalParam(UnitParameterType.STRENGTH), preStr);
    }

    public void 'test FortifyLeveling'() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit = humanSquad1.unit

        when:
        worldSteps.doFortify(humanSquad1)
        FortificationEffect effect = unit.getEffectManager().getEffect(FortificationEffect.class);
        Assert.assertNotNull(effect);

        then:
        assertThat(effect.getFortificationLevel()).isEqualTo(1);

        when:
        for (int i = 2; i <= FortificationEffect.MAX_AP_PROGRESS; i++) {
            worldSteps.rewindTeamsToStartNewWorldStep();
            WorldAsserts.assertUnitHasEffect(unit, FortificationEffect.class);
            assertThat(effect.getFortificationLevel()).isEqualTo(i);
        }

        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        assertThat(effect.getFortificationLevel()).isEqualTo(FortificationEffect.MAX_AP_PROGRESS);

    }

}
