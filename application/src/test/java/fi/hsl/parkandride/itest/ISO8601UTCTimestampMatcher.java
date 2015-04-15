// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ISO8601UTCTimestampMatcher extends TypeSafeMatcher<String> {
    public static String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN);
    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    protected boolean matchesSafely(String item) {
        try {
            FORMAT.parse(item);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("timestamp formatted as ").appendValue(PATTERN);
    }
}
