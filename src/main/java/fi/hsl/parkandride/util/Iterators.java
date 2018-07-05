// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.util;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Iterators {
    private Iterators() { /** prevent instantiation */}

    /**
     * Returns an iterator with {@code from} as first element. The subsequent elements are calculated by applying the {@code toNext}
     * function to the previous item. The iterator ends when {@code iterateWhile} returns {@code false}.
     * <p/>
     * Similar semantics as a for loop.
     * <p/>
     * Example: {@code iterateFor(0, i -> i+1, i -> i < 5) -> [0,1,2,3,4]}
     * <br/>
     * Beware: {@code iterateFor(0, i -> i++, i -> i < 5)}
     * will result in an infinite stream of [0,0,0,...]
     *
     * @param <T>
     * @param from the first element
     * @param iterateWhile the
     * @return
     */
    public static <T> Iterator<T> iterateFor(final T from, final Predicate<T> iterateWhile, final UnaryOperator<T> fn) {
        return new Iterator<T>() {
            private T current = from;
            @Override
            public boolean hasNext() {
                return iterateWhile.test(current);
            }

            @Override
            public T next() {
                T prev = current;
                current = fn.apply(current);
                return prev;
            }
        };
    }
}
