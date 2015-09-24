// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.util;

import java.util.function.Predicate;

public class Predicates {
    private Predicates() { /** prevent instantiation */}


    // GREATER THAN
    public static <T> Predicate<T> gt(Comparable<? super T> other) {
        return greaterThan(other);
    }
    public static <T> Predicate<T> gte(Comparable<? super T> other) {
        return greaterThanOrEqualTo(other);
    }
    public static <T> Predicate<T> greaterThan(Comparable<? super T> other) {
        return o -> other.compareTo(o) < 0;
    }
    public static <T> Predicate<T> greaterThanOrEqualTo(Comparable<? super T> other) {
        return o -> other.compareTo(o) <= 0;
    }

    // LESSER THAN
    public static <T> Predicate<T> lt(Comparable<? super T> other) {
        return lessThan(other);
    }
    public static <T> Predicate<T> lte(Comparable<? super T> other) {
        return lessThanOrEqualTo(other);
    }
    public static <T> Predicate<T> lessThan(Comparable<? super T> other) {
        return o -> other.compareTo(o) > 0;
    }
    public static <T> Predicate<T> lessThanOrEqualTo(Comparable<? super T> other) {
        return o -> other.compareTo(o) >= 0;
    }
}
