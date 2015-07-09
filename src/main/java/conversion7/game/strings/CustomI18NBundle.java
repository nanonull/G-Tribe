package conversion7.game.strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import org.slf4j.Logger;

import java.util.MissingResourceException;
import java.util.Properties;

/**
 * When to use: empty and null values from locale resource file should be replaced by value from global resource file
 * OR return ???<KEY>??? string
 */
public class CustomI18NBundle {

    private static final Logger LOG = Utils.getLoggerForClass();

    Properties textBundleGlobals;
    I18NBundle textBundle;

    public CustomI18NBundle(String pathToBundleWithoutExt) {
        FileHandle bundleFileHandle = Gdx.files.internal(pathToBundleWithoutExt);
        textBundle = I18NBundle.createBundle(bundleFileHandle, GdxgConstants.locale);

        FileHandle globalFileHandle = Gdx.files.internal(pathToBundleWithoutExt + ".properties");
        textBundleGlobals = Utils.loadProperties(globalFileHandle);
    }


    public String get(ResourceKey resourceKey, Object... args) {
        String resolvedResource;
        String resourceFromBundle;
        try {
            resourceFromBundle = textBundle.get(resourceKey.toString());
        } catch (MissingResourceException exception) {
            resourceFromBundle = "";
        }

        if (resourceFromBundle.isEmpty()) {
            String resourceFromGlobals = textBundleGlobals.getProperty(resourceKey.toString());
            if (resourceFromGlobals == null || resourceFromGlobals.isEmpty()) {
                LOG.warn("Resource was not defined: " + resourceKey);
                resolvedResource = "???" + resourceKey + "???";
            } else {
                resolvedResource = resourceFromGlobals;
            }
        } else {
            resolvedResource = resourceFromBundle;
        }

        return String.format(resolvedResource, args);
    }

    public String get(ResourceKey resourceKey) {
        return get(resourceKey, null);
    }


}
