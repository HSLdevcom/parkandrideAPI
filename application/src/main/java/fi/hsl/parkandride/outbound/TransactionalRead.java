package fi.hsl.parkandride.outbound;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Transactional;

@Target({ METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
@Transactional(readOnly = true, isolation = READ_COMMITTED, propagation = MANDATORY)
public @interface TransactionalRead {
}
