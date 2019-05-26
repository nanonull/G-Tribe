package conversion7.engine.artemis;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.ai.global.BattleAiEvaluator;
import conversion7.game.stages.world.WorldBattle;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

import java.util.List;

public class BattleAiSystem extends BaseSystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int MAX_ATTEMPTS_PER_UNIT =
            AbstractSquad.START_ATTACK_AP + AbstractSquad.START_MOVE_AP;
    private static final float INTERVAL = 1 / 60f;

    BattleAiEvaluator unitAiEvaluator = BattleAiEvaluator.instance;
    private AbstractSquad activeSquad;
    private WorldBattle worldBattle;
    private AiTask<AbstractSquad> activeSquadTask;
    private int runActiveSquadAiAttempts;
    private float deltaAcc;

    public boolean isProcessing() {
        return activeSquad != null;
    }

    @Override
    protected void processSystem() {
        deltaAcc += world.getDelta();
        if (deltaAcc < INTERVAL) {
            return;
        }
        deltaAcc -= INTERVAL;

        AbstractSquad activeSquadWip = this.activeSquad;
        if (activeSquadWip == null || AnimationSystem.isLocking()) {
            return;
        }

        activeSquadWip.batchFloatingStatusLines.start();
        runActiveSquadAi();
        activeSquadWip.batchFloatingStatusLines.flush(Color.CYAN);
    }

    private void endActiveSquadAi() {
        activeSquad = null;
        Gdxg.core.artemis.getSystem(BattleSystem.class).nextSquad();
    }

    private void runActiveSquadAi() {
        if (activeSquad.isRemovedFromWorld()
                || !activeSquad.hasAttackAp()
                || !GdxgConstants.BATTLE_AI
                || runActiveSquadAiAttempts > MAX_ATTEMPTS_PER_UNIT) {
            endActiveSquadAi();
            return;
        }

        if (activeSquadTask == null) {
            List<AiTask> aiTasks = null;
            try {
                aiTasks = unitAiEvaluator.findSortedTasks(activeSquad);
            } catch (Throwable error) {
                LOG.error("aiEvaluator.findSortedTasks: " + error.getMessage(), error);
            }

            if (aiTasks == null || aiTasks.size() == 0) {
                endActiveSquadAi();
            } else {
                activeSquadTask = aiTasks.remove(0);
                runActiveSquadAiAttempts++;
                AnimationSystem.lockAnimation();
                unitAiEvaluator.runTask(activeSquadTask);
            }
        } else {
            if (activeSquadTask.deadline < System.currentTimeMillis()) {
                activeSquadTask.complete();
            }
            if (activeSquadTask.completed) {
                endActiveSquadTask();
            }
        }

    }

    private void endActiveSquadTask() {
        activeSquadTask = null;
    }

    public void activateUnitAi(WorldBattle worldBattle) {
        activeSquad = worldBattle.getActiveSquad();
        this.worldBattle = worldBattle;
        runActiveSquadAiAttempts = 0;
    }
}
