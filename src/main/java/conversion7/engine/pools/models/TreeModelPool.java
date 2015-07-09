package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;

public class TreeModelPool extends Pool<ModelActor> {

    @Override
    protected ModelActor newObject() {
        ModelActor modelActorTree = Modeler.buildModelActor_tree();
        modelActorTree.linkToPool(this);
        return modelActorTree;
    }

}
