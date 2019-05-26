package tests.acceptance.world

import com.badlogic.gdx.utils.Array
import conversion7.engine.utils.Utils
import conversion7.game.stages.world.landscape.Cell
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

import static org.fest.assertions.api.Assertions.assertThat

public class LineOfSightAndFieldOfViewTest extends BaseGdxgSpec {
    private static final Logger LOG = Utils.getLoggerForClass();

    @Ignore
    public void 'test SquadLineOfSight'() {
        given:
        lockCore()
        Cell startCell = worldSteps.getNextStandaloneCell();
        Cell cellMinus4 = startCell.getCell(-4, 0);
        Cell cellMinus3 = startCell.getCell(-3, 0);
        LOG.info("cellMinus3 {}", cellMinus3);
        Cell cellMinus2 = startCell.getCell(-2, 0);
        Cell cellMinus1 = startCell.getCell(-1, 0);
        LOG.info("startCell {}", startCell);
        Cell cellPlus1 = startCell.getCell(1, 0);
        Cell cellPlus2 = startCell.getCell(2, 0);
        Cell cellPlus3 = startCell.getCell(3, 0);
        Cell cellPlus4 = startCell.getCell(4, 0);

        startCell.getLandscape().setForest();
        cellMinus1.getLandscape().setForest();
        worldSteps.removeBlockingSightFromCells(cellMinus4, cellMinus3, cellMinus2,
                cellPlus1, cellPlus2, cellPlus3, cellPlus4);
        releaseCore()

        when: "On creation"
        /*
        -4      -3      -2     -1      0          1        2        3         4
        [INVIS] [INVIS][INVIS][FOREST][ME+FOREST][VISIBLE][VISIBLE][VISIBLE] [INVIS]
         */
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                startCell);
        waitForNextCoreStep()

        then: "On creation"
        WorldAsserts.assertSquadSeesCells(humanSquad1, cellMinus1, startCell, cellPlus1, cellPlus2, cellPlus3);
        WorldAsserts.assertSquadDoesntSeeCells(humanSquad1, cellMinus4, cellMinus3, cellMinus2, cellPlus4);

        when: "After Move"
        /*
        -4         -3      -2       -1          0       1        2        3          4
        [VISIBLE] [VISIBLE][VISIBLE][ME+FOREST][VISIBLE][INVISIB][INVISIB][INVISIB] [INVISIB]
         */
        worldSteps.moveOnCell(humanSquad1, cellMinus1);

        then: "After Move"
        WorldAsserts.assertSquadSeesCells(humanSquad1, cellMinus4, cellMinus3, cellMinus2, cellMinus1, startCell);
        WorldAsserts.assertSquadDoesntSeeCells(humanSquad1, cellPlus1, cellPlus2, cellPlus3, cellPlus4);

        when: "After Move on start stateBodyText"
        /*
        -4      -3      -2     -1      0          1        2        3         4
        [INVIS] [INVIS][INVIS][FOREST][ME+FOREST][VISIBLE][VISIBLE][VISIBLE] [INVIS]
         */
        worldSteps.moveOnCell(humanSquad1, startCell);

        then: "After Move on start stateBodyText"
        WorldAsserts.assertSquadSeesCells(humanSquad1, cellMinus1, startCell, cellPlus1, cellPlus2, cellPlus3);
        WorldAsserts.assertSquadDoesntSeeCells(humanSquad1, cellMinus4, cellMinus3, cellMinus2, cellPlus4);

    }

    public void 'test CellIsNotVisibleByDefeatedSquad'() {
        given:
        lockCore()
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        releaseCore()
        waitForNextCoreStep()

        when:
        Array<Cell> viewRadiusCellsAndMyCell = humanSquad1.getViewRadiusCellsAndMyCell();

        then:
        humanSquad1.removeFromWorld();
        WorldAsserts.assertAreaObjectDefeated(humanSquad1);
        assertThat(humanSquad1.getVisibleCellsWithMyCell()).isEmpty();
        assertThat(humanSquad1.getVisibleCellsAround()).isEmpty();
        WorldAsserts.assertSquadDoesntSeeCells(humanSquad1, viewRadiusCellsAndMyCell.toArray(Cell.class) as Cell[]);
    }

}
