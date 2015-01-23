package fi.hsl.parkandride.core.domain.validation;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CollectionElementNotNullValidator implements ConstraintValidator<ElementNotNull, Collection<?>> {

    private String message;

    @Override
    public void initialize(ElementNotNull constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (collection != null) {
            int i = 0;
            for (Object val : collection) {
                if (val == null) {
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
