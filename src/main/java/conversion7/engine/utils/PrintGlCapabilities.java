package conversion7.engine.utils;

import org.slf4j.Logger;
import org.lwjgl.opengl.ContextCapabilities;

import java.lang.reflect.Field;

public class PrintGlCapabilities {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void print(ContextCapabilities capabilities) {
        LOG.info("\n PrintGlCapabilities: " + capabilities);
        for (Field field : capabilities.getClass().getDeclaredFields()) {
            field.setAccessible(true); // You might want to set modifier to public first.
            Object value = null;
            try {
                value = field.get(capabilities);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                System.out.println(field.getName() + "=" + value);
            }
        }
    }
}
