package conversion7.engine.pools;

import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool;

public class OrderedSetPool extends Pool<OrderedSet> {

    @Override
    protected OrderedSet newObject() {
        return new OrderedSet();
    }

    @Override
    public void free(OrderedSet object) {
        super.free(object);
        object.clear();
    }
}
