package fi.hsl.parkandride.core.domain.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = { WGS84CoordinatesValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@Retention(RUNTIME)
public @interface WGS84Coordinates {

    String message() default "{fi.hsl.parkandride.core.domain.validation.WGS84Coordinates.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
