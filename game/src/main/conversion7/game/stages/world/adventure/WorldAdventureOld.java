package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.utils.Predicate;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.PrimalExperienceJewel;
import org.slf4j.Logger;

// use WorldAdventure
@Deprecated
public enum WorldAdventureOld {
    ANIMAL_UNIT(
            cell -> {
                return cell.canBeSeized();
            },
            cell -> {
                WorldAdventure.placeRndAnimalUnit(cell);
                return true;
            }),
    JEWEL_EXP(
            cell -> {
                return cell.getObject(PrimalExperienceJewel.class) == null
                        && cell.canBeSeized();
            },
            cell -> {
                new PrimalExperienceJewel(cell);
                return true;
            }),
    HUMAN_TRIBE(
            cell -> {
                return cell.canBeSeized();
            },
            cell -> {
                WorldAdventure.placeAiTeam(cell);
                return true;
            });


    private static final Logger LOG = Utils.getLoggerForClass();
    public Predicate<Cell> testCellCandidate;
    public Predicate<Cell> executeOnCell;

    WorldAdventureOld(Predicate<Cell> testCellCandidate, Predicate<Cell> executeOnCell) {

        this.testCellCandidate = testCellCandidate;
        this.executeOnCell = executeOnCell;
    }

    public void startAdventure(Cell cellCandydate) {
        LOG.info("startAdventure " + this);
        LOG.info("...on cell " + cellCandydate);
        executeOnCell.evaluate(cellCandydate);
    }

    public boolean canBeActivatedOn(Cell cellCandydate) {
        return testCellCandidate.evaluate(cellCandydate);
    }

}
