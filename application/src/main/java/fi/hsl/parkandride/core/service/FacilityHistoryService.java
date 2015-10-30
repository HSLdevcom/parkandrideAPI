// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static fi.hsl.parkandride.util.Iterators.iterateFor;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.reducing;

public class FacilityHistoryService {

    private static final LocalTime WINDOW_START = new LocalTime("06:00");
    private static final LocalTime WINDOW_END = new LocalTime("10:00");
    private static final FacilityStatusHistory IDENTITY_STATUS = new FacilityStatusHistory(null, null, null, FacilityStatus.IN_OPERATION, null);

    private final FacilityHistoryRepository facilityHistoryRepository;
    private final FacilityRepository facilityRepository;

    public FacilityHistoryService(FacilityHistoryRepository facilityHistoryRepository, FacilityRepository facilityRepository) {
        this.facilityHistoryRepository = facilityHistoryRepository;
        this.facilityRepository = facilityRepository;
    }

    /**
     * Deduces the status history for the given date range.
     * If the status changes during the day, the status that was mostly active
     * during the period of 6 to 10 a.m. is selected.
     */
    @TransactionalRead
    public Map<LocalDate, FacilityStatus> getStatusHistoryByDay(final long facilityId, final LocalDate start, final LocalDate end) {
        final List<FacilityStatusHistory> statusHistory = facilityHistoryRepository.getStatusHistory(facilityId, start, end);
        return Maps.toMap(dateRangeClosed(start, end), date -> findEntryForDate(statusHistory, date, IDENTITY_STATUS).status);
    }

    /**
     * Deduces the unavailable capacities history for the given date range.
     * If the unavailable capacities change during the day, the capacity that was
     * mostly active during the the period of 6 to 10 am. is selected.
     */
    @TransactionalRead
    public Map<LocalDate, FacilityCapacity> getCapacityHistory(final long facilityId, final LocalDate start, final LocalDate end) {
        final List<FacilityCapacityHistory> capacityHistory = facilityHistoryRepository.getCapacityHistory(facilityId, start, end);

        // Fall back to current unavailable capacities
        final Facility facility = facilityRepository.getFacility(facilityId);
        final FacilityCapacityHistory identity = new FacilityCapacityHistory(
                null, null, null,
                Optional.ofNullable(facility.builtCapacity).orElse(emptyMap()),
                Optional.ofNullable(facility.unavailableCapacities).orElse(emptyList())
        );

        return Maps.toMap(dateRangeClosed(start, end), date -> new FacilityCapacity(findEntryForDate(capacityHistory, date, identity)));
    }

    private static <T extends HasInterval> T findEntryForDate(List<T> entries, LocalDate date, T identity) {
        final Interval significantWindow = windowForDate(date);
        return entries.stream()
                .filter(e -> e.getInterval().overlaps(date.toInterval()))
                .collect(reducing(identity, greatestOverlap(significantWindow)));
    }

    private static <T extends HasInterval> BinaryOperator<T> greatestOverlap(Interval significantWindow) {
        return (h1, h2) -> {
            final Duration h1overlap = h1.overlapWith(significantWindow);
            final Duration h2overlap = h2.overlapWith(significantWindow);
            return h1overlap.compareTo(h2overlap) >= 0 ? h1 : h2;
        };
    }

    private static Interval windowForDate(LocalDate date) {
        return new Interval(
                date.toDateTime(WINDOW_START),
                date.toDateTime(WINDOW_END)
        );
    }

    private static List<LocalDate> dateRangeClosed(LocalDate start, LocalDate end) {
        return ImmutableList.copyOf(iterateFor(start, d -> !d.isAfter(end), d -> d.plusDays(1)));
    }

}
