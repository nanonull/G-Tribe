package conversion7.engine.utils;

import java.util.Objects;

import static java.lang.String.format;

/**
 * Less new objects > less GC invocations. <br>
 * For basic asserts (true/false/equals) use org.testng.Assert
 */
public class FastAsserts {

    private static String getAssertMessage(String msg, Object... msgArgs) {
        if (msg == null) {
            return "";
        } else {
            if (msgArgs == null) {
                return msg;
            } else {
                return String.format(msg, msgArgs);
            }
        }
    }

    public static void assertMoreThan(int high, int low) {
        assertMoreThan(high, low, null);
    }

    public static void assertMoreThan(int high, int low, String msg, Object... msgArgs) {
        if (high <= low) {
            throw new AssertionError(format("%d expected to be more than %d [%s]",
                    high, low,
                    getAssertMessage(msg, msgArgs)));
        }
    }

    public static void assertMoreThanOrEqual(int high, int low) {
        if (high < low) {
            throw new AssertionError(format("%d expected to be more (or equal) than %d [%s]",
                    high, low,
                    ""));
        }
    }

    public static void assertLessThanOrEqual(int actual, int compareTo) {
        if (actual > compareTo) {
            throw new AssertionError(format("%d expected to be less (or equal) than %d [%s]",
                    actual, compareTo,
                    ""));
        }
    }

    public static void assertLessThan(int actual, int expected) {
        assertLessThan(actual, expected, null, null);
    }

    public static void assertLessThan(int actual, int expected, String msg, Object... msgArgs) {
        if (actual >= expected) {
            throw new AssertionError(format("%d expected to be less than %d [%s]",
                    actual, expected,
                    getAssertMessage(msg, msgArgs)));
        }
    }

    public static void assertNotEqual(Object o1, Object o2) {
        if (Objects.equals(o1, o2)) {
            throw new AssertionError("Expected not equal: [" + o1 + "] and [" + o2 + "]");
        }
    }
}
