package tests.acceptance.world.unit.actions

import conversion7.engine.quest_old.AbstractQuest
import conversion7.game.stages.world.objects.actions.items.PathfinderAction
import shared.BaseGdxgSpec

class PathfinderActionTest extends BaseGdxgSpec {

    @Override
    def cleanup() {
        for (AbstractQuest quest : new ArrayList<AbstractQuest>(AbstractQuest.activeQuests)) {
            quest.completeAndEnableInteraction()
        }
    }

    void 'test sees another squads'() {
        given:
        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        def squad2 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        def squad3 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
        def pathfinderAction = squad2.actionsController.getAction(PathfinderAction)
        assert pathfinderAction

        when:
        pathfinderAction.run()
        then:
        def cellsHints = pathfinderAction.environmentDescription.split("\n \n")
        int cellDetailsFromIndex = 3
        assert cellsHints[cellDetailsFromIndex++] == "Cell[-1:+1]: nothing special"
        assert (cellDetailsFromIndex += 2)
        assert cellsHints[cellDetailsFromIndex++] == "Cell[-1:0]: ${squad1.unit.getGameClassName()} unit of '${team1.name}' team"
        assert cellsHints[cellDetailsFromIndex++] == "Cell[0:0]: ${squad2.unit.getGameClassName()} unit of '${team1.name}' team"
        assert cellsHints[cellDetailsFromIndex++] == "Cell[+1:0]: ${squad3.unit.getGameClassName()} unit of '${team2.name}' team"
        assert (cellDetailsFromIndex += 2)
        assert cellsHints[cellDetailsFromIndex++] == "Cell[+1:-1]: nothing special"
    }


    void 'test sees history'() {
        given:
        def team1 = worldSteps.createHumanTeam()
        def team2 = worldSteps.createHumanTeam()

        def squad1 = worldSteps.createUnit(
                team1,
                worldSteps.nextNeighborCell)
        assert squad1.actionsController.getAction(PathfinderAction)

        def squad2 = worldSteps.createUnit(
                team2,
                worldSteps.nextNeighborCell)
    }

    void 'test save exploring results into team event'() {

    }


}
