package conversion7.engine.artemis.engine;

import com.artemis.World;
import com.artemis.injection.FieldResolver;
import com.artemis.utils.reflect.Field;
import conversion7.engine.ClientCore;

public class CustomFieldsResolver implements FieldResolver {

    private ClientCore clientCore;
    private World artemisWorld;

    public CustomFieldsResolver(ClientCore clientCore) {
        this.clientCore = clientCore;
    }

    @Override
    public void initialize(World world) {
        this.artemisWorld = world;
    }

    @Override
    public Object resolve(Class<?> fieldType, Field field) {
        if (fieldType.isInstance(clientCore)) {
            return clientCore;
        } else if (fieldType.isInstance(artemisWorld)) {
            return artemisWorld;
        }
        return null;
    }
}
