// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static fi.hsl.parkandride.util.Iterators.iterateFor;
import static org.assertj.core.api.Assertions.assertThat;

public class IteratorsTest {

    @Test
    public void testIterateFor() {
        final List<Integer> listOfIntegers = Lists.newArrayList(iterateFor(0, i -> i < 5, i -> i + 1));
        assertThat(listOfIntegers).containsExactly(0,1,2,3,4);
    }

}