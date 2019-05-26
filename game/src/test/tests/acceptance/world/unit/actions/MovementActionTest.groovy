package tests.acceptance.world.unit.actions

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.objects.actions.items.MoveAction
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

public class MovementActionTest extends BaseGdxgSpec {

    @Override
    def setup() {
        GdxgConstants.DEVELOPER_MODE = false;
    }

    void testSquadMovement() {
        when:
        lockCore()
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        releaseCoreAndWaitNextCoreStep()

        then: "from start"
        WorldAsserts.assertSquadCouldMove(squad);
        assert squad.actionsController.getAction(MoveAction) != null

        then: "after move"
        worldSteps.moveOnCell(squad, squad.getLastCell().getCouldBeSeizedNeighborCell());
        WorldAsserts.assertSquadCouldNotMove(squad);
        assert squad.actionsController.getAction(MoveAction) == null
    }

    void 'test animal actions'() {
        when:
        lockCore()
        worldSteps.createFirstDummyPlayerTeam()

        def squad = worldSteps.createUnit(
                worldSteps.createAnimalTeam(),
                worldSteps.getNextStandaloneCell());

        releaseCoreAndWaitNextCoreStep()

        then: "from start"
        WorldAsserts.assertSquadCouldMove(squad);
        assert squad.actionsController.getAction(MoveAction) != null

        then: "after move"
        worldSteps.moveOnCell(squad, squad.getLastCell().getCouldBeSeizedNeighborCell());
        WorldAsserts.assertSquadCouldNotMove(squad);
        assert squad.actionsController.getAction(MoveAction) == null
    }

}
