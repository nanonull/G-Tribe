package tests.acceptance.world.unit.actions

import conversion7.engine.dialog.AbstractDialog
import conversion7.game.dialogs.RitualDialog
import conversion7.game.stages.world.objects.Ritual
import conversion7.game.stages.world.objects.actions.items.RitualAction
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.actions.ActionEvaluation
import conversion7.game.stages.world.unit.effects.items.ShamanUnitEffect
import shared.BaseGdxgSpec

class RitualActionTest extends BaseGdxgSpec {

    void 'create shaman via ritual'() {
        given:
        lockCore()
        def playerTeam = worldSteps.createHumanTeam(true)

        def originCell = worldSteps.getNextNeighborCell()
        def squadTarget = worldSteps.createUnit(playerTeam,
                originCell)
        releaseCore()
        worldSteps.createAndCompleteCampConstruction(playerTeam, originCell)
        lockCore()
        List<AbstractSquad> squadsAround = []
        for (int i = 0; i < Ritual.STEPS; i++) {
            println i
            def squadAround = worldSteps.createUnit(playerTeam,
                    originCell.getCouldBeSeizedNeighborCell())
            squadsAround.add(squadAround)
            squadAround.unit.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX)
            assert squadAround.unit.canDoRitual()
            assert squadAround.getActionsController().getAction(RitualAction)

        }
        releaseCore()
        assert !originCell.ritual

        when:
        def ritualAction1 = squadsAround[0].getActionsController().getAction(RitualAction)
        assert ritualAction1.actionEvaluation.testOwnerVsTargetCell.apply(squadsAround[0].unit, originCell)
        ritualAction1.handleAcceptedInput(originCell)

        then:
        assert originCell.ritual
        assert originCell.ritual.progress == 1

        when:
        for (int i = 1; i < Ritual.STEPS - 1; i++) {
            def squad = squadsAround[i]
            assert ActionEvaluation.RITUAL.evaluateOwner(squad.unit)
            assert ActionEvaluation.RITUAL.testOwnerVsTargetCell.apply(squad.unit, originCell)
            def ritualAction = squad.getActionsController().getAction(RitualAction)
            ritualAction.handleAcceptedInput(originCell)
            assert originCell.ritual.progress == i + 1
        }

        and: 'last'
        def squad = squadsAround[squadsAround.size() - 1]
        assert ActionEvaluation.RITUAL.testOwner.evaluate(squad.unit)
        assert ActionEvaluation.RITUAL.testOwnerVsTargetCell.apply(squad.unit, originCell)
        def ritualAction = squad.getActionsController().getAction(RitualAction)
        def evolutionExperienceIn = playerTeam.evolutionExperience
        ritualAction.handleAcceptedInput(originCell)
        then:
        assert AbstractDialog.activeDialog
        assert AbstractDialog.activeDialog.getClass() == RitualDialog

        when:
        worldSteps.pressDialogOption(AbstractDialog.activeDialog, "SHAMAN")

        then:
        assert !originCell.ritual
        assert playerTeam.evolutionExperience == evolutionExperienceIn + Ritual.EVOLUTION_EXP
        assert squadTarget.unit.isShaman()
        assert squadTarget.unit.effectManager.getEffect(ShamanUnitEffect)
        assert squadTarget.team.shaman == squadTarget.unit
        assert (AbstractDialog.activeDialog.complete()) == null
    }

}
