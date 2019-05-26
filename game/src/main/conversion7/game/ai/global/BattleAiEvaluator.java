package conversion7.game.ai.global;

import conversion7.engine.ai_new.base.AiEvaluator;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.artemis.ui.float_lbl.UnitFloatingStatusBatch;
import conversion7.engine.utils.Utils;
import conversion7.game.ai.battle.BattleAiBranch;
import conversion7.game.ai.global.tasks.EscapeTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.PanicEffect;
import conversion7.game.stages.world.unit.effects.items.UnderControlEffect;
import org.slf4j.Logger;

public class BattleAiEvaluator extends AiEvaluator<AbstractSquad> {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static BattleAiEvaluator instance = new BattleAiEvaluator();

    @Override
    protected void evalEntityTasks(AbstractSquad squad) {
        BattleAiBranch.eval(squad);

        if (false) {
            // TODO: 06.05.2019 review control effects AI
            if (UnderControlEffect.isUnderControl(squad)) {
                squad.batchFloatingStatusLines.addImportantLine("Under control");
            }

            if (squad.getEffectManager().containsEffect(PanicEffect.class)) {
                for (Cell cell : squad.getLastCell().getCellsAround()) {
                    if (cell.canBeSeized()) {
                        squad.addAiTask(new EscapeTask(squad.unit, cell));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void runTask(AiTask<AbstractSquad> task) {
        try {
            if (UnitFloatingStatusBatch.SHOW_TASK_LABELS) {
                task.owner.batchFloatingStatusLines.addLine(task.getClass().getSimpleName() + " " + task.priority);
            }
            super.runTask(task);
        } catch (Throwable e) {
            LOG.error(e.getMessage() + " on task " + task.getClass().getSimpleName(), e);
//            Gdxg.core.addError(e);
        }
    }
}
