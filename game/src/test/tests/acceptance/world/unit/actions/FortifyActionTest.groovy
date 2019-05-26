package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.actions.items.FortifyAction
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.effects.items.FortificationEffect
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class FortifyActionTest extends BaseGdxgSpec {

    void test_FortifyRemovedAfterMove() {
        given:
        lockCore()
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        Unit unit1 = humanSquad1.unit

        when:
        worldSteps.doFortify(humanSquad1)
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
        WorldAsserts.assertUnitHasEffect(unit1, FortificationEffect.class)
        releaseCore()
        waitForNextCoreStep()

        worldSteps.rewindTeamsToStartNewWorldStep()
        worldSteps.moveOnCell(humanSquad1, humanSquad1.getLastCell().getCouldBeSeizedNeighborCell())

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
        WorldAsserts.assertUnitHasNoEffect(unit1, FortificationEffect.class)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, FortifyAction.class)

    }

    void test_fortifyActionAdded() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        Unit unit = humanSquad1.unit
        int preActionPoints = unit.getActionPoints()

        when:
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, FortifyAction.class)
        worldSteps.doFortify(humanSquad1)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
        WorldAsserts.assertUnitHasEffect(unit, FortificationEffect.class)
        WorldAsserts.assertUnitActionPointsIs(unit, preActionPoints - ActionPoints.FORTIFY)
    }

    void 'test fortifyMulti'() {
        when:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        Unit unit1 = humanSquad1.unit

        then:
        WorldAsserts.assertAreaObjectHasAction(humanSquad1, FortifyAction.class)

        when:
        int unit1ap = unit1.getActionPoints()
        worldSteps.doFortify(humanSquad1)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
        WorldAsserts.assertUnitActionPointsIs(unit1, unit1ap - ActionPoints.FORTIFY)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        def fortifyEffect = unit1.effectManager.getEffect(FortificationEffect)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
        assert unit1.getActionPoints() == ActionPoints.UNIT_START_ACTION_POINTS
        assert fortifyEffect
        assert fortifyEffect.fortificationLevel == 2
    }

    void test_couldNotFortifyIfNoAP() {
        given:
        def humanSquad1 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit1 = humanSquad1.unit

        when:
        Cell couldBeSeizedNeighborCell = humanSquad1.getLastCell().getCouldBeSeizedNeighborCell()
        worldSteps.moveOnCell(humanSquad1, couldBeSeizedNeighborCell)
        Assert.assertTrue(unit1.getActionPoints() < ActionPoints.FORTIFY)

        then:
        WorldAsserts.assertAreaObjectHasNoAction(humanSquad1, FortifyAction.class)
    }

}
