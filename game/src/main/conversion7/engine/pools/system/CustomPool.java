package conversion7.engine.pools.system;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** With check if freeObjects already contains object going to be free */
abstract public class CustomPool<T> {
    /** The maximum number of objects that will be pooled. */
    public final int max;
    /** The highest number of free objects. Can be reset any time. */
    public int peak;

    private final Array<T> freeObjects;

    /** Creates a pool with an initial capacity of 16 and no maximum. */
    public CustomPool() {
        this(16, Integer.MAX_VALUE);
    }

    /** Creates a pool with the specified initial capacity and no maximum. */
    public CustomPool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /** @param max The maximum number of free objects to store in this pool. */
    public CustomPool(int initialCapacity, int max) {
        freeObjects = new Array(false, initialCapacity);
        this.max = max;
    }

    abstract protected T newObject();

    /**
     * Returns an object from this pool. The object may be new (from {@link #newObject()}) or reused (previously
     * {@link #free(Object) freed}).
     */
    public T obtain() {
        if (freeObjects.size == 0) {
            return newObject();
        } else {
            return freeObjects.pop();
        }
    }

    /**
     * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}. If the pool already contains
     * {@link #max} free objects, the specified object is reset but not added to the pool.
     */
    public void free(T object) {
        if (object == null) throw new IllegalArgumentException("object cannot be null.");
        if (freeObjects.contains(object, true)) throw new GdxRuntimeException("Object is already in pool: " + object);
        if (freeObjects.size < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size);
        }
        if (object instanceof Poolable) ((Poolable) object).reset();
    }

    /**
     * Puts the specified objects in the pool. Null objects within the array are silently ignored.
     *
     * @see #free(Object)
     */
    public void freeAll(Array<T> objects) {
        for (T object : objects) {
            free(object);
        }
    }

    /** Removes all free objects from this pool. */
    public void clear() {
        freeObjects.clear();
    }

    /** The number of objects available to be obtained. */
    public int getFree() {
        return freeObjects.size;
    }

    /** Objects implementing this interface will have {@link #reset()} called when passed to {@link #free(Object)}. */
    static public interface Poolable {
        /** Resets the object for reuse. Object references should be nulled and fields may be set to default values. */
        public void reset();
    }
}
