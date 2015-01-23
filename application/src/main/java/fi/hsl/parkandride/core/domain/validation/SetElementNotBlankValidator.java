package fi.hsl.parkandride.core.domain.validation;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SetElementNotBlankValidator implements ConstraintValidator<ElementNotBlank, Set<? extends CharSequence>> {

    private String message;

    @Override
    public void initialize(ElementNotBlank constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Set<? extends CharSequence> set, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean ok = true;
        if (set != null) {
            int i = 0;
            for (CharSequence val : set) {
                if (val == null || val.toString().trim().length() == 0) {
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
