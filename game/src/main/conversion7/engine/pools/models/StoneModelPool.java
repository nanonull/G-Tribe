package conversion7.engine.pools.models;

import com.badlogic.gdx.utils.Pool;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.Actor3d;
import conversion7.engine.customscene.ModelActor;
import conversion7.game.Assets;

public class StoneModelPool extends Pool<ModelActor> {

    @Override
    protected ModelActor newObject() {
        Actor3d actor3d = new Actor3d(Assets.getModel("stone"), 0, 0, 0f);
        ModelActor modelActor = new ModelActor("stone", actor3d, Gdxg.modelBatch);
        modelActor.setScale(0.5f);
        modelActor.setEnvironment(Gdxg.graphic.environment);

        modelActor.linkToPool(this);
        return modelActor;
    }

}
