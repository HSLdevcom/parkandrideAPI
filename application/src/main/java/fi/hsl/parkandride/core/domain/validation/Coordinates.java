// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = { CoordinatesValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@Retention(RUNTIME)
public @interface Coordinates {

    String message() default "{fi.hsl.parkandride.core.domain.validation.Coordinates.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
