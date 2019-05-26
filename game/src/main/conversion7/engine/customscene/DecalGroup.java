package conversion7.engine.customscene;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.pools.system.PoolManager;

/**
 * Draws DecalBatch with group of associated Decals
 */
public class DecalGroup extends SceneGroup3d {

    private DecalBatch decalBatch;

    public DecalGroup(String name, DecalBatch decalBatch) {
        super(name);
        this.decalBatch = decalBatch;
    }

    public DecalGroup(DecalBatch decalBatch) {
        this(null, decalBatch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public boolean draw() {
        return super.draw();
    }

    @Override
    protected void updateWrappedObjects() {
        super.updateWrappedObjects();
    }

    public void addDecal(DecalActor decalActor) {
        super.addNode(decalActor);
        if (decalActor.decalBatch == null) {
            decalActor.decalBatch = this.decalBatch;
        }
    }

    public boolean removeDecal(DecalActor decalActor) {
        if (super.removeNode(decalActor)) {
            decalActor.decalBatch = null;
            return true;
        }
        return false;
    }

    @Override
    public void clearChildren() {
        for (int i = children.size - 1; i >= 0; i--) {
            removeDecal((DecalActor) children.get(i));
        }
    }

    /** Expensive... */
    public Array<DecalActor> getDecals() {
        Array<DecalActor> decalActors = PoolManager.ARRAYS_POOL.obtain();
        for (SceneNode3d child : children) {
            decalActors.add((DecalActor) child);
        }
        return decalActors;
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        super.hit(pickRay, intersectedGroups, intersectedActors);
    }
}
