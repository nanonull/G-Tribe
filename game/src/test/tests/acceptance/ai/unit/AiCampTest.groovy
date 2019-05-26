package tests.acceptance.ai.unit

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.actions.items.BuildCampAction
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.SkillType
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

import static org.fest.assertions.api.Assertions.assertThat

class AiCampTest extends BaseGdxgSpec {

    @Override
    def setup() {
        GdxgConstants.AREA_OBJECT_AI = true
    }

    void 'test build camp'() {
        given:
        lockCore()
        worldSteps.createHumanTeam()

        def cell1 = worldSteps.nextNeighborCell
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(false),
                cell1);
        Unit unit = squad.unit
        Team team = squad.getTeam();
        Cell cell = squad.getLastCell();
        worldSteps.prepareForBuildCamp(squad)
        worldSteps.makeUnitInvincible(squad.unit)
        releaseCoreAndWaitNextCoreStep()

        when: "1st turn"
        assert squad.unit.canBuildCamp()
        int inTowns = team.getCamps().size;
        worldSteps.rewindTeamsToStartNewWorldStep()
        Camp town = cell.getCamp();

        then:
        assert squad.lastCell == cell1
        assert squad.lastCell.camp
        assert squad.lastCell.camp.constructionProgress == ActionPoints.UNIT_START_ACTION_POINTS
        assertThat(team.getCamps().size).isEqualTo(inTowns + 1);

        when: "2nd turn"
        assertThat(squad.getLastCell()).isEqualTo(cell);
        int townsAmountAfterNewTownStarted = team.getCamps().size;
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        assertThat(team.getCamps().size).isEqualTo(townsAmountAfterNewTownStarted);
        WorldAsserts.assertUnitActionPointsIs(unit, ActionPoints.UNIT_START_ACTION_POINTS); // new step
        assertThat(town.getConstructionProgress()).isEqualTo(ActionPoints.CAMP_CONSTRUCTION_TOTAL);
        WorldAsserts.assertTownConstructionCompleted(town);
        WorldAsserts.assertAreaObjectHasNoAction(squad, BuildCampAction.class);
    }

    void 'test continue camp building on another cell'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = true

        lockCore()
        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()
        def team = worldSteps.createHumanTeam(false)

        Cell cell1 = worldSteps.nextNeighborCell;
        Cell cell2 = worldSteps.nextNeighborCell;
        Cell cell3 = worldSteps.nextNeighborCell;
        def squad = worldSteps.createUnit(
                team,
                cell1);
        squad.team.teamSkillsManager.getSkill(SkillType.BUILD_CAMP).learn()
        assert squad.unit.canBuildCamp()
        worldSteps.makeCellsBadForCamp(squad.visibleCellsWithMyCell.toList())
        assert !Camp.couldBeBuiltOnCell(squad.lastCell)
        worldSteps.makeCellsGoodForCamp([cell3])
        worldSteps.makeUnitInvincible(squad.unit)
        squad.validate(true)
        releaseCoreAndWaitNextCoreStep()

        and: "not completed camp exists"
        WorldAsserts.assertSquadSeesCells(squad, cell3)
        def camp = worldSteps.startCampConstruction(team, cell3)
        assert (team.getCamps().size) == (1);
        assert camp.constructionProgress == 0

        when: "1st turn"
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "move to camp"
        assert squad.lastCell == cell2
        assert !squad.lastCell.camp
        assert !Camp.couldBeBuiltOnCell(squad.lastCell)
        assert (team.getCamps().size) == (1);

        when: "2nd turn"
        worldSteps.rewindTeamsToStartNewWorldStep();

        then: "move to camp one more cell + continue building camp"
        assert squad.lastCell == cell3
        assert squad.lastCell.camp
        assert (team.getCamps().size) == (1);
        assert camp.constructionProgress == ActionPoints.UNIT_START_ACTION_POINTS - ActionPoints.MOVEMENT
    }

}
