package fi.hsl.parkandride.core.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidTimeDurationValidator implements ConstraintValidator<ValidTimeDuration, TimeDuration> {

    @Override
    public void initialize(ValidTimeDuration constraintAnnotation) {

    }

    @Override
    public boolean isValid(TimeDuration value, ConstraintValidatorContext context) {
        if (value == null || value.from == null || value.until == null) {
            return  true;
        }
        return value.from.isBefore(value.until);
    }
}
