package conversion7.game;

import com.badlogic.gdx.Gdx;
import conversion7.engine.AbstractClientCore;
import conversion7.engine.ClientCore;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.engine.utils.Waiting;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.battle.calculation.Round;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.LandscapeGenerator;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class WaitLibrary {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void waitTillLandscapeGeneratorFinishes(final LandscapeGenerator landscapeGenerator) {
        LOG.info("waitTillLandscapeGeneratorFinishes");
        int timeout;
        if (GdxgConstants.DEVELOPER_MODE) {
            timeout = Integer.MAX_VALUE;
        } else {
            timeout = PropertiesLoader.getIntProperty("LandscapeGenerator.TIMEOUT_COMPLETED_MS");
        }
        try {
            (new Waiting() {
                @Override
                public boolean until() throws Exception {
                    return landscapeGenerator.isCompleted();
                }
            }).waitUntil(timeout, 100, "LandscapeGenerator has not completed his work!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitTillClientCoreInitialized(final ClientCore clientCore) {
        LOG.info("waitTillClientCoreInitialized");
        try {
            (new Waiting() {
                @Override
                public boolean until() throws Exception {
                    return clientCore.isInitialized();
                }
            }).waitUntil((int) TimeUnit.MINUTES.toMillis(15), 100, "ClientCore has not been initialized!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitTillWorldInitialized() {
        LOG.info("waitTillWorldInitialized");
        try {
            (new Waiting() {
                @Override
                public boolean until() throws Exception {
                    return World.initialized;
                }
            }).waitUntil((int) TimeUnit.MINUTES.toMillis(15), 100, "World has not been initialized!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitBattleRoundCompleted(final Battle battle) {
        LOG.info("waitBattleRoundCompleted");
        try {
            (new Waiting() {
                @Override
                public boolean until() throws Exception {
                    return battle.round.state == Round.State.SET_ARMY;
                }
            }).waitUntil(60000, 100, "Battle Round is not completed!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitBattleCompleted(final Battle battle) {
        LOG.info("waitBattleCompleted");
        try {
            (new Waiting() {
                @Override
                public boolean until() throws Exception {
                    return battle.isCompleted();
                }
            }).waitUntil(1000, 100, "Battle is not completed!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static Throwable waitCoreCreated() {
        LOG.info("waitCoreCreated");
        try {
            (new Waiting() {
                float deltaTime = -1;

                @Override
                public boolean until() throws Exception {
                    if (AbstractClientCore.getApplicationCrash() != null) {
                        // failure found
                        return true;
                    }
                    float newDeltaTime = Gdx.graphics.getDeltaTime();
                    if (deltaTime > 0 && newDeltaTime != deltaTime) {
                        // success found (render in-progress > core created)
                        return true;
                    } else {
                        deltaTime = newDeltaTime;
                    }
                    return false;
                }
            }).waitUntil((int) TimeUnit.MINUTES.toMillis(15), 50, "Test is not completed!");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return AbstractClientCore.getApplicationCrash();
    }
}
