package tests.acceptance.world.town

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.team.Team
import shared.BaseGdxgSpec

public class CaptureCampTest extends BaseGdxgSpec {

    public void 'test 1'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        Team humanTeam = worldSteps.createHumanTeam();
        def cellWithCamp = worldSteps.getNextNeighborCell()
//        def squad = WorldServices.createUnit(humanTeam, cellWithCamp, SahelanthropusTchadensis);
        worldSteps.prepareForBuildCamp(squad)
        worldSteps.squadCreatesCamp(squad);
        assert cellWithCamp.camp.team == squad.team
        and: 'move squad away'
        worldSteps.moveOnCell(squad, squad.lastCell.getCell(squad.getViewRadius() + 1, 0))

        and:
        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(), worldSteps.nextNeighborCell);
        squad.team.setEnemy(squad2.team)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert squad2.lastCell == cellWithCamp
        assert cellWithCamp.camp.team == squad2.team
    }

}
