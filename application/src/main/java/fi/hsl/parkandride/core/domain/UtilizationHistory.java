// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import com.mysema.commons.lang.CloseableIterator;

public interface UtilizationHistory {

    CloseableIterator<Utilization> getUpdatesSince(DateTime startExclusive);
}
