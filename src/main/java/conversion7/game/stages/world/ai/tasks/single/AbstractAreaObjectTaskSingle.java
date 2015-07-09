package conversion7.game.stages.world.ai.tasks.single;

import conversion7.game.stages.world.ai.tasks.AbstractAreaObjectTask;
import conversion7.game.stages.world.objects.AreaObject;

import java.util.Comparator;

public abstract class AbstractAreaObjectTaskSingle extends AbstractAreaObjectTask {

    /** 1st is the most priority task (the smallest priority number) */
    public static final Comparator<AbstractAreaObjectTaskSingle> TASK_PRIORITY = new Comparator<AbstractAreaObjectTaskSingle>() {
        @Override
        public int compare(AbstractAreaObjectTaskSingle o1, AbstractAreaObjectTaskSingle o2) {
            return Integer.compare(o1.priority, o2.priority);
        }
    };

    AreaObject owner;

    public AbstractAreaObjectTaskSingle(AreaObject owner, int priority) {
        super(priority);
        this.owner = owner;
    }

    @Override
    public void complete() {
        owner.setCurrentObjectTask(null);
    }

    @Override
    public void cancel() {
        owner.setCurrentObjectTask(null);
    }
}
