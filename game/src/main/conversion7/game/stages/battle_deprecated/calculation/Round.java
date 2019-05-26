package conversion7.game.stages.battle_deprecated.calculation;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.TimeFI;
import conversion7.engine.utils.Utils;
import conversion7.game.BattleConstants;
import conversion7.game.interfaces.Progressive;
import conversion7.game.stages.battle_deprecated.Battle;
import conversion7.game.stages.battle_deprecated.TeamSide;
import org.slf4j.Logger;

public class Round implements Progressive {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final TimeFI STEP_LENGTH = new TimeFI(750);
    /**
     * Normal speed = 1; <br>
     * Double speed = 2...
     */
    public static final float ROUND_SPEED = 1;
    public static final boolean _MANUAL_STEP_DEBUG = false;
    private static float STEP_MIDDLE = STEP_LENGTH.getFloatSeconds() / 2f;
    Battle battle = null;
    public Step startStep;
    public Step currentActionStep = null;
    public State state = State.SET_ARMY;
    /** Starts from 1 */
    public int version = 1;
    StepStack stepStack;

    public Round(Battle b) {
        LOG.info("< create round");
        battle = b;
        startStep = new Step(battle);
        startStep.round = version;
        LOG.info("> round created");
    }

    @Override
    public void start() {
        LOG.info("start");
        stepStack = new StepStack(startStep);
        Thread t = new Thread(stepStack);
        t.start();
    }

    @Override
    public void act(float delta) {

        if (!state.equals(State.ACTION_IN_PROCESS)) {
            return;
        }

        float roundDelta = delta * ROUND_SPEED;

        if (currentActionStep == null) {
            currentActionStep = stepStack.getNextStep();

            if (currentActionStep == null) {
                LOG.info("wait till next step will be calculated");
                return;
            }

            if (currentActionStep.isLastStep()) {
                complete(currentActionStep);
                return;
            }
        }

        if (currentActionStep.progress == 0) {
            currentActionStep.start();
        }

        currentActionStep.progress += roundDelta;
        if (LOG.isDebugEnabled()) LOG.debug(" active step: " + currentActionStep);
        if (LOG.isDebugEnabled())
            LOG.debug("# roundDelta = " + roundDelta + " # step.progress = " + currentActionStep.progress);

        if (currentActionStep.progress >= STEP_MIDDLE && !currentActionStep.halfCompleted) {
            currentActionStep.completeHalf();
            return;
        }

        if (currentActionStep.progress >= STEP_LENGTH.getFloatSeconds()) {
            currentActionStep.complete();
            currentActionStep = null;

            if (Round._MANUAL_STEP_DEBUG) {
                state = State.PAUSE_DURING_ACTION;
            }

        }

    }

    @Override
    public void completeHalf() {
        Utils.error("is not supported");
    }

    @Override
    public void complete() {
        Utils.error("is not supported");
    }

    public void complete(Step lastStep) {
        LOG.info("# ROUND FINISHED " + version);

        state = State.SET_ARMY;
        startStep = lastStep;
        currentActionStep = null;

        invertSides();

        if (battle.validateWinner()) {
            battle.finish();
        } else {
            version++;
            LOG.info("battle could be continued...next round: " + version);
            startStep.noActiveFigures = false;
            startStep.round = version;
            battle.calculateTeamsInBattle();

            Gdxg.clientUi.getBattleWindowManageArmyForRound().refresh();
        }
    }

    public void invertSides() {
        LOG.info("invert sides");
        for (TeamSide teamSide : battle.teamSides) {
            teamSide.setBattleSide(BattleConstants.INVERT_TEAM.get(teamSide.getBattleSide()));
        }
    }

    public enum State {
        SET_ARMY(),
        PAUSE_DURING_ACTION(),
        ACTION_IN_PROCESS();
    }

}
