package conversion7.engine.utils;

/**
 * E - type of key<br>
 * T - type of value
 */
public interface MapAccessExt<E, T> {

    T get(final E key);

    void put(final E key, final T value);

    void update(final E key, final T value);

    void remove(final E key);

    default String getS(final E key) {
        Object value = get(key);
        return value == null ? null : value.toString();
    }

    default int getI(final E key) {
        Object value = get(key);
        if (value == null) {
            throw new NullPointerException("getI could not return null as primitive");
        }
        return Integer.parseInt(value.toString());
    }

    default Integer getInteger(final E key) {
        Object value = get(key);
        return value == null ? null : Integer.parseInt(value.toString());
    }

    default float getF(final E key) {
        Object value = get(key);
        if (value == null) {
            throw new NullPointerException("getF could not return null as primitive");
        }
        return Float.parseFloat(value.toString());
    }

    default Float getFloat(final E key) {
        Object value = get(key);
        return value == null ? null : Float.parseFloat(value.toString());
    }

    default Boolean getB(final E key) {
        Object value = get(key);
        return value == null ? null : Boolean.parseBoolean(value.toString());
    }

    default <C extends T> C get(final E key, final Class<C> type) {
        Object object = get(key);
        return (object == null) ? null : (C) object;
    }

}
