package conversion7.scene3dOld;


import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Actions attach to an {@link Actor} and perform some task, often over time.
 *
 * @author Nathan Sweet
 */
abstract public class Action3d implements Poolable {
    /** The actor this action is attached to, or null if it is not attached. */
    protected Actor3d actor3d;

    private Pool<Action3d> pool;

    /**
     * Updates the action based on time. Typically this is called each frame by {@link Actor#act(float)}.
     *
     * @param delta Time in seconds since the last frame.
     * @return true if the action is done. This method may continue to be called after the action is done.
     */
    abstract public boolean act(float delta);

    /** Sets the state of the action so it can be run again. */
    public void restart() {
    }

    /** @return null if the action is not attached to an actor. */
    public Actor3d getActor3d() {
        return actor3d;
    }

    /**
     * Sets the actor this action will be used for. This is called automatically when an action is added to an actor. This is also
     * called with null when an action is removed from an actor. When set to null, if the action has a {@link #setPool(Pool) pool}
     * then the action is {@link Pool#free(Object) returned} to the pool (which calls {@link #reset()}) and the pool is set to null.
     * If the action does not have a pool, {@link #reset()} is not called.
     * <p/>
     * This method is not typically a good place for a subclass to query the actor's state because the action may not be executed
     * for some time, eg it may be {@link DelayAction delayed}. The actor's state is best queried in the first call to
     * {@link #act(float)}. For a {@link TemporalAction}, use TemporalAction#begin().
     */
    public void setActor3d(Actor3d actor3d) {
        this.actor3d = actor3d;
        if (actor3d == null) {
            if (pool != null) {
                pool.free(this);
                pool = null;
            }
        }
    }

    /**
     * Resets the optional state of this action to as if it were newly created, allowing the action to be pooled and reused. State
     * required to be set for every usage of this action or computed during the action does not need to be reset.
     * <p/>
     * The default implementation calls {@link #restart()}.
     * <p/>
     * If a subclass has optional state, it must override this method, call super, and reset the optional state.
     */
    public void reset() {
        restart();
    }

    public Pool<Action3d> getPool() {
        return pool;
    }

    /**
     * Sets the pool that the action will be returned to when removed from the actor.
     *
     * @param pool May be null.
     * @see #setActor3d(Actor)
     */
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public String toString() {
        String name = getClass().getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) name = name.substring(dotIndex + 1);
        if (name.endsWith("Action")) name = name.substring(0, name.length() - 6);
        return name;
    }
}