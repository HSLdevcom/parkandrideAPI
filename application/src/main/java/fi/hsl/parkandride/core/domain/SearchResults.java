package fi.hsl.parkandride.core.domain;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class SearchResults<T> {

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

    public final List<T> results;

    public final boolean hasMore;

    private SearchResults(List<T> results, boolean hasMore) {
        this.results = results;
        this.hasMore = hasMore;
    }
}
