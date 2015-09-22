// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.util;

import java.util.function.Predicate;

import static fi.hsl.parkandride.core.util.Predicates.*;

public class ArgumentValidator<T> {
    private final T obj;

    private ArgumentValidator(T obj) {
        this.obj = obj;
    }

    public static <T> ArgumentValidator<T> validate(T arg) {
        return new ArgumentValidator<>(arg);
    }

    public T matches(Predicate<T> expr) {
        if (expr.test(obj)) {
            return obj;
        }
        throw new IllegalArgumentException("The argument did not satisfy: " + expr);
    }

    public T gt(Comparable<T> other) {
        return matches(greaterThan(other));
    }

    public T gte(Comparable<T> other) {
        return matches(greaterThanOrEqualTo(other));
    }

    public T lt(Comparable<T> other) {
        return matches(lessThan(other));
    }

    public T lte(Comparable<T> other) {
        return matches(lessThanOrEqualTo(other));
    }
}
