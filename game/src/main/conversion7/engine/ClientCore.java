package conversion7.engine;

import aurelienribon.tweenengine.Tween;
import com.artemis.AspectSubscriptionManager;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen;
import com.bitfire.utils.ShaderLoader;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.kotcrab.vis.ui.VisUI;
import conversion7.engine.artemis.commons.Aspects;
import conversion7.engine.artemis.engine.AbstractArtemisEngineBuilder;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.customscene.*;
import conversion7.engine.geometry.NoiseCubeShader;
import conversion7.engine.geometry.SkyBox;
import conversion7.engine.geometry.terrain.TerrainShader;
import conversion7.engine.geometry.water.WaterShader;
import conversion7.engine.tween.ActorAccessor;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GameError;
import conversion7.game.GdxgConstants;
import conversion7.game.WaitLibrary;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.stages.GameStage;
import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldSettings;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.inputlisteners.ContinuousInput;
import net.namekdev.entity_tracker.network.EntityTrackerServer;
import net.namekdev.entity_tracker.network.base.Server;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.UUID;
import java.util.concurrent.Semaphore;

public class ClientCore extends Game {

    static final int CLIENT_ENTITY_TRACKER_PORT = Server.DEFAULT_PORT;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int FAILS_MAX = 3;
    public static boolean initWorldFromWorldQuest;
    public final Array<Throwable> applicationErrorsOnCurrentTick = new Array<>();
    public com.artemis.World artemis;
    public TagManager artemisTag;
    public UuidEntityManager artemisUuid;
    public AspectSubscriptionManager artemisSubsription;
    public AreaViewer areaViewer;
    //    public AreaViewer worldViewer;
    public World world;
    public volatile long frameId;
    public volatile boolean freeze;
    protected GameStage activeStage;
    protected boolean initialized = false;
    protected boolean paused = false;
    protected float delta;
    EntityTrackerServerExt entityTrackerServer;
    private boolean systemExitOnErrorInRender;
    private boolean systemExitOnErrorInRenderDone;
    private boolean gdxAppExitOnErrorInRender;
    private boolean gdxAppExitOnErrorInRenderDone;
    private Semaphore coreLock = new Semaphore(1);
    private ClientGraphic graphic;
    private ClientUi clientUi;
    private int fails;

    @Deprecated
    public boolean isBattleActiveStage() {
        return activeStage.getClass().equals(Battle.class);
    }

    public boolean isAreaViewerActiveStage() {
        return activeStage.getClass().equals(AreaViewer.class);
    }

    public boolean isPaused() {
        return paused;
    }

