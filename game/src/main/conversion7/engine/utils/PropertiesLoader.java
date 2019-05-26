package conversion7.engine.utils;

import com.badlogic.gdx.files.FileHandle;
import org.slf4j.Logger;

import java.util.Properties;

public abstract class PropertiesLoader {

    private static final Logger LOG = Utils.getLoggerForClass();

    static Properties properties = Utils.loadProperties(new FileHandle("common.properties"));

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        LOG.info("getProperty: " + key + " = " + value);
        return value;
    }

    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
}
