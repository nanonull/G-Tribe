package conversion7.game.stages.world.ai.tasks.group;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.ai.tasks.AbstractAreaObjectTask;
import conversion7.game.stages.world.objects.AreaObject;

public abstract class AbstractAreaObjectTaskGroup extends AbstractAreaObjectTask {

    Array<? extends AreaObject> actors;
    protected AiTeamController aiTeamController;

    public AbstractAreaObjectTaskGroup(AiTeamController aiTeamController, int priority) {
        super(priority);
        this.aiTeamController = aiTeamController;
    }

    public void refreshActors(Array<? extends AreaObject> areaObjects) {
        actors = areaObjects;
    }

    @Override
    public void cancel() {
        if (actors != null) {
            PoolManager.ARRAYS_POOL.free(actors);
        }
    }

    @Override
    public void complete() {
        if (actors != null) {
            PoolManager.ARRAYS_POOL.free(actors);
        }
    }
}
