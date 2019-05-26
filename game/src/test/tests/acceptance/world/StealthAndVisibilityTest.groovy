package tests.acceptance.world

import conversion7.game.GdxgConstants
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

import static org.fest.assertions.api.Assertions.assertThat

public class StealthAndVisibilityTest extends BaseGdxgSpec {

    public void 'test AC1 CheckStealth On OtherMoves'() {
        given:
        List<Cell> cellsInRow = worldSteps.getNextStandaloneCellsInRow(3);
        Cell cell1 = cellsInRow.get(0);
        Cell cellSquad2 = cellsInRow.get(2);
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2);
        Cell cellSquad2ForMove = cellSquad2.getCouldBeSeizedNeighborCell();
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2ForMove);

        def squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);

        when:
        GdxgConstants.setAlwaysStealthOnCheck(true);
        AbstractSquad squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cellSquad2);

        then: "New(other) squad is in stealth and is not visible"
        WorldAsserts.assertSquadSeesCells(squad1, squad2.getLastCell());
        WorldAsserts.assertSquadDoesntSeeAnother(squad1, squad2);
        WorldAsserts.assertTeamDoesntSeeSquad(squad1.getTeam(), squad2);

        when:
        GdxgConstants.setAlwaysDontStealthOnCheck(true);
        worldSteps.moveOnCell(squad2, cellSquad2ForMove);

        then: "Other squad is got out from stealth"
        WorldAsserts.assertSquadSeesCells(squad1, squad2.getLastCell());
        WorldAsserts.assertSquadSeesAnother(squad1, squad2);
        WorldAsserts.assertTeamSeesSquad(squad1.getTeam(), squad2);
    }

    public void testAC3_NoChecksOnStealthIfVisible() {
        given:
        List<Cell> cellsInRow = worldSteps.getNextStandaloneCellsInRow(3);
        Cell cell1 = cellsInRow.get(0);
        Cell cellSquad2 = cellsInRow.get(2);
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2);
        Cell cellSquad2ForMove = cellSquad2.getCouldBeSeizedNeighborCell();
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2ForMove);

        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);

//                actAndAssertSection("New(other) squad is visible");
        GdxgConstants.setAlwaysDontStealthOnCheck(true);

        AbstractSquad otherSquad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cellSquad2);
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(humanSquad1, otherSquad2);

//                actAndAssertSection("Squad will not stealth on move");
        GdxgConstants.setAlwaysStealthOnCheck(true);

        worldSteps.moveOnCell(otherSquad2, cellSquad2ForMove);
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(humanSquad1, otherSquad2);
    }

    @Ignore
    public void testAC4_AC5_resetStealthWhenGoOutFromFieldOfView() {
        given:
        /*
     [ME][1][2][3][4 OTHER-squad]
     Other moves left by 1 stateBodyText
     Other moves right by 1 stateBodyText
      */
        List<Cell> cellsInRow = worldSteps.getNextStandaloneCellsInRow(5);
        Cell cell1 = cellsInRow.get(0);
        Cell cellSquad2 = cellsInRow.get(4);
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2);
        Cell cellSquad2ForMove = cellsInRow.get(3);

        when:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);
        GdxgConstants.setAlwaysDontStealthOnCheck(true);

        then: "Squad2 stateBodyText is out of FOV of Squad1"
        WorldAsserts.assertSquadDoesntSeeCells(humanSquad1, cellSquad2);
        WorldAsserts.assertSquadSeesCells(humanSquad1, cellSquad2ForMove);


        when:
        AbstractSquad otherSquad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cellSquad2);
        then: "Squad2 is created on out of FOV stateBodyText"
        WorldAsserts.assertSquadDoesntSeeAnother(humanSquad1, otherSquad2);
        WorldAsserts.assertSquadDoesntSeeAnother(otherSquad2, humanSquad1);

        when:
        worldSteps.moveOnCell(otherSquad2, cellSquad2ForMove);
        then: "Squad2 will be detected on enters into FOV"
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(humanSquad1, otherSquad2);
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(otherSquad2, humanSquad1);

        when:
        worldSteps.moveOnCell(otherSquad2, cellSquad2);
        then: "Squad2 goes out from FOV > removed from visible squads for Squad1"
        WorldAsserts.assertSquadDoesntSeeAnother(humanSquad1, otherSquad2);
        WorldAsserts.assertSquadDoesntSeeAnother(otherSquad2, humanSquad1);
    }

    public void testCheckStealthWhenCreated() {
        given:
        List<Cell> cellsInRow = worldSteps.getNextStandaloneCellsInRow(2);
        Cell cell1 = cellsInRow.get(0);
        Cell cellSquad2 = cellsInRow.get(1);
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2);

        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cell1);
        GdxgConstants.setAlwaysDontStealthOnCheck(true);

        when:
        AbstractSquad otherSquad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                cellSquad2);
        then: "Squad2 is created in FOV"
        WorldAsserts.assertSquadSeesAnother(humanSquad1, otherSquad2);
        WorldAsserts.assertSquadSeesAnother(otherSquad2, humanSquad1);
    }

    public void 'test CheckStealth when UnitRemoved'() {
        given:
        GdxgConstants.setAlwaysDontStealthOnCheck(true);

        List<Cell> cellsInRow = worldSteps.getNextStandaloneCellsInRow(2);
        Cell cell1 = cellsInRow.get(0);
        Cell cellSquad2 = cellsInRow.get(1);
        worldSteps.makeCellsVisibleOnSightOfView(cell1, cellSquad2);

        Team team1 = worldSteps.createHumanTeam();
//        AbstractSquad squad1 = WorldServices.createUnit(
//                team1, cell1, SahelanthropusTchadensis);

        when:
        Team team2 = worldSteps.createHumanTeam();
//        AbstractSquad squad2 = WorldServices.createUnit(
//                team2, cellSquad2, SahelanthropusTchadensis);

        then:
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(squad1, squad2);
        WorldAsserts.assertSquadSeesAnotherOutOfStealth(squad2, squad1);

        when:
        worldSteps.defeatKillUnit(squad1.unit);

        then:
        WorldAsserts.assertUnitDead(squad1.unit)

        assert squad1.visibleCellsWithMyCell.size == 0
        WorldAsserts.assertSquadDoesntSeeAnother(squad1, squad2);
        WorldAsserts.assertTeamDoesntSeeSquad(team1, squad2);

        WorldAsserts.assertSquadSeesCells(squad2, cell1);
        WorldAsserts.assertSquadDoesntSeeAnother(squad2, squad1);
        WorldAsserts.assertTeamDoesntSeeSquad(team2, squad1);
    }

    void 'test Im Seeing Squad And It Was Removed From World'() {
        given:
        lockCore()
        AbstractSquad squad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        LOG.info("testImSeeingSquadAndItWasRemovedFromWorld squad1 {}", squad1)

        AbstractSquad squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())
        LOG.info("testImSeeingSquadAndItWasRemovedFromWorld squad2 {}", squad2)
        releaseCore()
        waitForNextCoreStep()

        WorldAsserts.assertSquadsSeesEachOtherOutOfStealth(squad1, squad2)

        when:
        worldSteps.defeatObject(squad2)

        then:
        WorldAsserts.assertSquadDoesntSeeAnother(squad1, squad2)
        assertThat(squad1.visibleForObjects).doesNotContain(squad2)
    }

}
