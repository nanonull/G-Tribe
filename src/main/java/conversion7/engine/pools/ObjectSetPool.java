package conversion7.engine.pools;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

public class ObjectSetPool extends Pool<ObjectSet> {

    @Override
    protected ObjectSet newObject() {
        return new ObjectSet();
    }

    @Override
    public void free(ObjectSet object) {
        super.free(object);
        object.clear();
    }
}
