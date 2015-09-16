// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import org.assertj.core.api.ObjectAssert;
import org.joda.time.Duration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.Duration.*;

public class PredictionRequestTest {

    @Test
    public void relative_time_as_hours_and_minutes() {
        assertRelativeTime("00:00").isEqualTo(ZERO);
        assertRelativeTime("00:15").isEqualTo(standardMinutes(15));
        assertRelativeTime("15:00").isEqualTo(standardHours(15));

        // preceding zeroes are optional
        assertRelativeTime("1:2").isEqualTo(standardHours(1).plus(standardMinutes(2)));

        // there must be 60 minutes in an hour
        assertRelativeTime("00:59").isEqualTo(standardMinutes(59));
        assertRelativeTime("00:60").isEqualTo(standardMinutes(60));
        assertRelativeTime("00:99").isEqualTo(standardMinutes(99));
        assertRelativeTime("01:00").isEqualTo(standardMinutes(60));
    }

    @Test
    public void relative_time_as_minutes() {
        assertRelativeTime("0000").isEqualTo(ZERO);
        assertRelativeTime("0015").isEqualTo(standardMinutes(15));
        assertRelativeTime("1500").isEqualTo(standardMinutes(1500));

        // preceding zeroes are optional
        assertRelativeTime("1").isEqualTo(standardMinutes(1));

        // there are no hours
        assertRelativeTime("0059").isEqualTo(standardMinutes(59));
        assertRelativeTime("0060").isEqualTo(standardMinutes(60));
        assertRelativeTime("0099").isEqualTo(standardMinutes(99));
        assertRelativeTime("0100").isEqualTo(standardMinutes(100));
    }

    public static ObjectAssert<Duration> assertRelativeTime(String relativeTime) {
        return assertThat(PredictionRequest.parseRelativeTime(relativeTime)).as(relativeTime);
    }
}
