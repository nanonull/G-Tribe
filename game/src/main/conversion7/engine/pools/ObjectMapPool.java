package conversion7.engine.pools;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class ObjectMapPool extends Pool<ObjectMap> {

    @Override
    protected ObjectMap newObject() {
        return new ObjectMap();
    }

    @Override
    public void free(ObjectMap object) {
        super.free(object);
        object.clear();
    }
}
