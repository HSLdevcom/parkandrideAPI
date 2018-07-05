// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import org.joda.time.DateTime;

public class TimeUtil {

    public static DateTime roundMinutes(int resolution, DateTime dateTime) {
        int minute = dateTime.getMinuteOfHour();
        int remainder = minute % resolution;
        int roundedRem = (int) (Math.round(((double) remainder) / resolution) * resolution);
        return dateTime.plusMinutes(roundedRem - remainder)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }
}
