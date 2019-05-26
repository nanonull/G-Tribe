package conversion7.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import conversion7.engine.artemis.engine.AbstractArtemisEngineBuilder;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.WaitLibrary;
import conversion7.game.dialogs.WorldIntroDialog;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.WorldSettings;
import org.slf4j.Logger;

import java.io.File;

public class ClientApplication {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void startLibgdxCore(ClientCore newCore) {
        if (Gdxg.core != null) {
            LOG.info("Core already created " + Gdxg.core);
            if (!Gdxg.core.getClass().equals(newCore.getClass())) {
                throw new RuntimeException("Attempt to create new core class [" + newCore.getClass() + "]. \n" +
                        "Check if it possible to refactor this core: " + newCore);
            }
        } else {
            startLibgdxCoreApp(newCore);
            newCore.waitCreated();
        }
    }

    public static void startGameEngine(WorldSettings worldSettings, AbstractArtemisEngineBuilder artemisEngineBuilder) {
        ClientCore core = Gdxg.core;
        core.acquireCoreLock();
        core.registerArtemisOdbEngine(artemisEngineBuilder.build());
        core.releaseCoreLock();
        WorldServices.waitForNextCoreStep(null);

        if (worldSettings != null) {
            LOG.info("init World From Game Engine");
            if (ClientCore.initWorldFromWorldQuest) {
                LOG.info("wait for init World From Start Quest");
                new WorldIntroDialog(worldSettings).start();
            } else {
                WorldServices.scheduleNewWorld(core, worldSettings);
            }
        }

    }

    public static void startLibgdxCoreApp(ApplicationListener application) {
        LOG.info("startLibgdxCoreApp at thread {}", Thread.currentThread());
        LOG.info("applicationRoot: " + new File("").getAbsolutePath());

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "G-Tribe";
//        cfg.vSyncEnabled = true;
        cfg.width = PropertiesLoader.getIntProperty("SCREEN_WIDTH_IN_PX");
        cfg.height = PropertiesLoader.getIntProperty("SCREEN_HEIGHT_IN_PX");
        cfg.forceExit = false;
//        cfg.fullscreen = true;
        cfg.samples = 4;
        cfg.resizable = false;
//        cfg.useGL30 = true;
//        cfg.foregroundFPS = 60;
//        cfg.backgroundFPS = 60;

        new LwjglApplication(application, cfg);

        Utils.createThreadExceptionHandler((ClientCore) application);
        if (application instanceof ClientCore) {
            WaitLibrary.waitTillClientCoreInitialized((ClientCore) application);
        }
        LOG.info("Application start has been triggered: " + application.getClass().getSimpleName());
    }

}
