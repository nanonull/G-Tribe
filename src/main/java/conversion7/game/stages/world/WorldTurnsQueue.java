package conversion7.game.stages.world;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;
import org.testng.Assert;

public class WorldTurnsQueue {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static Team startedTeam = null;
    private static Team teamInAct = null;
    private int currentTeamIndex = -1;

    public WorldTurnsQueue() {
        LOG.info("run");
        askNextTeamTurn();
    }

    public void queueLoop() {
        // complete team act before start next team
        if (teamInAct != null && teamInAct.isAiPlayer()) {
            if (askCurrentTeamCompleteTurn()) {
                askNextTeamTurn();
            }
        }

        if (startedTeam != null) {
            Assert.assertNull(teamInAct);
            startedTeam.startTurn();
            teamInAct = startedTeam;
            startedTeam = null;
        }
    }

    public synchronized void askNextTeamTurn() {
        LOG.info("askNextTeamTurn");
        if (teamInAct != null && teamInAct.isHumanPlayer()) {
            // handle here re-call of turn completion when humanPlayer team will support tasks execution
            // Save team act progress (as for AI) and call this method again automatically after battle or other stage
            askCurrentTeamCompleteTurn();
        }

        currentTeamIndex++;
        if (World.TEAMS.size == currentTeamIndex) {
            World.finishStep();
            currentTeamIndex = 0;
        }

        startedTeam = World.TEAMS.get(currentTeamIndex);
    }

    /** Returns true if Current Team fully completed its turn */
    private boolean askCurrentTeamCompleteTurn() {
        if (teamInAct.completeTurn()) {
            teamInAct = null;
            return true;
        }
        return false;
    }

}
