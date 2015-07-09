package conversion7.engine;

import conversion7.engine.geometry.SkyBox;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.GameStage;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;

public class ClientGraphic extends DefaultClientGraphic {

    private static final Logger LOG = Utils.getLoggerForClass();
    public Water water;
    public SkyBox skyBox;

    public ClientGraphic(int screenWidthInPx, int screenHeightInPx) {
        super(screenWidthInPx, screenHeightInPx);
        setClientUi(new ClientUi(this));
        Gdxg.graphic = this;
        water = new Water();
        skyBox = new SkyBox();
        LOG.info("Created.");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        GdxgConstants.SCREEN_WIDTH_IN_PX = width;
        GdxgConstants.SCREEN_HEIGHT_IN_PX = height;
    }

    @Override
    public void draw(GameStage activeStage, float delta) {
        super.draw(activeStage, delta);
        water.update(delta);
    }
}
