// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.FacilityStatus.*;
import static fi.hsl.parkandride.core.service.reporting.ExcelUtil.time;
import static fi.hsl.parkandride.itest.FacilityUsageReportITest.ListBuilder.listFrom;
import static fi.hsl.parkandride.test.DateTimeTestUtils.withDate;
import static fi.hsl.parkandride.util.Iterators.iterateFor;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class FacilityUsageReportITest extends AbstractReportingITest {

    private static final int FACILITYUSAGE_FIRST_TIME_COLUMN = 12;
    private static final String FACILITY_USAGE = "FacilityUsage";
    private static final int MILLIS_IN_MINUTE = 60 * 1000;

    // Empty at 00:00, full at 12:00
    private static final BiFunction<Integer, Integer, Integer> SPACES_AT = (capacity, hour) -> (int)(capacity * ((Math.abs(hour - 12) / 12.0)));

    // ---------------------
    // FACILITY USAGE REPORT
    // ---------------------
    @Before
    @Override
    public void initialize() {
        // Needed to ensure history linearity
        withDate(initial, this::initFixture);
    }

    @Test
    public void report_FacilityUsage_asAdmin_oneFacility() {
        final ReportParameters params = baseParams();
        params.interval = 60;
        params.facilities = singleton(facility1.id);
        params.capacityTypes = singleton(CAR);
        params.usages = singleton(Usage.PARK_AND_RIDE);
        params.operators = singleton(facility1.operatorId);
        registerMockFacilityUsages(facility1, apiUser, 24, 2);
        registerMockFacilityUsages(facility2, apiUser2, 24, 2);

        final Response whenPostingToReportUrl = postToReportUrl(params, FACILITY_USAGE, adminUser);

        checkSheetContents(whenPostingToReportUrl, 0,
                headersWithTimes(params.interval),
                facilityRow(facility1, operator1, hub, Usage.PARK_AND_RIDE, CAR, params.interval, 24)
        );
    }

    @Test
    public void report_FacilityUsage_asAdmin() {
        final ReportParameters params = baseParams();
        params.interval = 24*60;
        registerMockFacilityUsages(facility1, apiUser, 24, 2);
        registerMockFacilityUsages(facility2, apiUser2, 24, 2);

        final Response whenPostingToReportUrl = postToReportUrl(params, FACILITY_USAGE, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headersWithTimes(params.interval),
                facilityRow(facility1, operator1, hub, facility1.usages.first(), CAR, params.interval, 24),
                facilityRow(facility1, operator1, hub, facility1.usages.first(), ELECTRIC_CAR, params.interval, 2),
                facilityRow(facility2, operator2, hub, facility2.usages.first(), CAR, params.interval, 24),
                facilityRow(facility2, operator2, hub, facility2.usages.first(), ELECTRIC_CAR, params.interval, 2)
        );
    }

    @Test
    public void report_FacilityUsage_asOperator2() {
        final ReportParameters params = baseParams();
        params.interval = 3*60;
        registerMockFacilityUsages(facility1, apiUser, 24, 2);
        registerMockFacilityUsages(facility2, apiUser2, 24, 2);

        final Response whenPostingToReportUrl = postToReportUrl(params, FACILITY_USAGE, operator2User);
        withWorkbook(whenPostingToReportUrl, workbook -> {
            assertThat(getSheetNames(workbook)).containsExactly("Täyttöasteraportti", "Selite");

            checkSheetContents(workbook, 0,
                    headersWithTimes(params.interval),
                    // No facility 1 as this is for different operator
                    facilityRow(facility2, operator2, hub, facility2.usages.first(), CAR, params.interval, 24),
                    facilityRow(facility2, operator2, hub, facility2.usages.first(), ELECTRIC_CAR, params.interval, 2)
            );
        });
    }

    @Test
    public void report_FacilityUsage_withStatusHistory() {
        final ReportParameters params = baseParams();
        params.interval = Days.ONE.toStandardMinutes().getMinutes();
        final LocalDate start = params.startDate;

        // Add status history, relates to the time window of 6-10
        // 1st day -> IN_OPERATION
        // 2nd and 3rd day -> TEMPORARILY_CLOSED
        // 4th day -> INACTIVE
        // 5th day onwards -> IN_OPERATION
        updateStatus(start.toDateTime(new LocalTime("07:00")), IN_OPERATION);
        updateStatus(start.plusDays(1).toDateTime(new LocalTime("07:59")), TEMPORARILY_CLOSED);
        updateStatus(start.plusDays(2).toDateTime(new LocalTime("08:00")), INACTIVE);
        updateStatus(start.plusDays(3).toDateTime(new LocalTime("08:01")), IN_OPERATION);

        // Add data for whole month to get all rows
        final List<LocalDate> dates = newArrayList(iterateFor(start, p -> !p.isAfter(params.endDate), d -> d.plusDays(1)));
        dates.forEach(d -> facilityService.registerUtilization(facility1.id, singletonList(
                utilize(CAR, 10, d.toDateTime(new LocalTime("13:37")), facility1)
        ), apiUser));

        final Response whenPostingToReportUrl = postToReportUrl(params, FACILITY_USAGE, adminUser);
        withWorkbook(whenPostingToReportUrl, wb -> {
            Sheet sheet = wb.getSheetAt(0);
            final List<String> expectedStates = listFrom("Status")
                    .add(translationService.translate(IN_OPERATION))
                    .addTimes(2, translationService.translate(TEMPORARILY_CLOSED))
                    .add(translationService.translate(INACTIVE))
                    .addTimes(dates.size() - 4, translationService.translate(IN_OPERATION))
                    .build();
            assertThat(getDataFromColumn(sheet, ReportColumns.DATE))
                    .containsSequence(dates.stream().map(d -> d.toString("d.M.yyyy")).toArray(String[]::new));
            assertThat(getDataFromColumn(sheet, ReportColumns.STATUS))
                    .containsExactlyElementsOf(expectedStates);
        });
    }

    private void updateStatus(DateTime dateTime, FacilityStatus status) {
        withDate(dateTime, () -> {
            facility1.status = status;
            facility1.builtCapacity = ImmutableMap.of(CAR, 50);
            facilityService.updateFacility(facility1.id, facility1, adminUser);
        });
    }

    static class ListBuilder<T> {

        private final List<T> list = new ArrayList<>();

        public static <T> ListBuilder<T> listFrom(T... items) {
            final ListBuilder<T> builder = new ListBuilder<>();
            return builder.addAll(items);
        }

        public ListBuilder<T> add(T item) {
            list.add(item);
            return this;
        }

        public ListBuilder<T> addAll(T... items) {
            Arrays.stream(items).forEach(this::add);
            return this;
        }

        public ListBuilder<T> addTimes(int times, T item) {
            IntStream.range(0, times).forEach(i -> list.add(item));
            return this;
        }

        public List<T> build() {
            return list;
        }
    }


    private List<String> headersWithTimes(int intervalMinutes) {
        final Stream<String> times = getReportTimes(intervalMinutes).map(t -> t.toString("HH:mm"));
        return Stream.concat(headersWithoutTimes().stream(), times).collect(toList());
    }

    private List<String> facilityRow(Facility f, Operator o, Hub hub, Usage usage, CapacityType type, int intervalMinutes, Integer maxSpacesAvailable) {
        final List<String> cells = newArrayList(
                f.name.fi,
                hub.name.fi,
                "Helsinki",
                o.name.fi,
                translationService.translate(usage),
                translationService.translate(type),
                translationService.translate(f.status),
                firstNonNull(time(f.openingHours.byDayType.get(DayType.BUSINESS_DAY)), ""),
                firstNonNull(time(f.openingHours.byDayType.get(DayType.SATURDAY)), ""),
                firstNonNull(time(f.openingHours.byDayType.get(DayType.SUNDAY)), ""),
                f.builtCapacity.get(type).toString(),
                BASE_DATE.toString(DATE_FORMAT)
        );
        cells.addAll(getReportTimes(intervalMinutes)
                // Get the latest utilization value for each window, e.g., for 00:00-03:00, the value is from 02:00
                .map(time -> time.plusMinutes(intervalMinutes).minusHours(1))
                .map(time -> SPACES_AT.apply(maxSpacesAvailable, time.getHourOfDay()))
                .map(String::valueOf)
                .collect(toList()));
        return cells;
    }

    private List<String> headersWithoutTimes() {
        return asList("Pysäköintipaikan nimi",
                "Alue",
                "Kunta",
                "Operaattori",
                "Käyttötapa",
                "Ajoneuvotyyppi",
                "Status",
                "Aukiolo, arki",
                "Aukiolo, la",
                "Aukiolo, su",
                "Pysäköintipaikkojen määrä",
                "Päivämäärä"
        );
    }

    private Stream<LocalTime> getReportTimes(int intervalMinutes) {
        return newArrayList(iterateFor(0, i -> i < 24 * 60 * MILLIS_IN_MINUTE, i -> i += intervalMinutes * MILLIS_IN_MINUTE))
                .stream()
                .map(LocalTime::fromMillisOfDay);
    }

    private void registerMockFacilityUsages(Facility facility, User user, int spacesAvailableCar, int spacesAvailableElectric) {
        DateTime startOfDay = BASE_DATE.toDateTimeAtStartOfDay();
        for (int i = 0; i < 24; i++) {
            facilityService.registerUtilization(facility.id, asList(
                    utilize(CAR, SPACES_AT.apply(spacesAvailableCar, i), startOfDay.withHourOfDay(i), facility),
                    utilize(ELECTRIC_CAR, SPACES_AT.apply(spacesAvailableElectric, i), startOfDay.withHourOfDay(i), facility)
            ), user);
        }
    }

    private interface ReportColumns {
        int STATUS = 6;
        int DATE = 11;
    }
}
