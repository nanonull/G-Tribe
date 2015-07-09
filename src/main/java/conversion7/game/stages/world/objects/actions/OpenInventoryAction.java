package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.AreaObject;

public class OpenInventoryAction extends AbstractAreaObjectAction {


    public OpenInventoryAction(AreaObject object) {
        super(object);
    }

    @Override
    public void execute() {
        Gdxg.clientUi.getInventoryWindow().show(getObject());
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.armyIcon;
    }
}
