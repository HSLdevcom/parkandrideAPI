// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import static java.util.Comparator.nullsFirst;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class NullSafeSortedSet<E> extends TreeSet<E> {

    public NullSafeSortedSet() {
        super((Comparator<? super E>) nullsFirst(Comparator.naturalOrder()));
    }

    public NullSafeSortedSet(Comparator<? super E> comparator) {
        super(nullsFirst(comparator));
    }

    public NullSafeSortedSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public NullSafeSortedSet(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }
}
