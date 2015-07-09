package conversion7.tests_standalone.misc;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.ArrayList;

/**
 * Array	add	25445038,00	25499573,00	24665894,00
 * get	7830863,00	8166170,00	7882697,00
 * remove	17521845,00	16521322,00	17459751,00
 * <p/>
 * ArrayList	add	37081453,00	35046930,00	33588530,00
 * get	7342209,00	8206666,00	7685077,00
 * remove	16013231,00	15067782,00	14965733,00
 */
public class TestsArrayVsArrayList {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static void main(String[] args) {
        LOG.info("TestsCollections main");
        testArrayList();

    }

    private static void testArrayList() {
        LOG.info("testArrayList");

        ArrayList<String> array = new ArrayList<>();
        int size = Utils.RANDOM.nextInt(2) + 1000000;
        Timer timer;

        timer = new Timer();
        for (int j = 0; j < size; j++) {
            array.add("add");
        }
        timer.stop("add");

        timer = new Timer();
        for (int j = 0; j < array.size() - 1; j++) {
            array.get(j);
        }
        timer.stop("get");

        timer = new Timer();
        for (int i = array.size() - 1; i >= 0; i--) {
            array.remove(i);
        }
        timer.stop("remove");
    }

    private static void testArray() {
        LOG.info("testArray");

        Array<String> array = new Array<>();
        int size = Utils.RANDOM.nextInt(2) + 1000000;
        Timer timer;

        timer = new Timer();
        for (int j = 0; j < size; j++) {
            array.add("add");
        }
        timer.stop("add");

        timer = new Timer();
        for (int j = 0; j < array.size - 1; j++) {
            array.get(j);
        }
        timer.stop("get");

        timer = new Timer();
        for (int i = array.size - 1; i >= 0; i--) {
            array.removeIndex(i);
        }
        timer.stop("remove");
    }
}
