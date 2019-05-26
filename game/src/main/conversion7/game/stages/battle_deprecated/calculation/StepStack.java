package conversion7.game.stages.battle_deprecated.calculation;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle_deprecated.Battle;
import org.slf4j.Logger;

import static org.fest.assertions.api.Assertions.assertThat;

public class StepStack implements Runnable {

    private static final Logger LOG = Utils.getLoggerForClass();

    Array<Step> steps = PoolManager.ARRAYS_POOL.obtain();
    Step lastStep;
    Battle battle;
    boolean lastStepHasBeenProceeded = false;
    private int curStep = -1;


    public StepStack(Step startStep) {
        assertThat(startStep).isNotNull();
        if (LOG.isDebugEnabled()) LOG.debug("< create StepStack");
        battle = startStep.battle;
        steps.add(startStep);
        if (LOG.isDebugEnabled()) LOG.debug("> StepStack created");

    }

    public Step getNextStep() {
        if (LOG.isDebugEnabled()) LOG.debug("getNextStep, curStep = " + curStep);

        if (lastStepHasBeenProceeded) {
            Utils.error("Can't get next step - whole stack had been proceeded");
        }

        if (steps.size > curStep + 1) {
            Step nextStep = steps.get(curStep + 1);
            if (LOG.isDebugEnabled()) LOG.debug(" > getNextStep > nextStep = " + nextStep);

            if (nextStep.calculationState.equals(State.COMPLETED)) {

                curStep++;
                if (nextStep.equals(lastStep)) {
                    lastStepHasBeenProceeded = true;
                }
                return nextStep;
            }
        } else {
            if (LOG.isDebugEnabled()) LOG.debug(" next step has not been prepared yet... waiting...");
        }

        return null;
    }

    @Override
    public void run() {
        if (LOG.isDebugEnabled()) LOG.debug("< run StepStack");

        Step step;

        step = steps.get(0);
        step.calculateActions();

        while (!step.isLastStep()) {
            if (LOG.isDebugEnabled()) LOG.debug(" calculate next step based on " + step);
            step = new Step(step);
            step.calculateActions();
            steps.add(step);
        }

        if (LOG.isDebugEnabled()) LOG.debug(" steps in stack:");
        for (Step s : steps) {
            if (LOG.isDebugEnabled()) LOG.debug(s.toString());
        }

        lastStep = step;
        if (LOG.isDebugEnabled()) LOG.debug(" lastStep: " + lastStep);
        if (LOG.isDebugEnabled()) LOG.debug("> StepStack finishing");
    }

}
