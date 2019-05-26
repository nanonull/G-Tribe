package conversion7.tests_standalone.misc;

import conversion7.engine.utils.Utils;
import groovy.lang.Closure;
import org.testng.annotations.Test;

public class TestClosure {

    @Test
    public void testGroovyClosureInJava() {
        new Closure(null) {
            @Override
            public Object call() {
                Utils.LOG.info("testGroovyClosureInJava");
                return null;
            }
        }.run();
    }

}
