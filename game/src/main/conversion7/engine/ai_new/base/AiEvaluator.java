package conversion7.engine.ai_new.base;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AiEvaluator<T extends AiEntity> {

    private static final Comparator<? super AiTask> AI_TASKS_COMPARATOR = (o1, o2) ->
            Integer.compare(o2.priority, o1.priority);

    public List<AiTask> findSortedTasks(T entity) {
        List<AiTask> aiTasks;
        evalEntityTasks(entity);
        aiTasks = entity.getAiTasks();
        aiTasks.sort(AI_TASKS_COMPARATOR);
        preProcessTasks(aiTasks);

        Iterator<AiTask> aiTaskIterator = aiTasks.iterator();
        while (aiTaskIterator.hasNext()) {
            AiTask aiTask = aiTaskIterator.next();
            if (!aiTask.isValid()) {
                aiTaskIterator.remove();
            }
        }

        return aiTasks;
    }

    protected void preProcessTasks(List<AiTask> aiTasks) {
    }

    public void runTask(AiTask<T> task) {
        task.run();
    }

    public void validateExpiration(List<AiTask> aiTasks) {
        if (aiTasks != null) {
            Iterator<AiTask> iterator = aiTasks.iterator();
            while (iterator.hasNext()) {
                AiTask next = iterator.next();
                next.stepsAlive++;
                if (next.stepsAlive >= next.expiresInSteps) {
                    iterator.remove();
                }
            }
        }
    }

    protected abstract void evalEntityTasks(T entity);
}
