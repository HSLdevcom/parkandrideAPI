// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public enum DayType {
    BUSINESS_DAY,
    SATURDAY,
    SUNDAY;

    public static DayType valueOf(DateTime timestamp) {
        switch (timestamp.getDayOfWeek()) {
        case DateTimeConstants.SATURDAY:
            return SATURDAY;
        case DateTimeConstants.SUNDAY:
            return SUNDAY;
        default:
            return BUSINESS_DAY;
        }
    }
}
