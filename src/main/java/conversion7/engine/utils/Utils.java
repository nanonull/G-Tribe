package conversion7.engine.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import conversion7.engine.AbstractClientCore;
import org.fest.assertions.api.Fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.Random;

import static java.lang.String.format;

public class Utils {

    public static final Logger LOG = Utils.getLoggerForClass();

    public static final Random RANDOM = new Random();
    public static final Json JSON = new Json();

    private static int id = 0;

    public static int getNextId() {
        return id++;
    }

    public static Logger getLoggerForClass() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public static void debug(String message) {
        if (LOG.isDebugEnabled())
            LOG.debug(message);
    }

    public static void debug(Logger logger, String formattedMessage, Object arg1, Object arg2, Object arg3) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(formattedMessage, arg1, arg2, arg3));
        }
    }

    public static void debug(Logger logger, String formattedMessage, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(formattedMessage, arg1, arg2));
        }
    }

    public static void debug(Logger logger, String formattedMessage, Object arg1) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(formattedMessage, arg1));
        }
    }

    public static void logErrorWithCurrentStacktrace(String text) {
        try {
            throw new Exception(text);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Will interrupt application.<br>
     * Use instead: throw new GdxRuntimeException("");
     */
    @Deprecated
    public static void error(String msg) {
        LOG.error(msg);
        Fail.fail(msg);
    }

    /** Use instead: throw new GdxRuntimeException(""); */
    @Deprecated
    public static void error(Throwable throwable) {
        LOG.error(throwable.getMessage(), throwable);
        throw new GdxRuntimeException(throwable);
    }

    public static void infinitySleepThread() {
        while (true) {
            sleepThread(Long.MAX_VALUE);
        }
    }

    public static void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Main reason:
     * need log exceptions to log file, but log is impossible when exception is thrown
     */
    public static void createThreadExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error(format("[UncaughtException] Exception from thread [%s]:", t), e);
                AbstractClientCore.setApplicationCrash(e);
            }
        });
    }

    public static Properties loadProperties(FileHandle fileHandle) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(fileHandle.readString()));
        } catch (IOException e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }

        return properties;
    }
}
