// Copyright © 2015 HSL

package fi.hsl.parkandride.front;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Results<T> {
    public final List<T> results;

    public Results(List<T> results) {
        this.results = results;
    }

    public static <T> Results<T> of(List<T> results) {
        return new Results<T>(results);
    }

    public static <T> Results<T> of(Set<T> results) {
        return new Results<T>(new ArrayList<>(results));
    }
}
