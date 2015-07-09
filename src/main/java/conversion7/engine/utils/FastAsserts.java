package conversion7.engine.utils;

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

    public static void assertMoreThan(int actual, int expected) {
        assertMoreThan(actual, expected, null);
    }

    public static void assertMoreThan(int actual, int expected, String msg, Object... msgArgs) {
        if (actual <= expected) {
            throw new AssertionError(format("%d expected to be more than %d [%s]",
                    actual, expected,
                    getAssertMessage(msg, msgArgs)));
        }
    }

    public static void assertMoreThanOrEqual(int actual, int compareTo) {
        if (actual < compareTo) {
            throw new AssertionError(format("%d expected to be more (or equal) than %d [%s]",
                    actual, compareTo,
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

    public static void assertLessThan(int actual, int expected, String msg, Object... msgArgs) {
        if (actual >= expected) {
            throw new AssertionError(format("%d expected to be less than %d [%s]",
                    actual, expected,
                    getAssertMessage(msg, msgArgs)));
        }
    }

}
