// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public final class MapUtils {
    private MapUtils() { /** prevent instantiation */}


    public static <F, T> Set<T> extractFromKeys(Map<F, ?> map, Function<F, T> fn) {
        return map.keySet().stream().map(fn).collect(toSet());
    }

    public static <T> Collector<T, ?, Map<T, Long>> countingOccurrences() {
        return groupingBy(identity(), counting());
    }

    /**
     * NOTE: will throw exception on duplicate keys. See {@link #entriesToMap(BinaryOperator)} to
     * cope with this.
     */
    public static <K,V> Collector<Map.Entry<K,V>, ?, Map<K, V>> entriesToMap() {
        return toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue()
        );
    }

    public static <K,V> Collector<Map.Entry<K,V>, ?, Map<K, V>> entriesToMap(BinaryOperator<V> mergeFn) {
        return toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue(),
                mergeFn
        );
    }

    public static <K, V, W> Function<Map.Entry<K,V>, Map.Entry<K, W>> mappingValue(BiFunction<K, V, W> mapper) {
        return entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), mapper.apply(entry.getKey(), entry.getValue()));
    }

    public static <K, V, KK> Function<Map.Entry<K,V>, Map.Entry<KK, V>> mappingKey(BiFunction<K, V, KK> mapper) {
        return entry -> new AbstractMap.SimpleImmutableEntry<>(mapper.apply(entry.getKey(), entry.getValue()), entry.getValue());
    }

    public static <K, V, OUT> Function<Map.Entry<K,V>, OUT> mappingEntry(BiFunction<K, V, OUT> mapper) {
        return entry -> mapper.apply(entry.getKey(), entry.getValue());
    }

    public static <K, V> Consumer<Map.Entry<K,V>> consumingEntry(BiConsumer<K, V> consumer) {
        return entry -> consumer.accept(entry.getKey(), entry.getValue());
    }
}
