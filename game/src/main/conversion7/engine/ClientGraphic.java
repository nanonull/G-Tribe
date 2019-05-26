package conversion7.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import conversion7.engine.geometry.SkyBox;
import conversion7.engine.geometry.water.Water;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.GameStage;
import conversion7.game.ui.ClientUi;
import org.slf4j.Logger;

public class ClientGraphic extends DefaultClientGraphic {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final DirectionalLight SUN_LIGHT = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
    public static final DirectionalLight MOON_LIGHT = new DirectionalLight().set(0.4f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
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

    public void setLight(boolean daytime) {
        environment.remove(SUN_LIGHT);
        environment.remove(MOON_LIGHT);
        environment.add(daytime ? SUN_LIGHT : MOON_LIGHT);

        float night = 0.01f;
        float day = 0.35f;
        setAmbient(daytime ? new Color(day, day, day, 1) : new Color(night, night, night, 1));
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
