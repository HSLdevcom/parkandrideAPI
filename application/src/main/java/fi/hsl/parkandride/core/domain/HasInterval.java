// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Optional;

public interface HasInterval {

    DateTime getStart();
    DateTime getEnd();

    default Interval getInterval() {
        return new Interval(getStart(), getEnd());
    }

    default Duration overlapWith(Interval interval) {
        return Optional.ofNullable(interval)
                .map(i -> getInterval().overlap(i))
                .map(Interval::toDuration)
                .orElse(Duration.ZERO);
    }
}
