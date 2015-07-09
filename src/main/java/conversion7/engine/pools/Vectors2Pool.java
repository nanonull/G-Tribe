package conversion7.engine.pools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Vectors2Pool extends Pool<Vector2> {

    @Override
    protected Vector2 newObject() {
        return new Vector2();
    }

    @Override
    public void free(Vector2 object) {
        super.free(object);
        object.set(0, 0);
    }
}
