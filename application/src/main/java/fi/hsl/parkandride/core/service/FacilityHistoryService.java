// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.domain.FacilityStatus;
import fi.hsl.parkandride.core.domain.FacilityStatusHistory;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static fi.hsl.parkandride.util.Iterators.iterateFor;
import static java.util.stream.Collectors.reducing;

public class FacilityHistoryService {

    private static final LocalTime WINDOW_START = new LocalTime("06:00");
    private static final LocalTime WINDOW_END = new LocalTime("10:00");
    private static final FacilityStatusHistory IDENTITY_STATUS = new FacilityStatusHistory(null, null, null, FacilityStatus.IN_OPERATION, null);

    private final FacilityHistoryRepository facilityHistoryRepository;

    public FacilityHistoryService(FacilityHistoryRepository facilityHistoryRepository) {
        this.facilityHistoryRepository = facilityHistoryRepository;
    }

    /**
     * Deduces the status history for the given date range.
     * If the status changes during the day, the status that was mostly active
     * during the period of 6 to 10 a.m.
     * @return
     */
    @TransactionalRead
    public Map<LocalDate, FacilityStatus> getStatusHistoryByDay(final long facilityId, final LocalDate start, final LocalDate end) {
        final List<FacilityStatusHistory> statusHistory = facilityHistoryRepository.getStatusHistory(facilityId, start, end);
        return Maps.toMap(dateRangeClosed(start, end), (LocalDate date) -> {
            final Interval significantWindow = windowForDate(date);
            final FacilityStatusHistory acceptedEntry = statusHistory.stream()
                    .filter(e -> intervalFor(e).overlaps(date.toInterval()))
                    .collect(reducing(IDENTITY_STATUS, greatestOverlap(significantWindow)));
            return acceptedEntry.status;
        });
    }

    private static BinaryOperator<FacilityStatusHistory> greatestOverlap(Interval significantWindow) {
        return (h1, h2) -> {
            final Duration h1overlap = overlappingDuration(significantWindow, intervalFor(h1));
            final Duration h2overlap = overlappingDuration(significantWindow, intervalFor(h2));
            return h1overlap.compareTo(h2overlap) >= 0 ? h1 : h2;
        };
    }

    private static Duration overlappingDuration(Interval significantWindow, Interval interval) {
        return Optional.ofNullable(interval.overlap(significantWindow)).map(Interval::toDuration).orElse(Duration.millis(0));
    }

    private static Interval windowForDate(LocalDate date) {
        return new Interval(
                date.toDateTime(WINDOW_START),
                date.toDateTime(WINDOW_END)
        );
    }

    private static Interval intervalFor(FacilityStatusHistory entry) {
        return new Interval(entry.startDate, entry.endDate);
    }

    private static List<LocalDate> dateRangeClosed(LocalDate start, LocalDate end) {
        return ImmutableList.copyOf(iterateFor(start, d -> !d.isAfter(end), d -> d.plusDays(1)));
    }

}
