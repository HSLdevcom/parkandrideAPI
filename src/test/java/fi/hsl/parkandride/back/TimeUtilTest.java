// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TimeUtilTest {

    @Test
    @Parameters(method = "parametersFor5minResolution")
    @TestCaseName("[{index}] {2}: {0} -> {1}")
    public void rounds_minutes_to_5min_resolution(DateTime input, DateTime expected, String message) {
        assertThat(TimeUtil.roundMinutes(5, input)).as(message).isEqualTo(expected);
    }

    @SuppressWarnings("UnusedDeclaration")
    private Object[] parametersFor5minResolution() {
        return new Object[][]{
                // rounding
                {new DateTime(2000, 1, 1, 12, 0), new DateTime(2000, 1, 1, 12, 0), "noop"},
                {new DateTime(2000, 1, 1, 12, 2), new DateTime(2000, 1, 1, 12, 0), "round down 1"},
                {new DateTime(2000, 1, 1, 12, 3), new DateTime(2000, 1, 1, 12, 5), "round up 1"},
                {new DateTime(2000, 1, 1, 12, 47), new DateTime(2000, 1, 1, 12, 45), "round down 2"},
                {new DateTime(2000, 1, 1, 12, 48), new DateTime(2000, 1, 1, 12, 50), "round up 2"},
                {new DateTime(2000, 1, 1, 12, 59), new DateTime(2000, 1, 1, 13, 0), "round up to next hour"},
                {new DateTime(2000, 1, 1, 23, 59), new DateTime(2000, 1, 2, 0, 0), "round up to next day"},
                // dropping
                {new DateTime(2000, 1, 1, 12, 0, 30), new DateTime(2000, 1, 1, 12, 0, 0), "drops seconds"},
                {new DateTime(2000, 1, 1, 12, 0, 0, 500), new DateTime(2000, 1, 1, 12, 0, 0), "drops milliseconds"},
        };
    }
}
