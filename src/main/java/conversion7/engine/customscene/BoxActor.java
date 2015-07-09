package conversion7.engine.customscene;

import com.badlogic.gdx.math.collision.Ray;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.geometry.BoundingBox2;
import conversion7.engine.geometry.Drawer3d;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import static org.fest.assertions.api.Assertions.assertThat;

public class BoxActor extends SceneNode3d {

    private static final Logger LOG = Utils.getLoggerForClass();

    public BoundingBox2 boundingBox;

    public BoxActor(BoundingBox2 boundingBox) {
        this(null, boundingBox);
    }

    public BoxActor(SceneNode3d parentNode, BoundingBox2 boundingBox) {
        this.boundingBox = boundingBox;
        this.setParent(parentNode);
    }

    @Override
    public boolean removeFromParent() {
        assertThat(getParent()).isNotNull();
        getParent().boundBoxActor = null;
        setParent(null);
        // TODO boundBoxActor returnToPool
        return true;
    }

    @Override
    protected void updateWrappedObjects() {
        // vectors are updated only on once per draw for box
        updateWorldPosition();
        updateWorldRotation();
        updateWorldScale();

        boundingBox.setPosition(globalPosition.x, globalPosition.y, globalPosition.z);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public boolean draw() {
        stage._drawnNodes++;
        Drawer3d.box(boundingBox);
        return true;
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        Utils.error("not supported! box could be picked only as a child of scene node");
    }
}
