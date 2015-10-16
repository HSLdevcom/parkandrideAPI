// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.util.function.Supplier;

public final class DateTimeTestUtils {
    private DateTimeTestUtils() { /** prevent instantiation */}

    public static <V> V withDate(DateTime date, Supplier<V> r) {
        DateTimeUtils.setCurrentMillisFixed(date.getMillis());
        final V v = r.get();
        DateTimeUtils.setCurrentMillisSystem();
        return v;
    }

    public static void withDate(DateTime date, Runnable r) {
        withDate(date, () -> {
            r.run();
            return null;
        });
    }
}
