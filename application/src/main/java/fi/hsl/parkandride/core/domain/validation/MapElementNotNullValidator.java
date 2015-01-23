package fi.hsl.parkandride.core.domain.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MapElementNotNullValidator implements ConstraintValidator<ElementNotNull, Map<?, ?>> {

    private String message;

    @Override
    public void initialize(ElementNotNull constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Map<?, ?> map, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (map != null) {
            int i = 0;
            for (Map.Entry<?, ?> entry: map.entrySet()) {
                if (entry.getValue() == null) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addBeanNode().inIterable().atKey(entry.getKey()).addConstraintViolation();
                    ok=false;
                }
                i++;
            }
        }
        return ok;
    }
}
