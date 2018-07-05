// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import fi.hsl.parkandride.core.domain.Violation;

public class ValidationService {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final Function<ConstraintViolation<?>, Violation> TO_VIOLATION = new Function<ConstraintViolation<?>, Violation>() {
        @Nullable
        @Override
        public Violation apply(@Nullable ConstraintViolation<?> input) {
            return new Violation(input);
        }
    };

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ValidationException(Iterables.transform(violations, TO_VIOLATION));
        }
    }

    public void validate(Object object, Collection<Violation> violations) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            violations.add(TO_VIOLATION.apply(constraintViolation));
        }
    }
}
