// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Transactional;

import fi.hsl.parkandride.core.domain.NotFoundException;

@Target({ METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
@Transactional(readOnly = true, isolation = READ_COMMITTED, propagation = REQUIRED, noRollbackFor= NotFoundException.class)
public @interface TransactionalRead {
}
