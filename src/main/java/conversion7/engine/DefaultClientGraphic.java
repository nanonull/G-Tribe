package conversion7.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
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
    private DirectionalLight sunLight;
    private ColorAttribute ambientColorAttribute;
    /** Stage between Gui and GameStages */
    public Stage globalStage;

    public DefaultClientGraphic(int screenWidthInPx, int screenHeightInPx) {
        this.screenWidthInPx = screenWidthInPx;
        this.screenHeightInPx = screenHeightInPx;
        LOG.info(getClass().getSimpleName() + " thread: " + Thread.currentThread().getName());
        environment = new Environment();

        float ambient = 0.2f;
        ambientColorAttribute = new ColorAttribute(ColorAttribute.AmbientLight, ambient, ambient, ambient, 0.4f);
        environment.set(ambientColorAttribute);
        float diffuse = 1f;
        environment.set(new ColorAttribute(ColorAttribute.Diffuse, diffuse, diffuse, diffuse, 1f));
        sunLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        environment.add(sunLight);

        perspectiveCamera = new PerspectiveCamera(55, screenWidthInPx, screenHeightInPx);
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
        return sunLight;
    }

    /** Ideally, should be used only once */
    public DecalBatch buildDecalBatch() {
        return new DecalBatch(cameraGroupStrategy);
    }


    public void draw(GameStage activeStage, float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);

        Gdxg.postProcessor.capture();
        Gdxg.glPrimitiveRenderer.begin(getCamera().combined, GL20.GL_LINES);
        Gdxg.terrainBatch.begin(perspectiveCamera);
        Gdxg.modelBatch.begin(perspectiveCamera);

        if (activeStage != null) {
            activeStage.draw();
        }

        Gdxg.glPrimitiveRenderer.end();

        Gdxg.decalBatchCommon.flush();
        Gdxg.terrainBatch.end();
        Gdxg.modelBatch.end();
        Gdxg.decalBatchTransparentLayer.flush();
        Gdxg.postProcessor.render();

        clientUi.stageGUI.draw();

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
    }

    public DefaultClientUi getClientUi() {
        return clientUi;
    }

    public void setClientUi(DefaultClientUi clientUi) {
        this.clientUi = clientUi;
    }
}
