package conversion7.engine.customscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class SceneGroup3d extends SceneNode3d {

    private static final Logger LOG = Utils.getLoggerForClass();

    public SnapshotArray<SceneNode3d> children = new SnapshotArray(SceneNode3d.class);
    private SceneNode3d[] childrenSnapshot;

    // group sizes
    public float widthX;
    public float widthY;
    public float height;
    private long childrenSnapshotFrame = -1;

    public SceneGroup3d() {
        super();
    }

    public SceneGroup3d(String name) {
        super(name);
    }

    @Override
    public void setStage(CustomStage customStage) {
        super.setStage(customStage);
        for (SceneNode3d child : children) {
            child.setStage(customStage);
        }
    }

    /** Use if you need pick inner group */
    @Override
    public void setDoNotReturnMeOnTouch(boolean doNotReturnMeOnTouch) {
        super.setDoNotReturnMeOnTouch(doNotReturnMeOnTouch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (SceneNode3d child : getChildrenSnapshot()) {
            child.act(delta);
        }
    }

    public Array<SceneNode3d> getChildrenSnapshot() {
        if (Gdx.graphics.getFrameId() != childrenSnapshotFrame) {
            childrenSnapshotFrame = Gdx.graphics.getFrameId();
            childrenSnapshot = children.begin();
        }
        return getChildren();
    }

    public Array<SceneNode3d> getChildren() {
        return children;
    }

    @Override
    public boolean draw() {
        boolean drawn = false;
        if (super.draw()) {
            for (SceneNode3d child : getChildrenSnapshot()) {
                child.draw();
            }
            drawn = true;
            children.end();
        }
        return drawn;
    }

    @Override
    protected void updateWrappedObjects() {

    }

    public void addNode(SceneNode3d node) {
        if (node.equals(this)) {
            throw new SceneException("Attempt to add itself!");
        }
        if (children.contains(node, true)) {
            throw new SceneException("Node already added!");
        }
        children.add(node);
        node.setParent(this);
        node.setStage(this.stage);

        node.updateWorldPosition();
    }

    public void insert(int index, SceneNode3d node) {
        if (node.equals(this)) {
            throw new SceneException("Attempt to add itself!");
        }
        if (children.contains(node, true)) {
            throw new SceneException("Node already added!");
        }
        children.insert(index, node);
        node.setParent(this);
        node.setStage(this.stage);

        node.updateWorldPosition();
    }

    public void clearChildren() {
        for (int i = children.size - 1; i >= 0; i--) {
            removeNode(children.get(i));
        }
    }

    /** Returns true only if node was removed */
    public boolean removeNode(SceneNode3d node) {
        if (children.removeValue(node, false)) {
            node.setParent(null);
            node.updateWorldPosition();
            return true;
        }
        throw new SceneException("is absent");
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        if (boundBoxActor == null) {
            // skip me and all my children (even if they have boxes!)
            return;
        } else {
            if (Intersector.intersectRayBounds(pickRay, boundBoxActor.boundingBox.wrappedBox, whereIntersected)) {
                if (!doNotReturnMeOnTouch) {
                    intersectedGroups.addNode(this, whereIntersected);
                }
                for (SceneNode3d child : getChildrenSnapshot()) {
                    child.hit(pickRay, intersectedGroups, intersectedActors);
                }
            }
        }
    }

    public void setDimensions(float widthX, float widthY, float height) {
        this.widthX = widthX;
        this.widthY = widthY;
        this.height = height;
    }

    public void createBoundingBox() {
        createBoundingBox(widthX, widthY, height);
    }

    @Override
    protected void updateWorldPosition() {
        super.updateWorldPosition();
        for (SceneNode3d child : children) {
            child.updateWorldPosition();
        }
    }

    @Override
    protected void updateWorldRotation() {
        super.updateWorldRotation();
        for (SceneNode3d child : children) {
            child.updateWorldRotation();
        }
    }

    @Override
    protected void updateWorldScale() {
        super.updateWorldScale();
        for (SceneNode3d child : children) {
            child.updateWorldScale();
        }
    }

    @Override
    public void dispose() {
        for (SceneNode3d child : children) {
            child.dispose();
        }
    }
}
