package conversion7.game.ai.global.tasks;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class DummyUnitTask extends AbstractUnitTask<AbstractSquad> {


    public DummyUnitTask(AbstractSquad owner) {
        super(owner);
    }

    @Override
    public boolean isValid() {
        return owner.isAlive();
    }

    @Override
    public void run() {
        scheduleCompleteAfterAnimation();
    }

}
