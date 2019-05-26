package conversion7.engine.utils;


import org.slf4j.Logger;

public class Timer {

    private static final Logger LOG = Utils.getLoggerForClass();

    private long time;
    private boolean stopped = false;
    private Logger logger;
    private boolean selfLogged;

    /** If selfLogged == false, then logger will be ignored and just message returned */
    public Timer(Logger logger, boolean selfLogged) {
        this.logger = logger;
        this.selfLogged = selfLogged;
        time = -System.nanoTime();
    }

    public Timer(boolean selfLogged) {
        this(LOG, selfLogged);
    }

    public Timer(Logger logger) {
        this(logger, true);
    }

    public Timer() {
        this(LOG, true);
    }

    public String stop() {
        return stop(null);
    }

    public String stop(String msg) {
        if (!this.stopped) {
            stopped = true;
            long newTime = time + System.nanoTime();

            StringBuilder sb = new StringBuilder("Timer stopped at ").append(newTime).append(" nanoseconds.");
            if (msg != null && !msg.isEmpty()) {
                sb.append(" Message: '").append(msg).append("'");
            }

            if (selfLogged) {
                logger.info(sb.toString());
                return null;
            } else {
                return sb.toString();
            }
        } else {
            Utils.error("Timer has been already stopped! Message: '" + msg + "'");
            return null;
        }
    }

    public int getTimeMillis() {
        return (int) ((time + System.nanoTime()) / 1000000);
    }
}
