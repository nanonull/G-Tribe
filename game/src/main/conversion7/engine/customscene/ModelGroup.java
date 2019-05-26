package conversion7.engine.customscene;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.pools.system.PoolManager;

/**
 * Draws ModelBatch with group of associated ModelInstances
 */
public class ModelGroup extends SceneGroup3d {

    private ModelBatch modelBatch;

    public ModelGroup(ModelBatch modelBatch) {
        this(null, modelBatch);
    }

    public ModelGroup(String name, ModelBatch modelBatch) {
        super(name);
        this.modelBatch = modelBatch;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    /** Important: call modelBatch.end in the end of render method to draw all children */
    @Override
    public boolean draw() {
        return super.draw();
    }

    @Override
    protected void updateWrappedObjects() {
        super.updateWrappedObjects();
    }

    public void addModel(ModelActor modelActor) {
        super.addNode(modelActor);
        modelActor.modelBatch = this.modelBatch;
    }

    public boolean removeModel(ModelActor modelActor) {
        if (super.removeNode(modelActor)) {
            modelActor.modelBatch = null;
            return true;
        }
        return false;
    }

    @Override
    public void clearChildren() {
        for (int i = children.size - 1; i >= 0; i--) {
            removeModel((ModelActor) children.get(i));
        }
    }

    /** Expensive... */
    public Array<ModelActor> getModels() {
        Array<ModelActor> modelActors = PoolManager.ARRAYS_POOL.obtain();
        for (SceneNode3d child : children) {
            modelActors.add((ModelActor) child);
        }
        return modelActors;
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        super.hit(pickRay, intersectedGroups, intersectedActors);
    }
}
