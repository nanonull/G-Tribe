package conversion7.game.stages.world.ai.tasks;

import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.Cancelable;
import conversion7.game.interfaces.Completable;
import conversion7.game.interfaces.Executable;

public abstract class AbstractAreaObjectTask implements Completable, Cancelable, Executable {

    public int priority;
    protected int id;

    public AbstractAreaObjectTask(int priority) {
        this.priority = priority;
        this.id = Utils.getNextId();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return toString();
    }
}
