// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class SearchResults<T> {

    public static <T> SearchResults<T> of(final Collection<T> results) {
        return of(new ArrayList<T>(results));
    }

    public static <T> SearchResults<T> of(final List<T> results) {
        return new SearchResults<T>(results, false);
    }

    public static <T> SearchResults<T> of(final Collection<T> results, final int limit) {
        return of(new ArrayList<T>(results), limit);
    }

    public static <T> SearchResults<T> of(final List<T> results, final int limit) {
        if (results.isEmpty()) {
            return (SearchResults<T>) SearchResults.EMPTY;
        } else if (results.size() <= limit) {
            return new SearchResults<T>(results, false);
        } else {
            return new SearchResults<T>(results.subList(0, (int) limit), true);
        }
    }

    public static final SearchResults EMPTY = new SearchResults(ImmutableList.of(), false);

    @ApiModelProperty(required = true)
    public final List<T> results;

    @ApiModelProperty(required = true)
    public final boolean hasMore;

    private SearchResults(List<T> results, boolean hasMore) {
        this.results = results;
        this.hasMore = hasMore;
    }

    public T get(int index) {
        return results.get(index);
    }

    public int size() {
        return results.size();
    }
}
