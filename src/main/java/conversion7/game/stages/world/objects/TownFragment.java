package conversion7.game.stages.world.objects;

import com.badlogic.gdx.math.collision.BoundingBox;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class TownFragment extends AreaObject {

    private static final Logger LOG = Utils.getLoggerForClass();

    public TownFragment(Cell cell, Team team) {
        super(cell, team);
        initActions();
    }

    @Override
    public void buildModelActor() {
        ModelActor modelActor = PoolManager.TOWN_FRAGMENT_MODEL_POOL.obtain();
        sceneGroup.addNode(modelActor);
        this.setModelActor(modelActor);
        BoundingBox boundingBox = new BoundingBox();
        modelActor.modelInstance.calculateBoundingBox(boundingBox);
        sceneGroup.assignBoundingBox(boundingBox);
    }

    @Override
    public void customObjectAi() {
        LOG.warn("customObjectAi is not supported yet");
    }

    @Override
    public boolean validateAndDefeat() {
        return false;
    }

    @Override
    public void validateReadyRangeUnits() {
        LOG.warn("Remove when UnitsController will be only on Squad!");
    }

    @Override
    public boolean couldJoinToTeam(AreaObject targetToBeJoined) {
        return false;
    }
}
