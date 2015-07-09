package conversion7.engine.customscene;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pool;
import conversion7.engine.customscene.input.CustomEvent;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.CustomInputListenerAbstract;
import conversion7.engine.customscene.input.IntersectResult;
import conversion7.engine.geometry.BoundingBox2;
import conversion7.engine.pools.system.LinkedPoolable;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public abstract class SceneNode3d implements LinkedPoolable {

    private static final Logger LOG = Utils.getLoggerForClass();

    public int id;
    private SceneNode3d parent;
    public CustomStage stage;
    private String name;
    public float frustrumRadius;
    private Pool pool;
    /** Useful for Groups which should handle and return their children touches only */
    boolean doNotReturnMeOnTouch;
    public BoxActor boundBoxActor;
    Vector3 whereIntersected;
    public final DelayedRemovalArray<CustomInputListenerAbstract> listeners = new DelayedRemovalArray(0);
    public final DelayedRemovalArray<CustomInputListenerAbstract> captureListeners = new DelayedRemovalArray(0);
    private boolean visible = true;
    private boolean frustrumVisible = true;

    public SceneNode3d(String name) {
        id = Utils.getNextId();
        this.name = name;
        doNotReturnMeOnTouch = false;
        whereIntersected = new Vector3();
        frustrumRadius = 0;

        localPosition = new Vector3();
        globalPosition = new Vector3();
        localRotation = new Vector3();
        globalRotation = new Vector3();
        localScale = new Vector3(1, 1, 1);
        globalScale = new Vector3();
    }

    protected SceneNode3d() {
        this(null);
    }

    public void setParent(SceneNode3d parent) {
        this.parent = parent;
    }

    public SceneNode3d getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + id + " " + name;
    }

    @Override
    public void linkToPool(Pool pool) {
        this.pool = pool;
    }

    @Override
    public void returnToPool() {
        if (pool != null) {
            pool.free(this);
        }
    }

    @Override
    public void reset() {
        removeFromParent();
    }

    public void act(float delta) {
        calculateFrustrumVisible();
        if (boundBoxActor != null) {
            boundBoxActor.updateWrappedObjects();
        }
    }

    public boolean draw() {
        if (!frustrumVisible || !visible) {
            stage._hiddenNodes++;
            return false;
        }
        stage._drawnNodes++;
        if (boundBoxActor != null) {
            if (stage.isDrawBoundingBoxes()) {
                boundBoxActor.draw();
            }
        }
        updateWorldScale();
        this.updateWrappedObjects();
        return true;
    }

    private void calculateFrustrumVisible() {
        if (parent != null && !parent.isFrustrumVisible()) {
            frustrumVisible = false;
        } else {
            frustrumVisible = frustrumRadius <= 0 || stage.camera.frustum.sphereInFrustum(globalPosition, frustrumRadius);
        }
    }

    // TODO update wrapped object once per change before final draw: add flags: pos, rot, scale updated for calculations only if there were changes

    /** Finally (before rendering), applies world position on real (wrapped) object */
    protected abstract void updateWrappedObjects();

    public void setStage(CustomStage customStage) {
        stage = customStage;
        if (stage != null && boundBoxActor != null) {
            boundBoxActor.stage = stage;
        }
    }

    /** Returns true only if node was removed from parent */
    public boolean removeFromParent() {
        if (parent == null) {
            return false;
        } else {
            return ((SceneGroup3d) parent).removeNode(this);
        }
    }

    public boolean addListener(CustomInputListenerAbstract listener) {
        if (!listeners.contains(listener, false)) {
            listeners.add(listener);
            return true;
        }
        return false;
    }

    public boolean removeListener(CustomInputListenerAbstract listener) {
        return listeners.removeValue(listener, true);
    }

    /**
     * {@link com.badlogic.gdx.scenes.scene2d.Actor#fire}
     */
    public boolean fire(CustomEvent event) {
        if (event.stage == null) event.stage = stage;
        event.targetActor = this;

        // Collect ancestors so event propagation is unaffected by hierarchy changes.
        Array<SceneNode3d> ancestors = PoolManager.ARRAYS_POOL.obtain();
        SceneNode3d parent = this.parent;
        while (parent != null) {
            ancestors.add(parent);
            parent = parent.parent;
        }

        try {
            // Notify all parent capture listeners, starting at the root. Ancestors may stop an event before children receive it.
            for (int i = ancestors.size - 1; i >= 0; i--) {
                SceneNode3d currentTarget = ancestors.get(i);
                currentTarget.notify(event, true);
                if (event.stopped) return event.cancelled;
            }

            // Notify the target capture listeners.
            notify(event, true);
            if (event.stopped) return event.cancelled;

            // Notify the target listeners.
            notify(event, false);
            if (!event.bubbles) return event.cancelled;
            if (event.stopped) return event.cancelled;

            // Notify all parent listeners, starting at the target. Children may stop an event before ancestors receive it.
            for (int i = 0, n = ancestors.size; i < n; i++) {
                ancestors.get(i).notify(event, false);
                if (event.stopped) return event.cancelled;
            }

            return event.cancelled;
        } finally {
            PoolManager.ARRAYS_POOL.free(ancestors);
        }
    }

    /**
     * Notifies this actor's listeners of the event. The event is not propagated to any parents.
     * Before notifying the listeners,
     * this actor is set as the {@link Event#getListenerActor() listener actor}.
     * The event {@link CustomEvent#targetActor}
     * must be set before calling this method.
     * If this actor is not in the stage, the stage must be set before calling this method.
     *
     * @param capture If true, the capture listeners will be notified instead of the regular listeners.
     * @return true of the event was {@link Event#cancel() cancelled}.
     */
    public boolean notify(CustomEvent event, boolean capture) {
        if (event.targetActor == null) throw new IllegalArgumentException("The event target cannot be null.");

        DelayedRemovalArray<CustomInputListenerAbstract> listeners = capture ? captureListeners : this.listeners;
        if (listeners.size == 0) return event.cancelled;

        event.listenerActor = this;
        event.capture = capture;
        if (event.stage == null) event.stage = stage;

        listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            CustomInputListenerAbstract listener = listeners.get(i);
            if (listener.handle(event)) {
                event.handle();
                if (event instanceof CustomInputEvent) {
                    CustomInputEvent inputEvent = (CustomInputEvent) event;
                    if (inputEvent.type == CustomInputEvent.Type.touchDown) {
                        event.stage.addTouchFocus(listener, this, inputEvent.targetActor, inputEvent.pointer,
                                inputEvent.button);
                    }
                }
            }
        }
        listeners.end();

        return event.cancelled;
    }


    // TOUCH

    public void setDoNotReturnMeOnTouch(boolean doNotReturnMeOnTouch) {
        this.doNotReturnMeOnTouch = doNotReturnMeOnTouch;
    }

    public void createBoundingBox(float widthX, float widthY, float height) {
        boundBoxActor = new BoxActor(this, new BoundingBox2(widthX, widthY, height));
        boundBoxActor.stage = stage;
    }

    public void assignBoundingBox(BoundingBox boundingBox) {
        boundBoxActor = new BoxActor(this, new BoundingBox2(boundingBox));
        boundBoxActor.stage = stage;
    }

    /**
     * For box logic refer to {@link CustomStage#touchObjects(int, int)}
     */
    public abstract void hit(Ray pickRay, IntersectResult intersectedGroups, IntersectResult intersectedActors);

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected boolean isFrustrumVisible() {
        return frustrumVisible;
    }

    // ************************************************************************************************************
    // POSITION, ROTATION, SCALE
    // ************************************************************************************************************

    // ************************************************************************************************************
    // 1 - POSITION
    // ************************************************************************************************************

    /**
     * Position inside parent if parent exists<br>
     * otherwise will be equal to world position<p>
     * Engine coords.
     */
    public Vector3 localPosition;
    /**
     * Real position in scene graph
     * Engine coords.
     */
    public Vector3 globalPosition;
    /**
     * X-Y-Z >>> Yaw-Pitch-Roll
     */
    public Vector3 localRotation;
    public Vector3 globalRotation;
    public Vector3 localScale;
    public Vector3 globalScale;

    /**
     * Sets position in engine coordinates<br>
     * Decal: position will represent center
     * ModelInstance: position will represent center also
     */
    public void setPosition(float x, float y, float z) {
        localPosition.x = x;
        localPosition.y = y;
        localPosition.z = z;

        updateWorldPosition();
    }

    public void setX(float x) {
        localPosition.x = x;
        updateWorldPosition();
    }

    public void setY(float y) {
        localPosition.y = y;
        updateWorldPosition();
    }

    public void setZ(float z) {
        localPosition.z = z;
        updateWorldPosition();
    }

    public void setPosition(Vector3 pos) {
        setPosition(pos.x, pos.y, pos.z);
        PoolManager.VECTOR_3_POOL.free(pos);
    }

    public void translate(float x, float y, float z) {
        setPosition(localPosition.x + x, localPosition.y + y, localPosition.z + z);
    }

    public void translate(Vector3 vector3) {
        translate(vector3.x, vector3.y, vector3.z);
    }

    protected void updateWorldPosition() {
        if (parent != null) {
            globalPosition.x = parent.globalPosition.x + localPosition.x;
            globalPosition.y = parent.globalPosition.y + localPosition.y;
            globalPosition.z = parent.globalPosition.z + localPosition.z;
        } else {
            globalPosition.x = localPosition.x;
            globalPosition.y = localPosition.y;
            globalPosition.z = localPosition.z;
        }
    }

    // ************************************************************************************************************
    // 2 - ROTATION
    // ************************************************************************************************************

    /*
     *  Set the actor3d's rotation values to new yaw, pitch and roll
	 *  @param newYaw, newPitch, newRoll these values must be within 360 degrees
	 */
    public void setRotation(float newYaw, float newPitch, float newRoll) {

        localRotation.x = newYaw;
        localRotation.y = newPitch;
        localRotation.z = newRoll;

        updateWorldRotation();
    }


    /*
     *  Rotates the actor3d by the amount of yaw, pitch and roll
	 *  @param amountYaw,amountPitch,amountRoll These values must be within 360 degrees
	 */
    public void rotate(float amountYaw, float amountPitch, float amountRoll) {
        setRotation(normalizeDegrees(localRotation.x + amountYaw),
                normalizeDegrees(localRotation.y + amountPitch),
                normalizeDegrees(localRotation.z + amountRoll));
    }

    protected void updateWorldRotation() {
        if (parent != null) {
            globalRotation.x = parent.globalRotation.x + localRotation.x;
            globalRotation.y = parent.globalRotation.y + localRotation.y;
            globalRotation.z = parent.globalRotation.z + localRotation.z;
        } else {
            globalRotation.x = localRotation.x;
            globalRotation.y = localRotation.y;
            globalRotation.z = localRotation.z;
        }
    }

    // util
    public static float normalizeDegrees(float degrees) {
        float newAngle = degrees;
        while (newAngle < -360) newAngle += 360;
        while (newAngle > 360) newAngle -= 360;
        return newAngle;
    }

    // ************************************************************************************************************
    // 3 - SCALE
    // ************************************************************************************************************

    public void setScale(float scaleX, float scaleY, float scaleZ) {
        localScale.x = scaleX;
        localScale.y = scaleY;
        localScale.z = scaleZ;
    }

    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }

    protected void updateWorldScale() {
        if (parent != null) {
            globalScale.x = parent.globalScale.x * localScale.x;
            globalScale.y = parent.globalScale.y * localScale.y;
            globalScale.z = parent.globalScale.z * localScale.z;
        } else {
            globalScale.x = localScale.x;
            globalScale.y = localScale.y;
            globalScale.z = localScale.z;
        }
    }

    public void scale(float x, float y, float z) {
        setScale(localScale.x + x, localScale.y + y, localScale.z + z);
    }

}