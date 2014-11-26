package fi.hsl.parkandride.core.service;

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

}
