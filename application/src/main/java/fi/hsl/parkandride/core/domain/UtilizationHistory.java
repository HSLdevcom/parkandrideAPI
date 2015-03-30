// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import java.util.stream.Stream;

public interface UtilizationHistory {

    boolean hasUpdatesSince(DateTime startExclusive);

    Stream<Utilization> getUpdatesSince(DateTime startExclusive);
}
