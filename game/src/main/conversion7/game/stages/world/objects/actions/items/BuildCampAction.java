package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class BuildCampAction extends AbstractSquadAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public BuildCampAction() {
        super(Group.TRIBE);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.homeIcon;
    }

    public static boolean hasCellEnoughDistanceFromAnotherTowns(Cell cell) {
        Array<Cell> cellsAround = cell.getCellsAroundToRadiusInclusively(Camp.CAMP_RADIUS);
        for (Cell arCell : cellsAround) {
            if (arCell.camp != null) {
                return false;
            }
        }

        return true;
    }

    public static void buildCamp(AbstractSquad squad) {
        Cell cell = squad.getLastCell();
        Camp camp = squad.getTeam().createCamp(cell);
    }

    @Override
    public String buildDescription() {
        return getName() +
                "\n \n" + Camp.HINT;
    }

    @Override
    public void begin() {
        buildCamp(getSquad());
        getSquad().getInventory().remove(ResourceCosts.getCost(Camp.class));
    }

}
