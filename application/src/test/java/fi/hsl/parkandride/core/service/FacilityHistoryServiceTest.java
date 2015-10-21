// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.domain.FacilityStatus;
import fi.hsl.parkandride.core.domain.FacilityStatusHistory;
import fi.hsl.parkandride.core.domain.MultilingualString;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;

import static fi.hsl.parkandride.core.domain.FacilityStatus.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class FacilityHistoryServiceTest {

    private static final LocalDate START = LocalDate.now().minusMonths(1).dayOfMonth().withMinimumValue();
    private static final LocalDate NEXT = START.plusDays(1);
    private static final LocalDate END = START.withDayOfMonth(11);
    private static final MultilingualString STATUS_DESCRIPTION = new MultilingualString();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    FacilityHistoryService service;
    @Mock
    FacilityHistoryRepository repository;

    static final LocalTime midnight = LocalTime.MIDNIGHT;
    static final LocalTime am759 = new LocalTime("07:59");
    static final LocalTime am800 = new LocalTime("08:00");
    static final LocalTime am801 = new LocalTime("08:01");

    @Before
    public void setup() {
        service = new FacilityHistoryService(repository);
        when(repository.getStatusHistory(anyLong(), any(), any())).thenReturn(emptyList());
    }

    @Test
    public void hasKeysForAllDays_statusInOperationIfNoHistory() {
        final Map<LocalDate, FacilityStatus> statusHistoryByDay = service.getStatusHistoryByDay(1l, START, END);
        assertThat(statusHistoryByDay.keySet()).hasSize(11);
        statusHistoryByDay.keySet().forEach(date -> assertThat(date).isLessThanOrEqualTo(END).isGreaterThanOrEqualTo(START));
        statusHistoryByDay.values().forEach(s -> assertThat(s).isEqualTo(IN_OPERATION));
    }

    @Test
    public void showsStatusHistory_statusChangedAtMidnight() {
        when(repository.getStatusHistory(1l, START, NEXT)).thenReturn(asList(
                new FacilityStatusHistory(1l, START.toDateTimeAtStartOfDay(), NEXT.toDateTimeAtStartOfDay(), EXCEPTIONAL_SITUATION, STATUS_DESCRIPTION),
                new FacilityStatusHistory(1l, START.toDateTimeAtStartOfDay().plusDays(1), null, IN_OPERATION, STATUS_DESCRIPTION)
        ));

        final Map<LocalDate, FacilityStatus> statusHistoryByDay = service.getStatusHistoryByDay(1l, START, NEXT);
        assertThat(statusHistoryByDay).hasSize(2);
        assertThat(statusHistoryByDay)
                .containsEntry(START, EXCEPTIONAL_SITUATION)
                .containsEntry(NEXT, IN_OPERATION);
    }

    @Test
    public void selectsTheStatusThatWasMostInActionFrom6to10am() {
        final DateTime first    = START.toDateTime(midnight);
        final DateTime second   = START.plusDays(1).toDateTime(am759);
        final DateTime third    = START.plusDays(2).toDateTime(am800);
        final DateTime fourth   = START.plusDays(3).toDateTime(am801);

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
}