// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.util;

import fi.hsl.parkandride.core.service.ValidationException;
import org.assertj.core.api.AbstractAssert;
import org.junit.Test;

import static fi.hsl.parkandride.util.ArgumentValidator.validate;
import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentValidatorTest {


    @Test
    public void valid_values_pass() {
        RunnableAssert.assertThat(() -> assertThat(validate(5).gt(4)).isEqualTo(5)).passesWithoutFailure();
        RunnableAssert.assertThat(() -> assertThat(validate(5).gte(5)).isEqualTo(5)).passesWithoutFailure();
        RunnableAssert.assertThat(() -> assertThat(validate(4).lt(5)).isEqualTo(4)).passesWithoutFailure();
        RunnableAssert.assertThat(() -> assertThat(validate(3).lte(3)).isEqualTo(3)).passesWithoutFailure();
    }

    @Test
    public void invalid_values_throw() {
        RunnableAssert.assertThat(() -> validate(5).gt(5))
                .failsWith(ValidationException.class);

        RunnableAssert.assertThat(() -> validate(5).gte(6))
                .failsWith(ValidationException.class);

        RunnableAssert.assertThat(() -> validate(5).lte(4))
                .failsWith(ValidationException.class);

        RunnableAssert.assertThat(() -> validate(5).lt(5))
                .failsWith(ValidationException.class);
    }

    public static class RunnableAssert extends AbstractAssert<RunnableAssert, Runnable> {

        public RunnableAssert(Runnable actual) {
            super(actual, RunnableAssert.class);
        }

        public static RunnableAssert assertThat(Runnable r) {
            return new RunnableAssert(r);
        }

        public RunnableAssert passesWithoutFailure() {
            try {
                actual.run();
            } catch (Exception e) {
                failWithMessage("Expected runnable to pass without exception. Caught <%s>", e);
            }
            return this;
        }

        public RunnableAssert failsWith(Class<? extends Exception> excClass) {
            try {
                actual.run();
                failWithMessage("Expected runnable to throw <%s>. Nothing thrown.", excClass);
            } catch (Exception e) {
                if (!excClass.isAssignableFrom(e.getClass())) {
                    failWithMessage("Expected runnable to throw <%s>. Caught <%s> instead.", excClass, e);
                }
            }
            return this;
        }
    }

}