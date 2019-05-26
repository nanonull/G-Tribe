package conversion7.game.stages.world.ai_deprecated.tasks.group;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.ai_deprecated.tasks.AbstractSquadTask;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public abstract class AbstractSquadTaskGroup extends AbstractSquadTask {

    protected AiTeamControllerOld aiTeamControllerOld;
    Array<? extends AbstractSquad> actors;

    public AbstractSquadTaskGroup(AiTeamControllerOld aiTeamControllerOld) {
        super();
        this.aiTeamControllerOld = aiTeamControllerOld;
    }

    public void refreshActors(Array<? extends AbstractSquad> areaObjects) {
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
