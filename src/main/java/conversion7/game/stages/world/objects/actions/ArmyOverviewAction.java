package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class ArmyOverviewAction extends AbstractAreaObjectAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public ArmyOverviewAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        LOG.info("execute");
        Gdxg.clientUi.getArmyOverviewWindow().showFor(getObject());
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.armyIcon;
    }
}
