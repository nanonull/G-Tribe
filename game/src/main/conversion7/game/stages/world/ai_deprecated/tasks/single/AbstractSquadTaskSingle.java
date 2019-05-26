package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.game.stages.world.ai_deprecated.tasks.AbstractSquadTask;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.Comparator;

public abstract class AbstractSquadTaskSingle extends AbstractSquadTask {

    /** 1st is the most priority task (the smallest priority number) */
    public static final Comparator<AbstractSquadTaskSingle> TASK_PRIORITY = new Comparator<AbstractSquadTaskSingle>() {
        @Override
        public int compare(AbstractSquadTaskSingle o1, AbstractSquadTaskSingle o2) {
            return Integer.compare(o1.priority, o2.priority);
        }
    };

    protected AbstractSquad owner;

    public AbstractSquadTaskSingle(AbstractSquad owner) {
        super();
        this.owner = owner;
    }

    public abstract boolean execute();

    @Override
    public void complete() {
        owner.setActiveTask(null);
    }

    @Override
    public void cancel() {
        owner.setActiveTask(null);
    }
}
