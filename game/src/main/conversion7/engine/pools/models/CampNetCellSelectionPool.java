package conversion7.engine.pools.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.FlushablePool;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.geometry.Modeler;

public class CampNetCellSelectionPool extends FlushablePool<SceneNode3d> {

    @Override
    protected SceneNode3d newObject() {
        ModelActor actor = Modeler.buildCellSelector(Color.WHITE,
                0.25f, 0.9f, Modeler.LEVEL0_SELECTION_ALPHA);
        return actor;
    }

}
