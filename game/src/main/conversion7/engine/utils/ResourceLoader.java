package conversion7.engine.utils;

import java.io.InputStream;

public class ResourceLoader {
    public static InputStream getResourceAsStream(String name) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(name);
    }
}
