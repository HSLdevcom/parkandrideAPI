// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.*;

import java.util.Optional;

public enum DayType {
    BUSINESS_DAY,
    SATURDAY,
    SUNDAY;

    public static DayType valueOf(ReadableInstant timestamp) {
        return valueOf(Optional.ofNullable(timestamp).map(ts -> ts.get(DateTimeFieldType.dayOfWeek())).orElse(null));
    }

    public static DayType valueOf(ReadablePartial timestamp) {
        return valueOf(Optional.ofNullable(timestamp).map(ts -> ts.get(DateTimeFieldType.dayOfWeek())).orElse(null));
    }

    private static DayType valueOf(Integer dayOfWeek) {
        switch (dayOfWeek) {
        case DateTimeConstants.SATURDAY:
            return SATURDAY;
        case DateTimeConstants.SUNDAY:
            return SUNDAY;
        default:
            return BUSINESS_DAY;
        }
    }

}
