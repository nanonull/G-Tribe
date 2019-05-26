package conversion7.engine.ai_new.base;

import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.game.stages.world.view.InWorldActionListener;

import static conversion7.engine.artemis.GlobalStrategyAiSystem.COMPLETE_TASK_DEADLINE_MS;

public abstract class AiTask<T extends AiEntity> implements InWorldActionListener {
    public final long deadline;
    public int priority;
    public int expiresInSteps;
    public int stepsAlive;
    public T owner;
    public boolean singleRun = true;
    public boolean completed;
    public long createdAt;
    public boolean globalStrategy;

    public AiTask(T owner) {
        this.owner = owner;
        createdAt = System.currentTimeMillis();
        deadline = createdAt + COMPLETE_TASK_DEADLINE_MS;
    }

    public abstract boolean isValid();

    public AiTask<T> setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    protected void scheduleCompleteAfterAnimation() {
        SchedulingSystem.schedule("scheduleCompleteAfterAnimation", AnimationSystem.ANIM_DURATION_MS,
                () -> {
                    complete();
                });
    }

    public abstract void run();

    @Override
    public void onEvent() {
        complete();
    }

    public void complete() {
        completed = true;
    }
}
