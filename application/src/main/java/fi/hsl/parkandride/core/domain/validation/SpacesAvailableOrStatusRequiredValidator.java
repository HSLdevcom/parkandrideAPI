package fi.hsl.parkandride.core.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.hsl.parkandride.core.domain.FacilityStatus;

public class SpacesAvailableOrStatusRequiredValidator implements ConstraintValidator<SpacesAvailableOrStatusRequired, FacilityStatus> {
    @Override
    public void initialize(SpacesAvailableOrStatusRequired constraintAnnotation) {

    }

    @Override
    public boolean isValid(FacilityStatus value, ConstraintValidatorContext context) {
        return value.spacesAvailable != null || value.status != null;
    }
}
