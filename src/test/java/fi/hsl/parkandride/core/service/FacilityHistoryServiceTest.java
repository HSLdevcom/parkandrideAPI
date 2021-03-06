// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import com.google.common.collect.ImmutableMap;
import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.FacilityStatus.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class FacilityHistoryServiceTest {

    private static final LocalDate START = LocalDate.now().minusMonths(1).dayOfMonth().withMinimumValue();
    private static final LocalDate NEXT = START.plusDays(1);
    private static final LocalDate END = START.withDayOfMonth(11);

    private static final DateTime START_DATETIME = START.toDateTimeAtStartOfDay();
    private static final DateTime NEXT_DATETIME = NEXT.toDateTimeAtStartOfDay();

    private static final MultilingualString STATUS_DESCRIPTION = new MultilingualString();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    FacilityHistoryService service;
    @Mock
    FacilityHistoryRepository repository;
    @Mock
    FacilityRepository facilityRepository;

    static final LocalTime midnight = LocalTime.MIDNIGHT;
    static final LocalTime am759 = new LocalTime("07:59");
    static final LocalTime am800 = new LocalTime("08:00");
    static final LocalTime am801 = new LocalTime("08:01");

    static final DateTime first    = START.toDateTime(midnight);
    static final DateTime second   = START.plusDays(1).toDateTime(am759);
    static final DateTime third    = START.plusDays(2).toDateTime(am800);
    static final DateTime fourth   = START.plusDays(3).toDateTime(am801);

    @Before
    public void setup() {
        service = new FacilityHistoryService(repository, facilityRepository);
        when(repository.getStatusHistory(anyLong(), any(), any())).thenReturn(emptyList());
    }

    @Test
    public void statusHistory_noHistory_hasInOperationForAllDays() {
        final Map<LocalDate, FacilityStatus> statusHistoryByDay = service.getStatusHistoryByDay(1l, START, END);
        assertThat(statusHistoryByDay.keySet()).hasSize(11);
        statusHistoryByDay.keySet().forEach(date -> assertThat(date).isLessThanOrEqualTo(END).isGreaterThanOrEqualTo(START));
        statusHistoryByDay.values().forEach(s -> assertThat(s).isEqualTo(IN_OPERATION));
    }

    @Test
    public void statusHistory_changedAtMidnight() {
        when(repository.getStatusHistory(1l, START, NEXT)).thenReturn(asList(
                new FacilityStatusHistory(1l, START_DATETIME, NEXT_DATETIME, EXCEPTIONAL_SITUATION, STATUS_DESCRIPTION),
                new FacilityStatusHistory(1l, NEXT_DATETIME, null, IN_OPERATION, STATUS_DESCRIPTION)
        ));

        final Map<LocalDate, FacilityStatus> statusHistoryByDay = service.getStatusHistoryByDay(1l, START, NEXT);
        assertThat(statusHistoryByDay).hasSize(2);
        assertThat(statusHistoryByDay)
                .containsEntry(START, EXCEPTIONAL_SITUATION)
                .containsEntry(NEXT, IN_OPERATION);
    }

    @Test
    public void statusHistory_mostEffectiveStatusSelected() {
        when(repository.getStatusHistory(1l, START, fourth.toLocalDate())).thenReturn(asList(
                new FacilityStatusHistory(1l, first,  second,  EXCEPTIONAL_SITUATION, STATUS_DESCRIPTION),
                new FacilityStatusHistory(1l, second,  third,  IN_OPERATION,          STATUS_DESCRIPTION),
                new FacilityStatusHistory(1l, third,  fourth,  TEMPORARILY_CLOSED,    STATUS_DESCRIPTION),
                new FacilityStatusHistory(1l, fourth,   null,  IN_OPERATION,          STATUS_DESCRIPTION)
        ));

        final Map<LocalDate, FacilityStatus> statusHistoryByDay = service.getStatusHistoryByDay(1l, START, fourth.toLocalDate());
        // First  -> Exceptional situation whole day
        // Second -> Exceptional situation end at 7.59 (1.59 in window 6-10)
        //           In operation for 2.01 of the significant window
        // Third  -> Temporarily closed begins at 8.00
        //           Equal periods in operation and closed, select first
        // Fourth -> Returns to operation at 8.01, temporarily closed for 2.01 of the window
        assertThat(statusHistoryByDay)
                .hasSize(4)
                .containsEntry(START, EXCEPTIONAL_SITUATION)
                .containsEntry(START.plusDays(1), IN_OPERATION)
                .containsEntry(START.plusDays(2), IN_OPERATION)
                .containsEntry(START.plusDays(3), TEMPORARILY_CLOSED);
    }

    @Test
    public void capacityHistory_hasKeysForAllDays_capacityFromCurrent_ifNoHistory() {
        Facility fac = returnFacilityWithDummyCapacities(dummyUnavailable());

        final Map<LocalDate, FacilityCapacity> historyByDate = service.getCapacityHistory(1l, START, END);
        assertThat(historyByDate.keySet()).hasSize(11);
        historyByDate.keySet().forEach(date -> assertThat(date).isLessThanOrEqualTo(END).isGreaterThanOrEqualTo(START));
        historyByDate.values().forEach(s -> assertThat(s).isEqualTo(new FacilityCapacity(fac.builtCapacity, fac.unavailableCapacities)));
    }

    @Test
    public void capacityHistory_changedAtMidnight() {
        returnFacilityWithDummyCapacities(dummyUnavailable());

        final FacilityCapacityHistory first = new FacilityCapacityHistory(1l, START_DATETIME, NEXT_DATETIME, null, null);
        final FacilityCapacityHistory second = new FacilityCapacityHistory(1l, NEXT_DATETIME, null, null, dummyUnavailable());
        when(repository.getCapacityHistory(1l, START, NEXT)).thenReturn(asList(first, second));

        final Map<LocalDate, FacilityCapacity> historyByDate = service.getCapacityHistory(1l, START, NEXT);
        assertThat(historyByDate).hasSize(2)
                .containsEntry(START, new FacilityCapacity(first))
                .containsEntry(NEXT, new FacilityCapacity(second));
    }

    @Test
    public void capacityHistory_mostEffectiveStatusSelected() {
        returnFacilityWithDummyCapacities(newArrayList());

        final FacilityCapacityHistory first = new FacilityCapacityHistory(1l, FacilityHistoryServiceTest.first, second, null, dummyUnavailable());
        final FacilityCapacityHistory second = new FacilityCapacityHistory(1l, FacilityHistoryServiceTest.second, third, null, dummyUnavailable());
        final FacilityCapacityHistory third = new FacilityCapacityHistory(1l, FacilityHistoryServiceTest.third, fourth, null, dummyUnavailable());
        final FacilityCapacityHistory fourth = new FacilityCapacityHistory(1l, FacilityHistoryServiceTest.fourth, null, null, dummyUnavailable());
        when(repository.getCapacityHistory(1l, START, FacilityHistoryServiceTest.fourth.toLocalDate())).thenReturn(asList(first, second, third, fourth));

        final Map<LocalDate, FacilityCapacity> statusHistoryByDay = service.getCapacityHistory(1l, START, FacilityHistoryServiceTest.fourth.toLocalDate());
        // First  -> Exceptional situation whole day
        // Second -> Exceptional situation end at 7.59 (1.59 in window 6-10)
        //           In operation for 2.01 of the significant window
        // Third  -> Temporarily closed begins at 8.00
        //           Equal periods in operation and closed, select first
        // Fourth -> Returns to operation at 8.01, temporarily closed for 2.01 of the window
        assertThat(statusHistoryByDay)
                .hasSize(4)
                .containsEntry(START, new FacilityCapacity(first))
                .containsEntry(START.plusDays(1), new FacilityCapacity(second))
                .containsEntry(START.plusDays(2), new FacilityCapacity(second))
                .containsEntry(START.plusDays(3), new FacilityCapacity(third));
    }

    private Facility returnFacilityWithDummyCapacities(List<UnavailableCapacity> capacities) {
        final Facility facility = new Facility();
        facility.builtCapacity = ImmutableMap.of(CAR, 50);
        facility.unavailableCapacities = capacities;
        when(facilityRepository.getFacility(1l)).thenReturn(facility);
        return facility;
    }

    private List<UnavailableCapacity> dummyUnavailable() {
        return singletonList(new UnavailableCapacity(
                randomFrom(CapacityType.class),
                randomFrom(Usage.class),
                nextInt(1, 10)
        ));
    }

    private <E extends Enum<E>, T extends Class<E>> E randomFrom(T clazz) {
        final E[] values = clazz.getEnumConstants();
        return values[nextInt(0, values.length)];
    }

}