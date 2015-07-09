package conversion7.engine.pools.system;

import com.badlogic.gdx.utils.Pool;

/** It's better to use standard pool */
@Deprecated
public interface LinkedPoolable extends Pool.Poolable {

    public void linkToPool(Pool pool);

    public void returnToPool();

}
