package fi.hsl.parkandride.core.domain.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullElementMapValidator implements ConstraintValidator<NotNullElement, Map<?, ?>> {

    private String message;

    @Override
    public void initialize(NotNullElement constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Map<?, ?> map, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (map != null) {
            for (Map.Entry<?, ?> entry: map.entrySet()) {
                if (entry.getValue() == null) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addBeanNode().inIterable().atKey(entry.getKey()).addConstraintViolation();
                    ok=false;
                }
            }
        }
        return ok;
    }
}
