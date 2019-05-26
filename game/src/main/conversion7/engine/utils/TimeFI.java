package conversion7.engine.utils;

/**
 * Time storage which could produce int or float value for different TimeUnit systems
 */
public class TimeFI {

    private long ms;
    private float second = -1;

    public TimeFI(long ms) {
        this.ms = ms;
    }

    public float getFloatSeconds() {
        if (second == -1) {
            second = (float) ms / 1000;
        }
        return second;
    }

    public long getMillis() {
        return ms;
    }

}
