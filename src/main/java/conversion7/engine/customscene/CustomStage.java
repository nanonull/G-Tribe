package conversion7.engine.customscene;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.CustomInputListenerAbstract;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class CustomStage extends InputAdapter {

    private static final Logger LOG = Utils.getLoggerForClass();

    public final SceneGroup3d root;
    private String name;
    public PerspectiveCamera camera;

    /**
     * Point where mouse-ray intersects with the closest appropriate object's box<br>
     * for more info refer to touchObjects
     */
    public Vector3 mouseOnStage = PoolManager.VECTOR_3_POOL.obtain();

    private final SnapshotArray<TouchFocus> touchFocuses = new SnapshotArray(true, 4, TouchFocus.class);
    private boolean drawBoundingBoxes;
    public int _drawnNodes;
    public int _hiddenNodes;
    public int _skipAnimations;
    public int _actAnimations;

    public CustomStage(String name, PerspectiveCamera camera) {
        this.name = name;
        this.camera = camera;
        this.root = new SceneGroup3d();
        root.setStage(this);
    }

    public boolean isDrawBoundingBoxes() {
        return drawBoundingBoxes;
    }

    public void setDrawBoundingBoxes(boolean drawBoundingBoxes) {
        this.drawBoundingBoxes = drawBoundingBoxes;
    }

    public void addNode(SceneNode3d node3d) {
        root.addNode(node3d);
    }

    public void removeNode(SceneNode3d node3d) {
        root.removeNode(node3d);
    }

    public boolean addListener(CustomInputListenerAbstract listener) {
        return root.addListener(listener);
    }

    public boolean removeListener(CustomInputListenerAbstract listener) {
        return root.removeListener(listener);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode() + " " + name;
    }

    public void act(float delta) {
        root.act(delta);
    }

    public void draw() {
        root.draw();

//        LOG.info(String.format("stage %s: _drawnNodes=%s _hiddenNodes=%s _skipAnimations=%s _actAnimations=%s",
//                name, _drawnNodes, _hiddenNodes, _skipAnimations, _actAnimations));
//        _drawnNodes = 0;
//        _hiddenNodes = 0;
//        _skipAnimations = 0;
//        _actAnimations = 0;
    }

    /**
     * Adds the listener to be notified for all touchDragged and touchUp events for the specified pointer and button.
     * The actor will be used as the {@link com.badlogic.gdx.scenes.scene2d.Event#getListenerActor() listener actor}
     * and {@link com.badlogic.gdx.scenes.scene2d.Event#getTarget() target}.
     */
    public void addTouchFocus(CustomInputListenerAbstract listener, SceneNode3d listenerActor, SceneNode3d target,
                              int pointer, int button) {
        TouchFocus focus = Pools.obtain(TouchFocus.class);
        focus.listenerActor = listenerActor;
        focus.target = target;
        focus.listener = listener;
        focus.pointer = pointer;
        focus.button = button;
        touchFocuses.add(focus);
    }

    public static final class TouchFocus implements Pool.Poolable {
        CustomInputListenerAbstract listener;
        SceneNode3d listenerActor, target;
        int pointer, button;

        @Override
        public void reset() {
            listenerActor = null;
            listener = null;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (LOG.isDebugEnabled()) LOG.debug("keyDown in " + getClass().getSimpleName());
        return super.keyDown(keycode);
    }

    public SceneNode3d mouseOverActor;

    public Vector3 getIntersectionPoint() {
        return mouseOverActor == null ? null : mouseOverActor.whereIntersected;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseOverActor = touchObjects(screenX, screenY);

        if (mouseOverActor != null) {
            if (LOG.isDebugEnabled()) LOG.debug("mouse over: " + mouseOverActor.toString());

            CustomInputEvent event = Pools.obtain(CustomInputEvent.class);
            event.type = CustomInputEvent.Type.mouseMoved;
            event.stage = this;
            event.whereIntersected = getIntersectionPoint();

            mouseOverActor.fire(event);
            boolean handled = event.handled;
            Pools.free(event);
            return handled;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        mouseOverActor = touchObjects(screenX, screenY);

        if (mouseOverActor != null) {
            if (LOG.isDebugEnabled()) LOG.debug("finally TOUCH-DOWN: " + mouseOverActor.toString());

            CustomInputEvent event = Pools.obtain(CustomInputEvent.class);
            event.type = CustomInputEvent.Type.touchDown;
            event.stage = this;
            event.pointer = pointer;
            event.button = button;
            event.whereIntersected = getIntersectionPoint();

            mouseOverActor.fire(event);
            boolean handled = event.handled;
            Pools.free(event);
            return handled;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        mouseOverActor = touchObjects(screenX, screenY);

        if (mouseOverActor != null) {
            if (LOG.isDebugEnabled()) LOG.debug("finally TOUCH-UP: " + mouseOverActor.toString());

            CustomInputEvent event = Pools.obtain(CustomInputEvent.class);
            event.type = CustomInputEvent.Type.touchUp;
            event.stage = this;
            event.pointer = pointer;
            event.button = button;
            event.whereIntersected = getIntersectionPoint();

            mouseOverActor.fire(event);
            boolean handled = event.handled;
            Pools.free(event);
            return handled;
        }
        return false;
    }

    private IntersectResult intersectedGroupsWip = new IntersectResult();
    private IntersectResult intersectedActorsWip = new IntersectResult();

    /**
     * Returns the closest actor <br>
     * or if was not touched - the closest group<br>
     * or if was not touched - null.<br><br>
     * Note: method supposes actors' boxes don't out bounds of parent group box, so<br>
     * if actor's box goes out of group bounds it will not be touched!
     */
    private SceneNode3d touchObjects(int screenX, int screenY) {
        Ray pickRay = camera.getPickRay(screenX, screenY);

        // collect lists: picked groups and all objects picked from these groups :)
        intersectedGroupsWip.clear();
        intersectedActorsWip.clear();
        for (SceneNode3d child : root.getChildrenSnapshot()) {
            child.hit(pickRay, intersectedGroupsWip, intersectedActorsWip);
        }

        if (intersectedGroupsWip.pickedNodesWithDistance.size == 0) {
            // no groups were touched, so actors are skipped
            return null;
        } else {
            intersectedGroupsWip.sortNodes();
            SceneGroup3d topGroup = (SceneGroup3d) intersectedGroupsWip.pickedNodesWithDistance.get(0).getKey();
            if (LOG.isDebugEnabled()) LOG.debug("\ntopGroup: " + topGroup.toString());
            if (intersectedActorsWip.pickedNodesWithDistance.size == 0) {
                return topGroup;
            } else {
                intersectedActorsWip.sortNodes();
                SceneNode3d topActor = intersectedActorsWip.pickedNodesWithDistance.get(0).getKey();
                if (LOG.isDebugEnabled()) LOG.debug("topActor: " + topActor.toString());
                return topActor;
            }

        }
    }

}