    public float getDelta() {
        return delta;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isSystemExitOnErrorInRender() {
        return systemExitOnErrorInRender;
    }

    public void setSystemExitOnErrorInRender(boolean systemExitOnErrorInRender) {
        this.systemExitOnErrorInRender = systemExitOnErrorInRender;
    }

    public ClientGraphic getGraphic() {
        return graphic;
    }

    public ClientUi getClientUi() {
        return clientUi;
    }

    public boolean isGdxAppExitOnErrorInRender() {
        return gdxAppExitOnErrorInRender;
    }

    public void setGdxAppExitOnErrorInRender(boolean gdxAppExitOnErrorInRender) {
        this.gdxAppExitOnErrorInRender = gdxAppExitOnErrorInRender;
    }

    public boolean isSystemExitOnErrorInRenderDone() {
        return systemExitOnErrorInRenderDone;
    }

    public boolean isGdxAppExitOnErrorInRenderDone() {
        return gdxAppExitOnErrorInRenderDone;
    }

    public GameStage getActiveStage() {
        return activeStage;
    }

    public void waitCreated() {
        WaitLibrary.waitCoreCreated(this);
    }

    @Override
    public void create() {
        Assert.assertNull(Gdxg.core, "Core already created!?");
        Gdxg.core = this;

        Gdxg.glPrimitiveRenderer = new ImmediateModeRenderer20(false, true, 0);
        Gdxg.modelBuilder = new ModelBuilder();
        Gdxg.spriteBatch = new SpriteBatch();
        Gdxg.modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if (WaterShader.isApplicableTo(renderable)) {
                    return new WaterShader(renderable);
                }
                if (NoiseCubeShader.isApplicableTo(renderable)) {
                    return new NoiseCubeShader(renderable);
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

        Assets.loadAll();
        VisUI.load();

        InventoryItemStaticParams.init();
        SkillType.postInit();
        AiTaskType.init();

        registerTweenEngine();
        registerShaders();
        registerPostProcessor(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX);

        Gdxg.graphic = new ClientGraphic(GdxgConstants.SCREEN_WIDTH_IN_PX, GdxgConstants.SCREEN_HEIGHT_IN_PX);
        graphic = Gdxg.graphic;
        Gdxg.clientUi.registerInputProcessors(null);
        clientUi = (ClientUi) graphic.getClientUi();

        LOG.info("Initialized.");
        initialized = true;
    }

    public void createNewWorld(WorldSettings worldSettings) {
        Gdxg.graphic.clearGlobalStageLayers();
        while (world == null || !world.initialized) {
            try {
                world = new World(worldSettings);
                world.init();
//                if (/*world.playerTeam == null*/ /*|| world.playerTeam.isDefeated()*/
//                        /*|| world.playerTeam.getSquads().size == 0*/) {
//                    world = null;
//                }
            } catch (GdxRuntimeException e) {
                AudioPlayer.playFail();
                fails++;
                if (fails > FAILS_MAX) {
                    LOG.error(e.getMessage(), e);
                    System.exit(-42);
                }
            }
        }
//        worldViewer = new WorldViewer(world.getArea(0, 0), this);
        areaViewer = new AreaViewer(world.getArea(0, 0), this);
    }

    public void registerArtemisOdbEngine(AbstractArtemisEngineBuilder artemisEngineBuilder) {
        LOG.info("registerArtemisOdbEngine for {}", this);

        if (artemis != null) {
            artemis.dispose();
        }

        artemis = artemisEngineBuilder.getWorld();
        Assert.assertNotNull(artemis);
        artemisTag = artemisEngineBuilder.getTagManager();
        artemisUuid = artemisEngineBuilder.getUuidEntityManager();
        artemisSubsription = artemisEngineBuilder.getSubscriptionManager();

        Aspects.init(artemisSubsription);

        SchedulingSystem.schedule("1 sec after core init", 1000, () -> {
            LOG.info("1 sec after core init");
        });
    }

    public EntityTrackerServer registerArtemisOdbEntityTracker() {
        if (entityTrackerServer == null) {
            LOG.info("registerArtemisOdbEntityTracker on port {}", ClientCore.CLIENT_ENTITY_TRACKER_PORT);
            entityTrackerServer = new EntityTrackerServerExt(ClientCore.CLIENT_ENTITY_TRACKER_PORT);
            entityTrackerServer.start();
        }
        return entityTrackerServer;
    }

    public void activateStage(GameStage newStage) {
        LOG.info("< active newStage will be: " + newStage);
        if (activeStage != null) activeStage.onHide();
        activeStage = newStage;
        activeStage.onShow();
        Gdxg.graphic.getClientUi().registerInputProcessors(getActiveStage().getInputProcessors());
        LOG.info("> active newStage was set");
    }

    protected void registerEntitySystems() {
        LOG.info("Register Entity Systems");
        throw new RuntimeException("TODO");
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
        Tween.registerAccessor(Window.class, actorAccessor);

        Node3dAccessor node3dAccessor = new Node3dAccessor();
        Tween.registerAccessor(BoxActor.class, node3dAccessor);
        Tween.registerAccessor(DecalActor.class, node3dAccessor);
        Tween.registerAccessor(DecalGroup.class, node3dAccessor);
        Tween.registerAccessor(ModelActor.class, node3dAccessor);
        Tween.registerAccessor(ModelGroup.class, node3dAccessor);
        Tween.registerAccessor(SceneGroup3d.class, node3dAccessor);
        Tween.registerAccessor(SceneNode3d.class, node3dAccessor);
        Tween.registerAccessor(SceneNode3dWith2dActor.class, node3dAccessor);
    }

    protected void registerPostProcessor(int screenWidthInPx, int screenHeightInPx) {
        LOG.info("Register Post Processor");
        ShaderLoader.BasePath = Assets.SHADERS_FOLDER;
        Bloom bloom = new Bloom((int) (screenWidthInPx * 0.3f),
                (int) (screenHeightInPx * 0.3f));
        Gdxg.postProcessor.addEffect(bloom);

        int vpW = Gdx.graphics.getWidth();
        int vpH = Gdx.graphics.getHeight();
        int effects = CrtScreen.Effect.TweakContrast.v
                // light gray tint
//                | CrtScreen.Effect.Vignette.v
                | CrtScreen.Effect.Tint.v
                | CrtScreen.Effect.Scanlines.v
                | CrtScreen.Effect.PhosphorVibrance.v
                // screen glitch line
//                | CrtScreen.Effect.ScanDistortion.v

                ;
        CrtMonitor crtMonitor = new CrtMonitor(vpW, vpH, false, false,
                CrtScreen.RgbMode.ChromaticAberrations, effects);
        Combine combine = crtMonitor.getCombinePass();
        combine.setSource1Intensity(0.05f);
        combine.setSource1Saturation(5f);
        combine.setSource2Intensity(1);
        combine.setSource2Saturation(1.3f);
//        crtMonitor.setZoom(2f);
        Gdxg.postProcessor.addEffect(crtMonitor);
    }

    @Override
    public void render() {
        try {
            frameId = Gdx.graphics.getFrameId();
            LOG.debug("render, frameId {}", frameId);
            acquireCoreLock();

            delta = Gdx.graphics.getDeltaTime();

            if (freeze) {
                LOG.info("freeze on frame {}", frameId);
                Awaitility.await().pollDelay(Duration.ONE_HUNDRED_MILLISECONDS).timeout(Duration.FOREVER)
                        .until(() -> {
                            return !freeze;
                        });
                LOG.info("unfreeze on frame {}", frameId);
            }

            if (!paused) {
                ContinuousInput.handle(delta);

                if (artemis != null) {
                    artemis.setDelta(delta);
                    artemis.process();
                }

                Gdxg.graphic.getCameraController().update(delta);

                if (activeStage != null) {
                    activeStage.act(delta);
                }

                Gdxg.graphic.getGlobalStage().act(delta);
                Gdxg.graphic.getClientUi().stageGUI.act(delta);

                Gdxg.tweenManager.update(delta);

                Gdxg.graphic.draw(activeStage, delta);
            }

        } catch (GameError gameError) {
            addError(gameError);
        } catch (Throwable throwable) {
            addError(throwable);
        } finally {
            releaseCoreLock();
        }

        flushErrors();
    }

    @Override
    public void resize(int width, int height) {
        Gdxg.graphic.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        LOG.info("Exit.\n\n");
        Gdxg.graphic.dispose();
        if (entityTrackerServer != null) {
            entityTrackerServer.stop();
        }
        areaViewer.dispose();
        Assets.dispose();
        // FIXME libgdx thread stucks:
//        AL.destroy();
    }

    public void returnToWorld() {
        Gdxg.core.activateStage(Gdxg.getAreaViewer());
        Gdxg.clientUi.showTeamUi();
    }

    public UUID getUuid(int e) {
        return artemisUuid.getUuid(artemis.getEntity(e));
    }

    public UUID getUuid(Entity e) {
        return artemisUuid.getUuid(e);
    }

    public int nextEntityId() {
        return artemis.create();
    }

    public int getEntityId(UUID uuid) {
        try {
            return artemisUuid.getEntity(uuid).getId();
        } catch (NullPointerException e) {
            throw new GdxRuntimeException("Entity not found: " + uuid, e);
        }
    }

    public Entity getEntity(int entityId) {
        return artemis.getEntity(entityId);
    }

    public void acquireCoreLock() {
        try {
            coreLock.acquire();
        } catch (InterruptedException e) {
            throw new GdxRuntimeException(e);
        }
    }

    public void releaseCoreLock() {
        coreLock.release();
    }

    public void addError(Throwable e) {
        LOG.error(e.getMessage(), e);
        if (GdxgConstants.DEVELOPER_MODE) {
            synchronized (applicationErrorsOnCurrentTick) {
                applicationErrorsOnCurrentTick.add(e);
            }
        }
    }

    public boolean flushErrors() {
        synchronized (applicationErrorsOnCurrentTick) {
            if (applicationErrorsOnCurrentTick.size == 0) {
                return false;
            }

            LOG.error(applicationErrorsOnCurrentTick.size + " error(s) on frame " + frameId);
            for (int i = 0; i < applicationErrorsOnCurrentTick.size; i++) {
                Throwable error = applicationErrorsOnCurrentTick.get(i);
                String errorTitle = "Error#" + i + "  " + error.getClass().getSimpleName() + ": " + error.getMessage();
                UiLogger.addErrorLabel(errorTitle);

                String errorMsg = errorTitle;
                if (error instanceof GameError) {
                    Object errorObject = ((GameError) error).getObjectWithError();
                    if (errorObject != null) {
                        errorMsg += "\n \n ...error was linked to object: \n" + errorObject;
                        if (errorObject instanceof AbstractSquad) {
                            AbstractSquad errorSquad = (AbstractSquad) errorObject;
                            errorMsg += "\n \nerrorObject => AbstractSquad logs: \n" + errorSquad.getSnapshotLogLines();
                        }
                    }
                }
                LOG.error(errorMsg, error);
            }

            if (systemExitOnErrorInRender) {
                LOG.info("systemExitOnErrorInRender");
                systemExitOnErrorInRenderDone = true;
                Gdxg.core = null;
                System.exit(42);
            }
            if (gdxAppExitOnErrorInRender) {
                LOG.info("gdxAppExitOnErrorInRender");
                gdxAppExitOnErrorInRenderDone = true;
                Gdxg.core = null;
                Gdx.app.exit();
            }

            applicationErrorsOnCurrentTick.clear();
            return true;
        }
    }
}
