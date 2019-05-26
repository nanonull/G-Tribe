package conversion7.engine.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conversion7.engine.ClientCore;
import org.fest.assertions.api.Fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Utils {

    public static final Logger LOG = Utils.getLoggerForClass();

    public static final Json JSON = new Json();
    public static final Gson GSON = buildGdxgGson();
    private static int id = 0;
    public static boolean activeSleepThread = false;

    public static synchronized int getNextId() {
        return id++;
    }

    public static Logger getLoggerForClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; ; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            String pointText = stackTraceElement.toString();
            if (pointText.contains("<clinit>")) {
                return LoggerFactory.getLogger(stackTraceElement.getClassName());
            }
        }
    }

    public static boolean safeIter(Array array) {
        Array.ArrayIterator arrayIterator = (Array.ArrayIterator) array.iterator();
        try {
            arrayIterator.iterator().hasNext();
            return true;
        } catch (GdxRuntimeException e) {
            return false;
        }
    }

    public static <T> Array.ArrayIterable<T> iter(Array<T> array) {
        return new Array.ArrayIterable<>(array);
    }

    public static String joinToString(Collection<?> collection, String delimiter) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    private static Gson buildGdxgGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    public static Logger getLoggerForClass(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
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

    public static void printErrorWithCurrentStacktrace(String text) {
        try {
            throw new ShowStackTraceMessage(text);
        } catch (ShowStackTraceMessage e) {
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
        activeSleepThread = true;
        while (activeSleepThread) {
            sleepThread(1000);
        }
    }

    public static void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Main reason:
     * need log exceptions to log file, but log is impossible when exception is thrown
     */
    public static void createThreadExceptionHandler(ClientCore clientCore) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error(format("[UncaughtException] Exception from thread [%s]:", t), e);
                clientCore.addError(e);
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

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> from) {
        List<T> list = new ArrayList<T>(from);
        java.util.Collections.sort(list);
        return list;
    }

}
