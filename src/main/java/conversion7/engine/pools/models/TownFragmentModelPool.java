package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.geometry.Modeler;

public class TownFragmentModelPool extends Pool<ModelActor> {

    @Override
    protected ModelActor newObject() {
        ModelActor modelActor = new ModelActor("town", Modeler.buildBlueBox(), Gdxg.modelBatch);
        modelActor.setEnvironment(Gdxg.graphic.environment);
        modelActor.linkToPool(this);
        return modelActor;
    }

}
