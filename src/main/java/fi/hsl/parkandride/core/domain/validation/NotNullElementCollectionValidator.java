// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.validation;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullElementCollectionValidator implements ConstraintValidator<NotNullElement, Collection<?>> {

    private String message;

    @Override
    public void initialize(NotNullElement constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        if (collection != null) {
            for (Object val : collection) {
                if (val == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
