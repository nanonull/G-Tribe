package conversion7.scene3dOld;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;


public class Group3d extends Actor3d {
    private final SnapshotArray<Actor3d> children = new SnapshotArray<Actor3d>(true, 4, Actor3d.class);
    public int visibleCount;

    public Group3d() {
        super();
    }

    public Group3d(Model model) {
        super(model);
    }

    public void act(float delta) {
        super.act(delta);
        Actor3d[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            actors[i].act(delta);
        }
        children.end();
    }

    /**
     * Draws the group and its children. The default implementation calls {@link #applyTransform(Batch, Matrix4)} if needed, then
     * {@link #drawChildren(Batch, float)}, then {@link #resetTransform(Batch)} if needed.
     */
    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {
        super.draw(modelBatch, environment);
        drawChildren(modelBatch, environment);
    }


    public void drawChildren(ModelBatch modelBatch, Environment environment) {
        //modelBatch.render(children, environment); maybe faster
        SnapshotArray<Actor3d> children = this.children;
        Actor3d[] actors = children.begin();
        visibleCount = 0;
        for (int i = 0, n = children.size; i < n; i++) {
            if (actors[i] instanceof Group3d) {
                ((Group3d) actors[i]).drawChildren(modelBatch, environment);
            } else {
                // 1 group params
                float offsetX = x, offsetY = y, offsetZ = z;
                float offsetScaleX = scaleX, offsetScaleY = scaleY, offsetScaleZ = scaleZ;
                float offsetYaw = yaw, offsetPitch = pitch, offsetRoll = roll;

                // 2 reset buffers
                x = 0;
                y = 0;
                z = 0;
                scaleX = 0;
                scaleY = 0;
                scaleZ = 0;
                yaw = 0;
                pitch = 0;
                roll = 0;


                Actor3d child = actors[i];
                if (!child.isVisible()) continue;
                    /*Matrix4 diff = sub(child.getTransform(), getTransform());
					Matrix4 childMatrix = child.getTransform().cpy();
					child.getTransform().set(add(diff, childMatrix));
					child.draw(modelBatch, environment);*/

                // 3 save actor params
                float cx = child.x, cy = child.y, cz = child.z;
                float sx = child.scaleX, sy = child.scaleY, sz = child.scaleZ;
                float ry = child.yaw, rp = child.pitch, rr = child.roll;
                //child.x = cx + offsetX;
                //child.y = cy + offsetY;
                //child.z = cz + offsetZ;

                // 4 affect actor params by parent group
                child.setPosition(cx + offsetX, cy + offsetY, cz + offsetZ);
                child.setScale(sx + offsetScaleX, sy + offsetScaleY, sz + offsetScaleZ);
                child.setRotation(ry + offsetYaw, rp + offsetPitch, rr + offsetRoll);

                // 5 draw with affect by group
                if (child.isCullable(getStage3d().getCamera())) {
                    child.draw(modelBatch, environment);
                    visibleCount++;
                }

                // 6 revert to original actor parameters
                child.x = cx;
                child.y = cy;
                child.z = cz;
                x = offsetX;
                y = offsetY;
                z = offsetZ;
                child.scaleX = sx;
                child.scaleY = sy;
                child.scaleZ = sz;
                scaleX = offsetScaleX;
                scaleY = offsetScaleY;
                scaleZ = offsetScaleZ;
                child.yaw = ry;
                child.pitch = rp;
                child.roll = rr;
                yaw = offsetYaw;
                pitch = offsetPitch;
                roll = offsetRoll;
            }
        }
        children.end();
    }

    /**
     * Adds an actor as a child of this group. The actor is first removed from its parent group, if any.
     *
     * @see #remove()
     */
    public void addActor3d(Actor3d actor3d) {
        actor3d.remove();
        children.add(actor3d);
        actor3d.setParent(this);
        actor3d.setStage3d(getStage3d());
        childrenChanged();
    }

    /**
     * Removes an actor from this group. If the actor will not be used again and has actions, they should be
     * {@link Actor#clearActions3d() cleared} so the actions will be returned to their
     * {@link Action#setPool(com.badlogic.gdx.utils.Pool) pool}, if any. This is not done automatically.
     */
    public boolean removeActor3d(Actor3d actor3d) {
        if (!children.removeValue(actor3d, true)) return false;
        Stage3d stage = getStage3d();
        if (stage != null) stage.unfocus(actor3d);
        actor3d.setParent(null);
        actor3d.setStage3d(null);
        childrenChanged();
        return true;
    }

    /** Called when actors are added to or removed from the group. */
    protected void childrenChanged() {
    }

    /** Removes all actors from this group. */
    public void clearChildren() {
        Actor3d[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            Actor3d child = actors[i];
            child.setStage3d(null);
            child.setParent(null);
        }
        children.end();
        children.clear();
        childrenChanged();
    }

    /** Removes all children, actions, and listeners from this group. */
    public void clear() {
        super.clear();
        clearChildren();
    }

    /** Returns the first actor found with the specified name. Note this recursively compares the name of every actor in the group. */
    public Actor3d findActor(String name) {
        Array<Actor3d> children = this.children;
        for (int i = 0, n = children.size; i < n; i++)
            if (name.equals(children.get(i).getName())) return children.get(i);
        for (int i = 0, n = children.size; i < n; i++) {
            Actor3d child = children.get(i);
            if (child instanceof Group3d) {
                Actor3d actor = ((Group3d) child).findActor(name);
                if (actor != null) return actor;
            }
        }
        return null;
    }

    @Override
    protected void setStage3d(Stage3d stage3d) {
        super.setStage3d(stage3d);
        Array<Actor3d> children = this.children;
        for (int i = 0, n = children.size; i < n; i++)
            children.get(i).setStage3d(stage3d);
    }

    /** Returns an ordered list of child actors in this group. */
    public SnapshotArray<Actor3d> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size > 0;
    }

    /** Prints the actor hierarchy recursively for debugging purposes. */
    public void print() {
        print("");
    }

    private void print(String indent) {
        Actor3d[] actors = children.begin();
        for (int i = 0, n = children.size; i < n; i++) {
            System.out.println(indent + actors[i]);
            if (actors[i] instanceof Group3d) ((Group3d) actors[i]).print(indent + "|  ");
        }
        children.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Actor3d actor3d : children)
            actor3d.dispose();
    }
}
