// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinElementMapValidator implements ConstraintValidator<MinElement, Map<?, ? extends Number>> {

    private long min;

    private String message;

    @Override
    public void initialize(MinElement constraintAnnotation) {
        this.min = constraintAnnotation.value();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Map<?, ? extends Number> map, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (map != null) {
            for (Map.Entry<?, ? extends Number> entry: map.entrySet()) {
                Number num = entry.getValue();
                if (num != null && num.longValue() < min) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addBeanNode().inIterable().atKey(entry.getKey()).addConstraintViolation();
                    ok=false;
                }
            }
        }
        return ok;
    }
}
