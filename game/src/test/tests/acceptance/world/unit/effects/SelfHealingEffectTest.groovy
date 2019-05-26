package tests.acceptance.world.unit.effects

import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.SelfHealingEffect
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class SelfHealingEffectTest extends BaseGdxgSpec {
    void 'test HealingTick'() {
        when:
        Cell cell = worldSteps.getNextStandaloneCell()
        worldSteps.makePerfectConditionsOnCell(cell)
        def humanSquad1 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                cell)
        Unit unit = humanSquad1.unit
        assert !unit.hurt(2)

        int rewindWorldsSteps = 0

        then: "unit has healing effect from creation"
        WorldAsserts.assertUnitHasEffect(unit, SelfHealingEffect.class)

        when:
        int healthBeforeHeal = unit.getBaseParams().get(UnitParameterType.HEALTH)
        int expHealthAfterHealing = healthBeforeHeal + 1
        worldSteps.rewindTeamsToStartNewWorldStep()
        rewindWorldsSteps++
        SelfHealingEffect healing = unit.getEffectManager().getEffect(SelfHealingEffect.class)
        then:
        WorldAsserts.assertUnitHasEffect(unit, SelfHealingEffect.class)
        WorldAsserts.assertUnitEffectHasTickCounter(healing, 1)
        WorldAsserts.assertUnitHasHealth(unit, healthBeforeHeal)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        rewindWorldsSteps++

        then: "no healing till effect counter reaches MAX value: SelfHealingEffect.HEALING_LENGTH_STEPS"
        WorldAsserts.assertUnitHasHealth(unit, healthBeforeHeal)
        WorldAsserts.assertUnitEffectHasTickCounter(healing, 2)

        when:
        int ticksToHeal = SelfHealingEffect.HEALING_LENGTH_STEPS - rewindWorldsSteps
        worldSteps.rewindWorldSteps(ticksToHeal)

        then:
        WorldAsserts.assertUnitHasHealth(unit, expHealthAfterHealing)
        WorldAsserts.assertUnitHasEffect(unit, SelfHealingEffect.class)
        WorldAsserts.assertUnitEffectHasTickCounter(healing, 0)
    }

    void 'test Healing in town'() {
        when:
        Cell cell = worldSteps.getNextStandaloneCell()
        worldSteps.makePerfectConditionsOnCell(cell)
        def team = worldSteps.createHumanTeam()
        def humanSquad1 = worldSteps.createUnit(team,
                cell)
        worldSteps.createAndCompleteCampConstruction(team, cell)
        Unit unit = humanSquad1.unit
        assert !unit.hurt(SelfHealingEffect.CAMP_HEAL_BOOST + 1)
        int healthBeforeHeal = unit.getBaseParams().get(UnitParameterType.HEALTH)
        int expHealthAfterHealing = healthBeforeHeal + SelfHealingEffect.CAMP_HEAL_BOOST

        then: "unit has healing effect from creation"
        WorldAsserts.assertUnitHasEffect(unit, SelfHealingEffect.class)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        SelfHealingEffect healing = unit.getEffectManager().getEffect(SelfHealingEffect.class)
        then: "heal all possible points"
        WorldAsserts.assertUnitHasEffect(unit, SelfHealingEffect.class)
        WorldAsserts.assertUnitEffectHasTickCounter(healing, 0)
        WorldAsserts.assertUnitHasHealth(unit, expHealthAfterHealing)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "heal full: hp == max hp"
        assert unit.getTotalParam(UnitParameterType.HEALTH) == unit.maxHealth
        WorldAsserts.assertUnitEffectHasTickCounter(healing, 0)
    }

}
