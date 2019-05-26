package conversion7.engine.customscene;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import conversion7.engine.customscene.input.IntersectResult;
import org.testng.Assert;

/**
 * Wraps 2d actors into 3d actors.<br>
 * Example: sprites which always look into camera
 */
public class SceneNode3dWith2dActor extends SceneNode3d {

    Vector3 vector3Wip = new Vector3();

    private Actor actor;

    public SceneNode3dWith2dActor(Actor actor) {
        this.actor = actor;
        Assert.assertNotNull(actor);
    }

    public Actor getActor() {
        return actor;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        actor.setVisible(visible);
    }

    @Override
    protected void updateWrappedObjects() {
        vector3Wip.set(globalPosition);
        stage.getCamera().project(vector3Wip);
        actor.setPosition(vector3Wip.x, vector3Wip.y);
    }

    @Override
    public void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors) {
        // not supported for 2d scene actors
    }

    @Override
    public boolean removeFromParent() {
        boolean removeFromParent = super.removeFromParent();
        actor.remove();
        return removeFromParent;
    }
}
