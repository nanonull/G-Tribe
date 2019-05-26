package tests.acceptance.world.town

import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.team.skills.SkillType
import shared.BaseGdxgSpec

class CampNetTest extends BaseGdxgSpec {

    public void 'test 1'() {
        given:
        Cell cell = worldSteps.nextNeighborCell;
        worldSteps.makePerfectConditionsOnCell(cell);
        def team = worldSteps.createHumanTeam()
        def squad = worldSteps.createUnit(team,
                cell);
        squad.team.teamSkillsManager.getSkill(SkillType.BUILD_CAMP).learn()

        when:
        worldSteps.createAndCompleteCampConstruction(team, cell)
        then:
        assert cell.camp
        assert cell.camp.net
        assert cell.camp.net.camps.toList() == [cell.camp].toList()

        when:
        def initialNet = cell.camp.net
        def cell2 = cell.getCell(Camp.MIN_DISTANCE_BTW_CAMPS, 0)
        then:
        Camp.couldBeBuiltOnCell(cell2)

        when: 'create 2nd camp'
        worldSteps.createAndCompleteCampConstruction(team, cell2)
        then:
        assert cell2.camp
        assert cell2.camp.net == cell.camp.net
        assert cell.camp.net.camps.toList().sort() == [cell.camp, cell2.camp].toList().sort()


        when:
        def cell3 = cell2.getCell(Camp.MIN_DISTANCE_BTW_CAMPS, 0)
        then:
        Camp.couldBeBuiltOnCell(cell3)

        when: 'create 3rd camp'
        worldSteps.createAndCompleteCampConstruction(team, cell3)
        def squad2 = worldSteps.createUnit(worldSteps.createHumanTeam(),
                worldSteps.nextStandaloneCell);

        then:
        assert cell3.camp
        assert cell3.camp.net == cell.camp.net
        assert cell.camp.net.camps.toList().sort() == [cell.camp, cell2.camp, cell3.camp].toList().sort()

        when:
        cell2.camp.captureBy(squad2.team)

        then:
        assert cell.camp.net
        assert cell2.camp.net
        assert cell3.camp.net
        assert cell.camp.net != initialNet
        assert cell.camp.net != cell2.camp.net
        assert cell.camp.net != cell3.camp.net
        assert cell.camp.net.camps.toList().sort() == [cell.camp].toList().sort()
        assert cell2.camp.net.camps.toList().sort() == [cell2.camp].toList().sort()
        assert cell3.camp.net.camps.toList().sort() == [cell3.camp].toList().sort()

    }


}
