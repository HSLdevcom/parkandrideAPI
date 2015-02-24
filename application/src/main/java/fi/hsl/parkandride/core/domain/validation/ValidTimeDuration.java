package fi.hsl.parkandride.core.domain.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = { ValidTimeDurationValidator.class })
@Target({ TYPE})
@Retention(RUNTIME)
public @interface ValidTimeDuration {

    String message() default "{fi.hsl.parkandride.core.domain.validation.ValidTimeDuration.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
