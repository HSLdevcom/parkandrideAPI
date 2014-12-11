package fi.hsl.parkandride.core.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpacesAvailableOrStatusRequiredValidator implements ConstraintValidator<SpacesAvailableOrStatusRequired, FacilityStatus> {
    @Override
    public void initialize(SpacesAvailableOrStatusRequired constraintAnnotation) {

    }

    @Override
    public boolean isValid(FacilityStatus value, ConstraintValidatorContext context) {
        return value.spacesAvailable != null || value.status != null;
    }
}
