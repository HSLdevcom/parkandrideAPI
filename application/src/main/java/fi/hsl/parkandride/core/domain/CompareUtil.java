package fi.hsl.parkandride.core.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class CompareUtil {
    public static <T, U extends Comparable<? super U>> Comparator<T> comparingNullsLast(
            Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
                (c1, c2) -> {
                    U v1 = keyExtractor.apply(c1);
                    U v2 = keyExtractor.apply(c2);
                    if (v1 == null) {
                        return v2 == null ? 0 : -1;
                    } else if (v2 == null) {
                        return 1;
                    } else {
                        return v1.compareTo(v2);
                    }
                };
    }
}
