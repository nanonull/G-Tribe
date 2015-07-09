package conversion7.tests_standalone.misc;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class TestsHashMapVsObjectMap {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void main(String[] args) {
        LOG.info("TestsCollections main");
//        testJavaCollection();
        testLibgdxCollection();
    }

    private static void testJavaCollection() {
        LOG.info("testJavaCollection");

        Map<String, String> map = new HashMap<>();
        int size = Utils.RANDOM.nextInt(2) + 1000000;
        Timer timer;

        timer = new Timer();
        for (int j = 0; j < size; j++) {
            map.put(String.valueOf(j), "value");
        }
        timer.stop("add");

        timer = new Timer();
        for (int j = 0; j < map.size() - 1; j++) {
            map.get(String.valueOf(j));
        }
        timer.stop("get");

        timer = new Timer();
        for (int i = map.size() - 1; i >= 0; i--) {
            map.remove(String.valueOf(i));
        }
        timer.stop("remove");
    }

    private static void testLibgdxCollection() {
        LOG.info("testLibgdxCollection");

        ObjectMap<String, String> map = new ObjectMap<>();
        int size = Utils.RANDOM.nextInt(2) + 1000000;
        Timer timer;

        timer = new Timer();
        for (int j = 0; j < size; j++) {
            map.put(String.valueOf(j), "value");
        }
        timer.stop("add");

        timer = new Timer();
        for (int j = 0; j < map.size - 1; j++) {
            map.get(String.valueOf(j));
        }
        timer.stop("get");

        timer = new Timer();
        for (int i = map.size - 1; i >= 0; i--) {
            map.remove(String.valueOf(i));
        }
        timer.stop("remove");
    }
}
