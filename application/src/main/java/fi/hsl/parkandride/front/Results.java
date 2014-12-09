package fi.hsl.parkandride.front;

import java.util.List;

public class Results<T> {
    public final List<T> results;

    public Results(List<T> results) {
        this.results = results;
    }

    public static <T> Results<T> of(List<T> results) {
        return new Results<T>(results);
    }
}
