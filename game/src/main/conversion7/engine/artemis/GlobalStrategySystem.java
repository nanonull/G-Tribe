package conversion7.engine.artemis;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.audio.TrackAudioSystem;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.world.TeamsTurnsPanel;
import org.slf4j.Logger;

import static java.lang.String.format;

public class GlobalStrategySystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static ComponentMapper<GlobalStrategyComponent> components;
    public long teamActivatedOnCoreFrame;
    ClientCore core;
    private int currentTeamIndex = -1;

    public GlobalStrategySystem() {
        super(Aspect.all(GlobalStrategyComponent.class));
    }

    @Override
    protected void process(int entityId) {
        GlobalStrategyComponent globalStrategyComponent = components.get(entityId);
        World world = core.world;
        if (world.teams.size == 0
                || (world.activeTeam != null
                && world.activeTeam.aiInProgress)) {
            return;
        }
        components.remove(entityId);

        if (world.activeTeam == null) {
            activateNextTeam(world);
        } else {
            world.activeTeam.completeTurn();
            world.activeTeam.onCompleteTurn();
            activateNextTeam(world);
        }

        worldActiveTeamStartsTurn(globalStrategyComponent);
    }

    public void worldActiveTeamStartsTurn(GlobalStrategyComponent globalStrategyComponent) {
        World world = core.world;
        Team activeTeam = core.world.activeTeam;

        LOG.info("worldActiveTeamStartsTurn: {}", activeTeam);
        TeamsTurnsPanel teamsTurnsPanel = Gdxg.clientUi.getTeamsTurnsPanel();
        if (activeTeam.isAiPlayer()) {
            teamsTurnsPanel.newTeamStarted(world);
            teamsTurnsPanel.show();
            teamsTurnsPanel.toFront();
        } else {
            teamsTurnsPanel.hide();
        }
        Gdxg.core.artemis.getSystem(TrackAudioSystem.class).setMaxVol();

        activeTeam.startTurn();
        if (globalStrategyComponent.completeAiTeams
                && activeTeam.isAiPlayer()
                && world.isPlayerTeamAlive()) {
            world.requestNextTeamTurn();
        }
    }

    public void activateNextTeam(World world) {
        while (true) {
            if (world.teams.size == 0) {
                throw new RuntimeException("world.teams.size == 0");
            }

            if (world.activeTeam == null) {
                currentTeamIndex = 0;
                world.beforeNewStepStarts();
            } else {
                currentTeamIndex = world.teams.indexOf(world.activeTeam, true) + 1;
            }

            if (world.teams.size == currentTeamIndex) {
                world.activeTeam = null;
                world.finishStep();
                continue;
            } else if (world.teams.size < currentTeamIndex) {
                throw new RuntimeException(format("world.teams.size [%s] < currentTeamIndex [%s]"
                        , world.teams.size, currentTeamIndex));
            }

            world.activeTeam = world.teams.get(currentTeamIndex);
            if (world.activeTeam.isHumanPlayer()) {
                world.lastActivePlayerTeam = world.activeTeam;
            }
            teamActivatedOnCoreFrame = core.frameId;
            if (world.activeTeam.isDefeated()) {
                LOG.info("skip defeated team {}", world.activeTeam);
            } else {
                return;
            }
        }
    }
}
