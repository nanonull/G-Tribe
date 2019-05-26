package conversion7.engine.utils;

/**
 * E - type of key<br>
 * T - type of value
 */
public abstract class AbstractEnumMap<E extends Enum<E>, T> {

    public abstract T get(final E key);

    public abstract void put(final E key, final T value);

    public abstract void update(final E key, final T value);

    public abstract void remove(final E key);

    public String getS(final E key) {
        Object value = get(key);
        return value == null ? null : value.toString();
    }

    public Integer getI(final E key) {
        Object value = get(key);
        return value == null ? null : Integer.parseInt(value.toString());
    }

    public Float getF(final E key) {
        Object value = get(key);
        return value == null ? null : Float.parseFloat(value.toString());
    }

    public Boolean getB(final E key) {
        Object value = get(key);
        return value == null ? null : Boolean.parseBoolean(value.toString());
    }

    public <C extends T> C get(final E key, final Class<C> type) {
        Object object = get(key);
        return (object == null) ? null : (C) object;
    }

}
