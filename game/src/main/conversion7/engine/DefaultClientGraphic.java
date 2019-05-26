package conversion7.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.GameStage;
import conversion7.game.ui.inputlisteners.GlobalInputListener;
import org.slf4j.Logger;

public class DefaultClientGraphic {

    private static final Logger LOG = Utils.getLoggerForClass();
    private int screenWidthInPx;
    private int screenHeightInPx;

    private DefaultClientUi clientUi;
    private CameraController cameraController;
    private PerspectiveCamera perspectiveCamera;
    public CameraGroupStrategy cameraGroupStrategy;
    public Environment environment;
    private ColorAttribute ambientColorAttribute;
    private Stage globalStage;
    private boolean startedGraphicBatches;

    public DefaultClientGraphic(int screenWidthInPx, int screenHeightInPx) {
        this.screenWidthInPx = screenWidthInPx;
        this.screenHeightInPx = screenHeightInPx;
        LOG.info(getClass().getSimpleName() + " thread: " + Thread.currentThread().getName());
        environment = new Environment();

        float ambient = 0.25f;
        setAmbient(new Color(ambient, ambient, ambient, 0.4f));

        float diffuse = 1f;
        environment.set(new ColorAttribute(ColorAttribute.Diffuse, diffuse, diffuse, diffuse, 1f));

        perspectiveCamera = new PerspectiveCamera(60, screenWidthInPx, screenHeightInPx);
        cameraController = new CameraController(perspectiveCamera, PropertiesLoader.getIntProperty("AbstractClientGraphic.CameraMovementSpeed"));
        perspectiveCamera.position.set(0, CameraController.CAMERA_START_HEIGHT, 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;

        cameraGroupStrategy = new CameraGroupStrategy(perspectiveCamera);
        Gdxg.decalBatchCommon = buildDecalBatch();
        Gdxg.decalBatchTransparentLayer = buildDecalBatch();

        globalStage = new Stage();
        globalStage.addListener(new GlobalInputListener());

        Gdx.gl.glViewport(0, 0, screenWidthInPx, screenHeightInPx);

        LOG.info("Created.");
    }

    public ColorAttribute getAmbientColorAttribute() {
        return ambientColorAttribute;
    }

    public DirectionalLight getSunLight() {
        return ClientGraphic.SUN_LIGHT;
    }

    /** Stage between Gui and GameStages */
    public Stage getGlobalStage() {
        return globalStage;
    }

    public CameraController getCameraController() {
        return cameraController;
    }

    public PerspectiveCamera getCamera() {
        return perspectiveCamera;
    }

    public int getScreenWidthInPx() {
        return screenWidthInPx;
    }

    public int getScreenHeightInPx() {
        return screenHeightInPx;
    }

    public DefaultClientUi getClientUi() {
        return clientUi;
    }

    public void setClientUi(DefaultClientUi clientUi) {
        this.clientUi = clientUi;
    }

    public void setAmbient(Color color) {
        if (ambientColorAttribute == null) {
            ambientColorAttribute = new ColorAttribute(ColorAttribute.AmbientLight, color);
            environment.set(ambientColorAttribute);
        }
        ambientColorAttribute.color.set(color);
    }

    /** Ideally, should be used only once */
    public DecalBatch buildDecalBatch() {
        return new DecalBatch(cameraGroupStrategy);
    }

    public void draw(GameStage activeStage, float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);

        startGraphicBatches();

        if (activeStage != null) {
            activeStage.draw();
        }

        flushGraphicBatches();

        globalStage.draw();
        clientUi.stageGUI.draw();
    }

    private void startGraphicBatches() {
        if (startedGraphicBatches) {
            LOG.warn("force flushGraphicBatches after errors in previous core tick");
            flushGraphicBatches();
        }
        startedGraphicBatches = true;
        Gdxg.postProcessor.capture();
        Gdxg.glPrimitiveRenderer.begin(getCamera().combined, GL20.GL_LINES);
        Gdxg.terrainBatch.begin(perspectiveCamera);
        Gdxg.modelBatch.begin(perspectiveCamera);
    }

    private void flushGraphicBatches() {
        Gdxg.glPrimitiveRenderer.end();
        Gdxg.decalBatchCommon.flush();
        Gdxg.terrainBatch.end();
        Gdxg.modelBatch.end();
        Gdxg.decalBatchTransparentLayer.flush();
        Gdxg.postProcessor.render();
        startedGraphicBatches = false;
    }

    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
        screenWidthInPx = width;
        screenHeightInPx = height;
        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
        Viewport guiViewport = clientUi.stageGUI.getViewport();
        guiViewport.setScreenWidth(width);
        guiViewport.setScreenHeight(height);
    }

    public void dispose() {
        globalStage.dispose();
    }

    public void clearGlobalStageLayers() {
        for (Actor actor : globalStage.getActors()) {
            actor.clear();
        }
    }
}
