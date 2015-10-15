// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;
import fi.hsl.parkandride.core.domain.NullSafeSortedSet;

import java.util.SortedSet;

public class GSortedSet<T extends Comparable<? super T>> extends AbstractGroupExpression<T, NullSafeSortedSet<T>> {

    public static <E extends Comparable<? super E>> AbstractGroupExpression<E, NullSafeSortedSet<E>> sortedSet(Expression<E> expression) {
        return new GSortedSet<E>(expression);
    }

    private static final long serialVersionUID = -1575808026237160843L;

    public GSortedSet(Expression<T> expr) {
        super(SortedSet.class, expr);
    }

    @Override
    public GroupCollector<T, NullSafeSortedSet<T>> createGroupCollector() {
        return new GroupCollector<T, NullSafeSortedSet<T>>() {

            private final NullSafeSortedSet<T> set = new NullSafeSortedSet<T>();

            @Override
            public void add(T o) {
                if (o != null) {
                    set.add(o);
                }
            }

            @Override
            public NullSafeSortedSet<T> get() {
                return set;
            }

        };
    }
}
