package conversion7.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.engine.utils.Waiting;
import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.stages.battle_deprecated.calculation.Round;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.LandscapeGenerator;
import conversion7.game.stages.world.team.Team;
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
                public boolean until() {
                    return landscapeGenerator.isCompleted();
                }
            }).waitUntil(timeout, 100, "LandscapeGenerator has not completed his work!");
        } catch (Exception e) {
            throw new GdxRuntimeException(e);
        }
    }

    public static void waitTillClientCoreInitialized(final ClientCore clientCore) {
        LOG.info("waitTillClientCoreInitialized");
        try {
            (new Waiting() {
                @Override
                public boolean until()  {
                    return clientCore.isInitialized();
                }
            }).waitUntil((int) TimeUnit.MINUTES.toMillis(15), 100, "ClientCore has not been initialized!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitTillNewWorldInitialized(World prevWorld, int timeout) {
        LOG.info("waitTillNewWorldInitialized");
        try {
            (new Waiting() {
                @Override
                public boolean until()  {
                    if (Gdxg.core.world == null
                            || Gdxg.core.world == prevWorld) {
                        return false;
                    }
                    return Gdxg.core.world.initialized;
                }
            }).waitUntil((int) TimeUnit.SECONDS.toMillis(timeout), 100, "World has not been initialized!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitBattleRoundCompleted(final Battle battle) {
        LOG.info("waitBattleRoundCompleted");
        try {
            (new Waiting() {
                @Override
                public boolean until()  {
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
                public boolean until()  {
                    return battle.isCompleted();
                }
            }).waitUntil(1000, 100, "Battle is not completed!");
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public static void waitCoreCreated(ClientCore clientCore) {
        LOG.info("waitCoreCreated {}", clientCore);
        try {
            (new Waiting() {
                float deltaTime = -1;

                @Override
                public boolean until()  {
                    if (clientCore.applicationErrorsOnCurrentTick.size > 0) {
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
            }).waitUntil((int) TimeUnit.MINUTES.toMillis(15), 50, "waitCoreCreated");
        } catch (Exception e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }
    }

    public static void waitWorldHasStartedTeam() {
        try {
            (new Waiting() {
                @Override
                public boolean until()  {
                    return Gdxg.core.world.activeTeam != null;
                }
            }).waitUntil((int) TimeUnit.SECONDS.toMillis(3), 50, "waitWorldHasStartedTeam!");
        } catch (Exception e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }
    }

    public static void waitWorldHasActiveTeam(Team expectedTeam) {
        try {
            (new Waiting() {
                @Override
                public boolean until()  {
                    return Gdxg.core.world.activeTeam == expectedTeam;
                }
            }).waitUntil((int) TimeUnit.SECONDS.toMillis(3), 50, "waitWorldHasActiveTeam!");
        } catch (Exception e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }
    }
}
