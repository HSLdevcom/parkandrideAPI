// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class StrictIsoDateTimeDeserializerTest {

    private final StrictIsoDateTimeDeserializer dateTimeDeserializer = new StrictIsoDateTimeDeserializer();

    @Test
    @Parameters({
            "2015-04-12T18:51:19Z",         // UTC
            "2015-04-12T18:51:19+00:00",    // UTC in numeric, positive
            "2015-04-12T18:51:19-00:00",    // UTC in numeric, negative
            "2015-04-12T18:51:19+03:00",    // positive timezone offset
            "2015-04-12T18:51:19-03:00",    // negative timezone offset
            // fractions of second
            "2015-04-12T18:51:19.123Z",         // period separator
            "2015-04-12T18:51:19\\,123Z",       // comma separator
            "2015-04-12T18:51:19.1Z",           // fewer fractions
            "2015-04-12T18:51:19.123456789Z",   // more fractions
    })
    public void valid_formats(String str) {
        assertThat(dateTimeDeserializer.isValid(str)).as(str).isTrue();
    }

    @Test
    @Parameters({
            "1429178598",               // unix timestamp
            "1429178598648",            // unix timestamp in milliseconds
            "15-04-12T18:51:19Z",       // year with 2 numbers
            "20150412T185119Z",         // no date nor time separators
            "20150412T18:51:19Z",       // no date separators
            "2015-04-12T185119Z",       // no time separators
            "2015-04-12T18:51:19+0300", // no timezone separator
            "2015-04-12T18:51:19+03",   // no timezone minutes
            "2015-04-12T18:51:19.123",  // milliseconds, no timezone
            "2015-04-12T18:51:19",      // seconds, no timezone
            "2015-04-12T18:51Z",        // minutes
            "2015-04-12T18:51",         // minutes, no timezone
            "2015-04-12T18Z",           // hours
            "2015-04-12T18",            // hours, no timezone
            "2015-04-12",               // date
            "2015-W15",                 // week
            "2015-W15-7",               // date with week number
            "2015-102",                 // ordinal date
    })
    public void invalid_formats(String str) {
        assertThat(dateTimeDeserializer.isValid(str)).as(str).isFalse();
    }

    @Test
    public void null_formats() {
        assertThat(dateTimeDeserializer.isValid(null)).as("null").isTrue();
        assertThat(dateTimeDeserializer.isValid("")).as("empty string").isTrue();
    }
}
