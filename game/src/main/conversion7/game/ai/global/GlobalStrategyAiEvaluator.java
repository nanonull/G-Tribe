package conversion7.game.ai.global;

import conversion7.engine.ai_new.base.AiEvaluator;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.ai.global.branches.*;
import conversion7.game.ai.global.tasks.AnimalMigrationTask;
import conversion7.game.ai.global.tasks.EscapeTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.effects.items.PanicEffect;
import conversion7.game.stages.world.unit.effects.items.UnderControlEffect;
import org.slf4j.Logger;

// TODO: 2019-04-27 AI moves to enemy and creates battle
// leader moves with 5-10 another units to some target
public class GlobalStrategyAiEvaluator extends AiEvaluator<AbstractSquad> {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static GlobalStrategyAiEvaluator instance = new GlobalStrategyAiEvaluator();

    @Override
    protected void evalEntityTasks(AbstractSquad squad) {
        if (squad.isAnimal()) {
            squad.addAiTask(new AnimalMigrationTask((WorldSquad) squad)
                    .setPriority(AiTaskType.ANIMAL_MIGRATION.priority));
            BadCellConditionsAiBranch.eval(squad);
        } else {
            UnitGoalsAiBranch.eval(squad);
            EnemiesAiBranch.eval(squad);
            ExploreAndExpandAiBranch.eval(squad);
            ContactOtherTribesAiBranch.eval(squad);
            FertilizeAiBranch.eval(squad);
        }

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

    @Override
    public void runTask(AiTask<AbstractSquad> task) {
        try {
            if (GdxgConstants.DEVELOPER_MODE) {
                task.owner.batchFloatingStatusLines.addLine(task.getClass().getSimpleName() + " " + task.priority);
            }
            task.globalStrategy = true;
            super.runTask(task);
        } catch (Throwable e) {
            LOG.error(e.getMessage() + " on task " + task.getClass().getSimpleName(), e);
//            Gdxg.core.addError(e);
        }
    }
}
