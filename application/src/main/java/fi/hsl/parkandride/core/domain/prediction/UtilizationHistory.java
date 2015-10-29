// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.domain.Utilization;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public interface UtilizationHistory {

    Optional<Utilization> getLatest();

    List<Utilization> getRange(DateTime startInclusive, DateTime endInclusive);

    CloseableIterator<Utilization> getUpdatesSince(DateTime startExclusive);

    Optional<Utilization> getAt(DateTime timestamp);
}
