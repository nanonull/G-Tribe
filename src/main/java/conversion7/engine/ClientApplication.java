package conversion7.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.WaitLibrary;
import org.slf4j.Logger;
import org.testng.Assert;

import java.io.File;

public class ClientApplication {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void start(ApplicationListener application) {
        LOG.info("Start!");
        LOG.info("applicationRoot: " + new File("").getAbsolutePath());

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "gdxG";
        cfg.vSyncEnabled = true;
        cfg.width = PropertiesLoader.getIntProperty("SCREEN_WIDTH_IN_PX");
        Assert.assertTrue(cfg.width > 768, "Window minimum width is 768!");
        cfg.height = PropertiesLoader.getIntProperty("SCREEN_HEIGHT_IN_PX");
//        cfg.useGL30 = true;
//        cfg.foregroundFPS = 60;
//        cfg.backgroundFPS = 60;

        new LwjglApplication(application, cfg);

        Utils.createThreadExceptionHandler();

        if (application instanceof ClientCore) {
            WaitLibrary.waitTillClientCoreInitialized((ClientCore) application);
        }
        LOG.info("Application start has been triggered: " + application.getClass().getSimpleName());
    }

    public static void startClientCore() {
        if (ClientCore.core == null || !ClientCore.core.getClass().equals(ClientCore.class)) {
            start(new ClientCore());
        }
    }

}
