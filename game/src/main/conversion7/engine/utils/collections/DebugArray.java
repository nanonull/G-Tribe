package conversion7.engine.utils.collections;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DebugArray<T> extends Array<T> {
    private static final Logger LOG = Utils.getLoggerForClass();

    private Map<String, Throwable> lastAccessAt = new LinkedHashMap<>();

    @Override
    public Iterator<T> iterator() {
        Iterator<T> iterator;
        try {
            iterator = super.iterator();
            iterator.hasNext();
        } catch (Throwable e) {
            printLastAccess();
            throw e;
        }
        lastAccessAt.put(Calendar.getInstance().getTime().toString(), new Exception());
        return iterator;
    }

    public void printLastAccess() {
        for (Map.Entry<String, Throwable> entry : lastAccessAt.entrySet()) {
            LOG.warn(entry.getKey(), entry.getValue());
        }
    }

}
