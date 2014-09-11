package fi.hsl.parkandride.core.service;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.lang.annotation.*;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Target({ METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
@Transactional(readOnly = false, isolation = READ_COMMITTED, propagation = REQUIRED)
public @interface TransactionalWrite {
}
