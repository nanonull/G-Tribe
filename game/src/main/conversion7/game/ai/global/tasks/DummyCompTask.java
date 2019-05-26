package conversion7.game.ai.global.tasks;

import conversion7.engine.ai_new.base.AiTask;
import conversion7.game.stages.world.objects.composite.CompositeAreaObject;

public class DummyCompTask extends AiTask<CompositeAreaObject> {


    public DummyCompTask(CompositeAreaObject owner) {
        super(owner);
    }

    @Override
    public boolean isValid() {
        return owner.isActive();
    }

    @Override
    public void run() {
        scheduleCompleteAfterAnimation();
    }

}
