package fi.hsl.parkandride.core.service;

import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.hsl.parkandride.core.domain.Violation;

public class ValidationException extends RuntimeException {

    public final List<Violation> violations;

    public ValidationException(Iterable<Violation> violations) {
        this.violations = ImmutableList.copyOf(violations);
    }

}
