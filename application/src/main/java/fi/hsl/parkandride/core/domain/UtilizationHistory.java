// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import java.util.stream.Stream;

public interface UtilizationHistory {

    Stream<Utilization> getUpdatesSince(DateTime startExclusive);
}
