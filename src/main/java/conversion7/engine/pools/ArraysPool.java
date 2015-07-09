package conversion7.engine.pools;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.CustomPool;

public class ArraysPool extends CustomPool<Array> {

    @Override
    protected Array newObject() {
        return new Array();
    }

    @Override
    public void free(Array object) {
        super.free(object);
        object.ordered = true;
        object.clear();
    }
}
