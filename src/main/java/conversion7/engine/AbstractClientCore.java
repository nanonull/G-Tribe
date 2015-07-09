package conversion7.engine;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.utils.ShaderLoader;
import conversion7.engine.ashley.FluentPollingSystem;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.SkyBox;
import conversion7.engine.geometry.terrain.TerrainShader;
import conversion7.engine.geometry.water.WaterShader;
import conversion7.engine.tween.ActorAccessor;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.WaitLibrary;
import conversion7.game.stages.GameStage;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import org.slf4j.Logger;

public abstract class AbstractClientCore extends Game {

    private static final Logger LOG = Utils.getLoggerForClass();
    static Throwable applicationCrash;

    public DefaultClientGraphic graphic = null;

    protected GameStage activeStage;
    protected boolean initialized = false;
    protected boolean paused = false;
    protected float delta;
    private boolean crashOnErrorInRender;

    public static void waitCoreCreated() {
        Throwable failed = WaitLibrary.waitCoreCreated();
        if (failed != null) {
            throw new GdxRuntimeException("Core thread failed with exception: ", failed);
        }
    }

    @Override
    public void create() {
        Gdxg.glPrimitiveRenderer = new ImmediateModeRenderer20(false, true, 0);
        Gdxg.modelBuilder = new ModelBuilder();
        Gdxg.spriteBatch = new SpriteBatch();
        Gdxg.modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if (WaterShader.isApplicableTo(renderable)) {
                    return new WaterShader(renderable);
                }
                return super.createShader(renderable);
            }
        });
        Gdxg.terrainBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if (TerrainShader.isApplicableTo(renderable)) {
                    return new TerrainShader(renderable);
                }
                throw new GdxRuntimeException("Terrain only!");
            }
        });
        Gdxg.postProcessor = new PostProcessor(true, false, true);

        LOG.info("Initialized.");
    }

    public float getDelta() {
        return delta;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public GameStage getActiveStage() {
        return activeStage;
    }

    public void activateStage(GameStage newStage) {
        LOG.info("< active newStage will be: " + newStage);
        if (activeStage != null) activeStage.onHide();
        activeStage = newStage;
        activeStage.onShow();
        graphic.getClientUi().registerInputProcessors(getActiveStage().getInputProcessors());
        LOG.info("> active newStage was set");
    }

    protected void registerEntitySystems() {
        LOG.info("Register Entity Systems");
        Gdxg.ENTITY_SYSTEMS_ENGINE.addSystem(new FluentPollingSystem());
    }

    @Deprecated
    protected void registerShaders() {
        LOG.info("Register Shaders");
        ShaderLoader.BasePath = Assets.SHADERS_FABULA_FOLDER;
        ShaderProgram.pedantic = false;
        Gdxg.shaders = new ShaderManager();
        Gdxg.shaders.add(SkyBox.SKYBOX_SHADER);
    }

    protected void registerTweenEngine() {
        LOG.info("registerTweenEngine");
        Tween.setCombinedAttributesLimit(4);
        ActorAccessor actorAccessor = new ActorAccessor();
        Node3dAccessor node3dAccessor = new Node3dAccessor();
        Tween.registerAccessor(Window.class, actorAccessor);
        Tween.registerAccessor(ModelActor.class, node3dAccessor);
        Tween.registerAccessor(SceneGroup3d.class, node3dAccessor);
    }

    protected void registerPostProcessor(int screenWidthInPx, int screenHeightInPx) {
        LOG.info("Register Post Processor");
        ShaderLoader.BasePath = Assets.SHADERS_FOLDER;
        Bloom bloom = new Bloom((int) (screenWidthInPx * 0.25f), (int) (screenHeightInPx * 0.25f));
        Gdxg.postProcessor.addEffect(bloom);
    }

    @Override
    public void render() {

        try {
            delta = Gdx.graphics.getDeltaTime();

            if (!paused) {

                ContinuousInput.handle(delta);

                Gdxg.ENTITY_SYSTEMS_ENGINE.update(delta);
                graphic.getCameraController().update(delta);

                if (activeStage != null) {
                    activeStage.act(delta);
                }

                graphic.globalStage.act(delta);
                graphic.getClientUi().stageGUI.act(delta);

                Gdxg.tweenManager.update(delta);

                graphic.draw(activeStage, delta);
            }
        } catch (Throwable throwable) {
            if (crashOnErrorInRender) {
                throw new RuntimeException("Error on render: " + throwable.getMessage(), throwable);
            } else {
                setApplicationCrash(throwable);
                LOG.error(throwable.getMessage(), throwable);
                UiLogger.addErrorLabel(throwable.getMessage());
                Gdxg.clientUi.getInfoDialog().show(throwable.getClass().getSimpleName(), throwable.getMessage());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        graphic.resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        LOG.info("Exit.\n\n");
        graphic.dispose();
    }

    public boolean isCrashOnErrorInRender() {
        return crashOnErrorInRender;
    }

    public void setCrashOnErrorInRender(boolean crashOnErrorInRender) {
        this.crashOnErrorInRender = crashOnErrorInRender;
    }

    public static Throwable getApplicationCrash() {
        return applicationCrash;
    }

    public static void setApplicationCrash(Throwable throwable) {
        applicationCrash = throwable;
    }
}
