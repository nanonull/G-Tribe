package conversion7.tests_standalone.misc;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class TestStringConcat {

private static final Logger LOG = Utils.getLoggerForClass();

    public static void main(String[] args) {
        int i = 0;
        long prev_time = System.currentTimeMillis();
        long time;

        for (i = 0; i < 100000; i++) {
            String s = "Blah" + i + "Blah";
        }
        time = System.currentTimeMillis() - prev_time;
        LOG.info("Time after for loop " + time);


        prev_time = System.currentTimeMillis();
        for (i = 0; i < 100000; i++) {
            String s = String.format("Blah %d Blah", i);
        }
        time = System.currentTimeMillis() - prev_time;
        LOG.info("Time after for loop " + time);


        prev_time = System.currentTimeMillis();
        for (i = 0; i < 100000; i++) {
            String s = new StringBuilder("Blah ").append(i).append(" Blah").toString();
        }
        time = System.currentTimeMillis() - prev_time;
        LOG.info("Time after for loop " + time);


    }

}
