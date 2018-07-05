// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.util;

import org.assertj.core.api.AbstractAssert;
import org.junit.Test;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static fi.hsl.parkandride.util.Predicates.*;
import static fi.hsl.parkandride.util.PredicatesTest.PredicatesAssert.assertThat;

public class PredicatesTest {

    @Test
    public void gt_works() {
        assertThat(gt(5))
                .matches(6,7,8,9,10,Integer.MAX_VALUE)
                .doesNotMatch(Integer.MIN_VALUE, -1, 0, 1, 2, 3, 4, 5);
    }

    @Test
    public void gte_works() {
        assertThat(gte(0))
                .matches(0,1,2,3,Integer.MAX_VALUE)
                .doesNotMatch(Integer.MIN_VALUE, -2, -1);
    }

    @Test
    public void lt_works() {
        assertThat(lt(5))
                .doesNotMatch(5,6,7,8,9,10,Integer.MAX_VALUE)
                .matches(Integer.MIN_VALUE, -1, 0, 1, 2, 3, 4);
    }

    @Test
    public void lte_works() {
        assertThat(lte(0))
                .doesNotMatch(1, 2, 3, Integer.MAX_VALUE)
                .matches(Integer.MIN_VALUE, -2, -1, 0);
    }


    public static class PredicatesAssert<T> extends AbstractAssert<PredicatesAssert<T>, Predicate<T>> {

        public PredicatesAssert(Predicate<T> actual) {
            super(actual, PredicatesAssert.class);
        }

        public static <T> PredicatesAssert<T> assertThat(Predicate<T> actual) {
            return new PredicatesAssert<>(actual);
        }

        public PredicatesAssert<T> matches(T target) {
            isNotNull();
            if (!actual.test(target)) {
                failWithMessage("Expected predicate <%s> to match <%s>", actual, target);
            }
            return this;
        }

        public PredicatesAssert<T> matches(T... targets) {
            Stream.of(targets).forEach(this::matches);
            return this;
        }

        public PredicatesAssert<T> doesNotMatch(T target) {
            isNotNull();
            if (actual.test(target)) {
                failWithMessage("Expected predicate <%s> not to match <%s>", actual, target);
            }
            return this;
        }

        public PredicatesAssert<T> doesNotMatch(T... targets) {
            Stream.of(targets).forEach(this::doesNotMatch);
            return this;
        }
    }

}