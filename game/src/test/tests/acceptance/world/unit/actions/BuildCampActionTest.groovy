package tests.acceptance.world.unit.actions

import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.actions.items.BuildCampAction
import conversion7.game.stages.world.objects.buildings.Camp
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.unit.Unit
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

import static org.fest.assertions.api.Assertions.assertThat

public class BuildCampActionTest extends BaseGdxgSpec {

    public void testNoActionIfNoAp() {
        when:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Unit unit = squad.unit
        worldSteps.prepareForBuildCamp(squad)

        then:
        WorldAsserts.assertAreaObjectHasAction(squad, BuildCampAction.class);

        and: "1 AP"
        unit.updateActionPoints(-unit.getActionPoints() + 1);
        WorldAsserts.assertAreaObjectHasAction(squad, BuildCampAction.class);

        and: "0 AP"
        unit.updateActionPoints(-unit.getActionPoints());
        WorldAsserts.assertAreaObjectHasNoAction(squad, BuildCampAction.class);
    }

    public void 'test ArmyCreates camp'() {
        given:
        Team humanTeam = worldSteps.createHumanTeam();
        def cell = worldSteps.getNextNeighborCell()
//        def squad = WorldServices.createUnit(humanTeam, cell, SahelanthropusTchadensis);
        worldSteps.prepareForBuildCamp(squad)

        when:
        worldSteps.squadCreatesCamp(squad);

        then:
        WorldAsserts.assertAreaObjectHasNoAction(squad, BuildCampAction.class);
        WorldAsserts.assertAreaObjectDefeated(squad, false);
        WorldAsserts.assertCellHasTown(squad.getLastCell(), true);
    }

    public void 'test Unit completes Building camp'() {
        given:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Unit unit = squad.unit
        Team team = squad.getTeam();
        Cell cell = squad.getLastCell();
        worldSteps.prepareForBuildCamp(squad)

        when: "1st turn"
        int inTowns = team.getCamps().size;
        int unitAP = unit.getActionPoints();

        worldSteps.squadIsBuildingCampOneStep(squad); //15
        Camp town = cell.getCamp();

        then:
        assertThat(team.getCamps().size).isEqualTo(inTowns + 1);
        WorldAsserts.assertUnitActionPointsIs(unit, 0);
        assertThat(town.getConstructionProgress()).isEqualTo(unitAP);

        when: "2nd turn"
        worldSteps.rewindTeamsToStartNewWorldStep();
        assertThat(squad.getLastCell()).isEqualTo(cell);
        int townsAmountAfterNewTownStarted = team.getCamps().size;
        worldSteps.squadIsBuildingCampOneStep(squad); //30
        int giveApOn2ndStep = ActionPoints.CAMP_CONSTRUCTION_TOTAL - ActionPoints.UNIT_START_ACTION_POINTS

        then:
        assertThat(team.getCamps().size).isEqualTo(townsAmountAfterNewTownStarted);
        WorldAsserts.assertUnitActionPointsIs(unit, ActionPoints.UNIT_START_ACTION_POINTS - giveApOn2ndStep);
        assertThat(town.getConstructionProgress()).isEqualTo(ActionPoints.CAMP_CONSTRUCTION_TOTAL);
        WorldAsserts.assertTownConstructionCompleted(town);
        WorldAsserts.assertAreaObjectHasNoAction(squad, BuildCampAction.class);

    }

    public void testTownActionActiveAfterMoveOnCellWithTownConstructionInProgress() {
        given:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        Cell startCell = squad.getLastCell();
        worldSteps.prepareForBuildCamp(squad)

        when: "Start construction"
        worldSteps.squadIsBuildingCampOneStep(squad);
        then:
        WorldAsserts.assertCellHasTown(startCell, true);
        assertThat(startCell.getCamp().isConstructionCompleted()).isFalse();

        when: "Move from camp"
        worldSteps.rewindTeamsToStartNewWorldStep();
        worldSteps.moveOnCell(squad, startCell.getCouldBeSeizedNeighborCell());

        and: "Return on startCell"
        worldSteps.rewindTeamsToStartNewWorldStep();
        worldSteps.moveOnCell(squad, startCell);
        then:
        WorldAsserts.assertAreaObjectHasAction(squad, BuildCampAction.class);
    }

}
