// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import java.util.stream.Stream;

public interface UtilizationHistory {

    Stream<Utilization> getUpdatesSince(DateTime startExclusive);
}
