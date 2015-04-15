// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.validation;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ElementLengthSetValidator implements ConstraintValidator<ElementLength, Set<? extends CharSequence>> {

    private int min;

    private int max;

    private String message;

    @Override
    public void initialize(ElementLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Set<? extends CharSequence> set, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (set != null) {
            int i = 0;
            for (CharSequence val : set) {
                if (val != null && (val.length() < min || val.length() > max)) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addBeanNode().inIterable().atIndex(i).addConstraintViolation();
                    ok=false;
                }
                i++;
            }
        }
        return ok;
    }
}
