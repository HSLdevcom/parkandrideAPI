// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {

    public static <T> List<List<T>> transpose(List<List<T>> sources) {
        List<Iterator<T>> iterators = sources.stream()
                .map(List::iterator)
                .collect(Collectors.toList());
        List<List<T>> results = new ArrayList<>();
        while (hasNexts(iterators)) {
            results.add(nexts(iterators));
        }
        return results;
    }

    private static <T> boolean hasNexts(List<Iterator<T>> heads) {
        return heads.stream().anyMatch(Iterator::hasNext);
    }

    private static <T> List<T> nexts(List<Iterator<T>> heads) {
        return heads.stream()
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .collect(Collectors.toList());
    }
}
