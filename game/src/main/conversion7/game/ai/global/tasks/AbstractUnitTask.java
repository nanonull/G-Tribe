package conversion7.game.ai.global.tasks;

import conversion7.engine.ai_new.base.AiTask;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public abstract class AbstractUnitTask<T extends AbstractSquad> extends AiTask<T> {
    public AbstractUnitTask(T owner) {
        super(owner);
    }
}
