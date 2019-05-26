package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.objects.actions.items.CaptureUnitAction
import shared.BaseGdxgSpec

class CaptureUnitActionTest extends BaseGdxgSpec {
    def unitsAroundForCaptureBase

    @Override
    def cleanup() {
        CaptureUnitAction.MIN_UNITS_AROUND_FOR_CAPTURE = unitsAroundForCaptureBase
    }

    void 'test capture another human'() {
        given:
        unitsAroundForCaptureBase = CaptureUnitAction.MIN_UNITS_AROUND_FOR_CAPTURE
        CaptureUnitAction.MIN_UNITS_AROUND_FOR_CAPTURE = 2

        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)

        def captureUnitAction1
        def captureUnitAction2
        def captureUnitAction3

        when: '1st unit around'
        def squad2enemy = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
        captureUnitAction1 = squad1.getActionsController().getAction(CaptureUnitAction)
        then:
        assert !captureUnitAction1

        when: '2nd unit around'
        def squad3 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        captureUnitAction1 = squad1.getActionsController().getAction(CaptureUnitAction)
        captureUnitAction2 = squad2enemy.getActionsController().getAction(CaptureUnitAction)
        captureUnitAction3 = squad3.getActionsController().getAction(CaptureUnitAction)

        then:
        assert captureUnitAction1
        assert !captureUnitAction2
        assert captureUnitAction3

        when:
        captureUnitAction3.handleAcceptedInput(squad2enemy.lastCell)
        captureUnitAction1 = squad1.getActionsController().getAction(CaptureUnitAction)
        captureUnitAction2 = squad2enemy.getActionsController().getAction(CaptureUnitAction)
        captureUnitAction3 = squad3.getActionsController().getAction(CaptureUnitAction)
        then:
        assert squad3.unit.actionPoints == ActionPoints.UNIT_START_ACTION_POINTS - CaptureUnitAction.ACTION_POINTS
        assert squad2enemy.team == squad1.team
        assert !captureUnitAction1
        assert !captureUnitAction2
        assert !captureUnitAction3
    }


}
