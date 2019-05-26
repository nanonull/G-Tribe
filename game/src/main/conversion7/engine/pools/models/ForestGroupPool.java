package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.customscene.ModelGroup;
import conversion7.engine.geometry.Modeler;

public class ForestGroupPool extends Pool<ModelGroup> {

    @Override
    protected ModelGroup newObject() {
        ModelGroup groupForest = Modeler.buildModelGroup_forest();
        groupForest.linkToPool(this);
        return groupForest;
    }

}
