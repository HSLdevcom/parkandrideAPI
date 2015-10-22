// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class UtilizationHistoryList implements UtilizationHistory {
    private final List<Utilization> utilizationList;

    public UtilizationHistoryList(List<Utilization> utilizationList) {
        if (utilizationList == null || utilizationList.size() == 0)
            throw new IllegalArgumentException("utilizationList must not be null or empty.");
        this.utilizationList = utilizationList;
    }

    @Override
    public Utilization getLatest() {
        return utilizationList.stream().reduce(utilizationList.get(0), BinaryOperator.maxBy((a, b) -> a.timestamp.compareTo(b.timestamp)));
    }

    @Override
    public List<Utilization> getRange(DateTime startInclusive, DateTime endInclusive) {
        return utilizationList.stream()
                .filter(utilization -> !utilization.timestamp.isBefore(startInclusive) && !utilization.timestamp.isAfter(endInclusive))
                .collect(Collectors.toList());
    }

    @Override
    public CloseableIterator<Utilization> getUpdatesSince(DateTime startExclusive) {
        return new IteratorAdapter<Utilization>(
                utilizationList.stream()
                .filter(utilization -> utilization.timestamp.isAfter(startExclusive))
                .iterator()
        );
    }
}
