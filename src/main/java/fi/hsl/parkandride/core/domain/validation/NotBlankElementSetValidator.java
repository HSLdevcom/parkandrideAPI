// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.validation;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankElementSetValidator implements ConstraintValidator<NotBlankElement, Set<? extends CharSequence>> {

    private String message;

    @Override
    public void initialize(NotBlankElement constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Set<? extends CharSequence> set, ConstraintValidatorContext context) {
        if (set != null) {
            for (CharSequence val : set) {
                if (val == null || val.toString().trim().length() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
