// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

public class UtilizationHistoryImpl implements UtilizationHistory {

    private final UtilizationRepository utilizationRepository;
    private final UtilizationKey utilizationKey;

    public UtilizationHistoryImpl(UtilizationRepository utilizationRepository, UtilizationKey utilizationKey) {
        this.utilizationRepository = utilizationRepository;
        this.utilizationKey = utilizationKey;
    }

    @Override
    public CloseableIterator<Utilization> getUpdatesSince(DateTime startExclusive) {
        DateTime start = startExclusive.plusMillis(1);
        DateTime end = new DateTime().plusYears(1);
        return utilizationRepository.findUtilizationsBetween(utilizationKey, start, end);
    }
}
