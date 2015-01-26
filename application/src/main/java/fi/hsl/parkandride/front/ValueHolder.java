package fi.hsl.parkandride.front;

public class ValueHolder<T> {
    public ValueHolder(T t) {
        value = t;
    }

    public static <T> ValueHolder<T> of(T t) {
        return new ValueHolder<>(t);
    }

    public T value;
}
