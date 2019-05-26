package conversion7.game.ui.world.main_panel;

import com.artemis.BaseSystem;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.unit.Unit;

import java.util.LinkedHashSet;

public class WorldMainWindowSystem extends BaseSystem {
    public static final LinkedHashSet<Unit> updatedObjects = new LinkedHashSet<>();

    @Override
    protected void processSystem() {
        for (Unit updatedObject : updatedObjects) {
            if (Gdxg.clientUi.getWorldMainWindow().loadedCell == updatedObject.squad.getLastCell()) {
                Gdxg.clientUi.getWorldMainWindow().refresh();
                break;
            }
        }
        updatedObjects.clear();
    }
}
