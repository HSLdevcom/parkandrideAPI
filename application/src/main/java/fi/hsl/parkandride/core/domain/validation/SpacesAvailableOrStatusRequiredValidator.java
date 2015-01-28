package fi.hsl.parkandride.core.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.hsl.parkandride.core.domain.Utilization;

public class SpacesAvailableOrStatusRequiredValidator implements ConstraintValidator<SpacesAvailableOrStatusRequired, Utilization> {
    @Override
    public void initialize(SpacesAvailableOrStatusRequired constraintAnnotation) {

    }

    @Override
    public boolean isValid(Utilization value, ConstraintValidatorContext context) {
        return value.spacesAvailable != null || value.status != null;
    }
}
