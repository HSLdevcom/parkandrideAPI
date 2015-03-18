// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.hsl.parkandride.core.domain.Violation;

public class ValidationException extends IllegalArgumentException {

    public final List<Violation> violations;

    public ValidationException(Violation violation) {
        this(ImmutableList.of(violation));
    }

    public ValidationException(Iterable<Violation> violations) {
        super("Invalid data. See violations for details.");
        this.violations = ImmutableList.copyOf(violations);
    }

}
