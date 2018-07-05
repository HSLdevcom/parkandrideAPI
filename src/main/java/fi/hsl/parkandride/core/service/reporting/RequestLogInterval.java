// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service.reporting;

import fi.hsl.parkandride.core.domain.RequestLogKey;
import org.joda.time.DateTime;

public enum RequestLogInterval {
    HOUR {
        @Override
        public DateTime apply(DateTime dateTime) {
            return removeSmallPartials(dateTime);
        }
    },
    DAY {
        @Override
        public DateTime apply(DateTime dateTime) {
            return HOUR.apply(dateTime)
                    .millisOfDay().withMinimumValue();
        }
    },
    MONTH {
        @Override
        public DateTime apply(DateTime dateTime) {
            return DAY.apply(dateTime)
                    .dayOfMonth().withMinimumValue();
        }
    };

    public abstract DateTime apply(DateTime dateTime);
    public RequestLogKey apply(RequestLogKey key) {
        return new RequestLogKey(key.urlPattern, key.source, this.apply(key.timestamp));
    }

    private static DateTime removeSmallPartials(DateTime time) {
        return time.millisOfSecond().withMinimumValue()
                .secondOfMinute().withMinimumValue()
                .minuteOfHour().withMinimumValue();
    }
}
