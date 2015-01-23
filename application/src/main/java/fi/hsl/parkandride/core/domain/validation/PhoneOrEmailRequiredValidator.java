package fi.hsl.parkandride.core.domain.validation;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.hsl.parkandride.core.domain.Contact;

public class PhoneOrEmailRequiredValidator implements ConstraintValidator<PhoneOrEmailRequired, Contact> {

    @Override
    public void initialize(PhoneOrEmailRequired constraintAnnotation) {
    }

    @Override
    public boolean isValid(Contact contact, ConstraintValidatorContext context) {
        return contact == null || !(isNullOrEmpty(contact.email) && contact.phone == null);
    }
}
