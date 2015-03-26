// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import com.google.common.collect.ImmutableList;
import fi.hsl.parkandride.core.domain.Violation;

import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.StreamSupport;

public class ValidationException extends IllegalArgumentException {

    public final List<Violation> violations;

    public ValidationException(Violation violation) {
        this(ImmutableList.of(violation));
    }

    public ValidationException(Iterable<Violation> violations) {
        super("Invalid data. Violations in " + formatViolations(violations));
        this.violations = ImmutableList.copyOf(violations);
    }

    private static String formatViolations(Iterable<Violation> violations) {
        StringJoiner joiner = new StringJoiner(", ");
        StreamSupport.stream(violations.spliterator(), false)
                .sorted(Comparator.comparing((Violation v) -> v.path).thenComparing(v -> v.type))
                .forEach(v -> joiner.add(v.path + " (" + v.type + ")"));
        return joiner.toString();
    }
}
