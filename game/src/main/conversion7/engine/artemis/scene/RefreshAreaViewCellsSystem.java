package conversion7.engine.artemis.scene;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.view.AreaViewer;

public class RefreshAreaViewCellsSystem extends IteratingSystem {
     static ComponentMapper<RefreshAreaViewCellsComp> components;

    public RefreshAreaViewCellsSystem() {
        super(Aspect.all(RefreshAreaViewCellsComp.class));
    }

    @Override
    protected void process(int entityId) {
        RefreshAreaViewCellsComp refreshAreaViewCellsComp = components.get(entityId);
        components.remove(entityId);

        AreaViewer viewer = refreshAreaViewCellsComp.viewer;
        viewer.updateViewInProgress = true;

        for (int x = 0; x < viewer.WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < viewer.HEIGHT_IN_AREAS; y++) {
                viewer.views[x][y].refreshArea();
            }
        }
        viewer.updateViewInProgress = false;
    }

    public static void create(AreaViewer areaViewer) {
        RefreshAreaViewCellsComp refreshAreaViewCellsComp = components.create(Gdxg.core.nextEntityId());
        refreshAreaViewCellsComp.viewer = areaViewer;
    }
}