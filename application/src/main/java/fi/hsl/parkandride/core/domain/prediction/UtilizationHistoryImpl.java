// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.mysema.commons.lang.CloseableIterator;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UtilizationHistoryImpl implements UtilizationHistory {

    private final UtilizationRepository utilizationRepository;
    private final UtilizationKey utilizationKey;

    public UtilizationHistoryImpl(UtilizationRepository utilizationRepository, UtilizationKey utilizationKey) {
        this.utilizationRepository = utilizationRepository;
        this.utilizationKey = utilizationKey;
    }

    @Override
    public Optional<Utilization> getLatest() {
        Set<Utilization> utilizations = utilizationRepository.findLatestUtilization(utilizationKey.facilityId);
        return utilizations.stream()
                .filter(u -> u.getUtilizationKey().equals(utilizationKey))
                .findAny();
    }

    @Override
    public List<Utilization> getRange(DateTime startInclusive, DateTime endInclusive) {
        return utilizationRepository.findUtilizationsWithResolution(utilizationKey, startInclusive, endInclusive, PredictionRepository.PREDICTION_RESOLUTION);
    }

    @Override
    public CloseableIterator<Utilization> getUpdatesSince(DateTime startExclusive) {
        DateTime start = startExclusive.plusMillis(1);
        DateTime end = new DateTime().plusYears(1);
        return utilizationRepository.findUtilizationsBetween(utilizationKey, start, end);
    }

    @Override
    public Optional<Utilization> getAt(DateTime instant) {
        return utilizationRepository.findUtilizationAtInstant(utilizationKey, instant);
    }
}
