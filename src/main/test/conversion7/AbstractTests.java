package conversion7;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.AbstractClientCore;
import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.WaitLibrary;
import conversion7.game.services.WorldServices;
import conversion7.game.utils.collections.IterationRegistrators;
import conversion7.test_steps.WorldSteps;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;

import java.io.File;

public abstract class AbstractTests {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String GAME_THREAD_CRASHED = "Game thread crashed with: ";

    private static boolean clientInitialized;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        if (!clientInitialized) {
            LOG.info("\n" +
                    "==========================================================================================\n" +
                    "\n" +
                    "    START 'Prepare Client for Tests'\n" +
                    "\n" +
                    "==========================================================================================");
            LOG.info("applicationRoot: " + new File("").getAbsolutePath());
            ClientCore.initWorldFromCore = true;
            WorldServices.disableFaunaGeneration();

            ClientApplication.startClientCore();
            GdxgConstants.DEVELOPER_MODE = true;
            WaitLibrary.waitTillWorldInitialized();
            WorldSteps.makePlayerTeamInvincible();
            checkAndResetGameThreadCrash();

            LOG.info("\n" +
                    "==========================================================================================\n" +
                    "\n" +
                    "    'Prepare Client for Tests' COMPLETED\n" +
                    "\n" +
                    "==========================================================================================\n");
            clientInitialized = true;
        }
    }

    public static void checkAndResetGameThreadCrash() {
        Throwable applicationCrash = AbstractClientCore.getApplicationCrash();
        if (applicationCrash != null) {
            AbstractClientCore.setApplicationCrash(null);

            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.reset();

            if (applicationCrash.getMessage().contains("add here message of exception about add sceneObject into sceneGroup during draw")) {
                throw new PossibleNotImportantTestFailure(GAME_THREAD_CRASHED + applicationCrash.getMessage(), applicationCrash);
            }
            throw new GdxRuntimeException(GAME_THREAD_CRASHED + applicationCrash.getMessage(), applicationCrash);
        }
    }
}
