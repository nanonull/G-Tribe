package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.objects.TownFragment;
import org.slf4j.Logger;

public class CreateTownAction extends AbstractAreaObjectAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public CreateTownAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        LOG.info("execute");
        HumanSquad oldArmy = (HumanSquad) getObject();
        Cell cell = oldArmy.getCell();
        World.getAreaViewer().deselect();
        oldArmy.removeFromWorld();
        // create town
        TownFragment town = oldArmy.getTeam().createTown(cell);
        town.getUnitsController().addUnitsAndValidate(oldArmy.getUnits());
        town.getFoodStorage().setFoodAndValidate(oldArmy.getFoodStorage().getFood());
        World.getAreaViewer().select(town);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.homeIcon;
    }
}
