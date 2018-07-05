// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

/**
 * Wraps a value to JSON object for REST APIs. This is because the root of a JSON document
 * must be a JSON object or JSON array; primitive values are not valid and some JSON parsers
 * may not support them.
 */
public class ValueHolder<T> {

    public ValueHolder() {
    }

    public ValueHolder(T t) {
        value = t;
    }

    public static <T> ValueHolder<T> of(T t) {
        return new ValueHolder<>(t);
    }

    public T value;
}
