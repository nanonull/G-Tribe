package conversion7.engine.pools;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class Vectors3Pool extends Pool<Vector3> {

    @Override
    protected Vector3 newObject() {
        return new Vector3();
    }

    @Override
    public void free(Vector3 object) {
        super.free(object);
        object.set(0, 0, 0);
    }
}
