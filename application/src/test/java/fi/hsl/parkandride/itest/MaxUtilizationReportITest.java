// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.FacilityHistoryService;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE_SECURE_SPACE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static fi.hsl.parkandride.test.DateTimeTestUtils.withDate;
import static fi.hsl.parkandride.util.MapUtils.consumingEntry;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeConstants.MONDAY;

public class MaxUtilizationReportITest extends AbstractReportingITest {
    private static final String MAX_UTILIZATION = "MaxUtilization";

    // Day 15 to ensure that weekdays stay in the same month
    private static final DateTime baseDate = BASE_DATE.toDateTime(new LocalTime("7:59")).withDayOfMonth(15);
    private static final DateTime mon = baseDate.withDayOfWeek(MONDAY);
    private static final DateTime tue = mon.plusDays(1);
    private static final DateTime wed = mon.plusDays(2);
    private static final DateTime fri = mon.plusDays(4);
    private static final DateTime sat = mon.plusDays(5);
    private static final DateTime sun = mon.plusDays(6);
    private static final DateTime initial = mon.minusMonths(1);
    private static final int UNAVAILABLE_COLUMN = 6;

    @Inject FacilityHistoryRepository facilityHistoryRepository;
    @Inject FacilityHistoryService facilityHistoryService;
    @Inject FacilityRepository facilityRepository;

    // ---------------------
    // MAX UTILIZATION REPORT
    // ---------------------

    @Before
    @Override
    public void initialize() {
        // Needed to ensure history linearity
        withDate(initial, () -> {
            this.initFixture();

            // Clear the unavailable capacities history
            facility1.status = FacilityStatus.IN_OPERATION;
            facility2.status = FacilityStatus.IN_OPERATION;
            facility1.unavailableCapacities = newArrayList();
            facility2.unavailableCapacities = newArrayList();
            facilityRepository.updateFacility(facility1.id, facility1);
            facilityRepository.updateFacility(facility2.id, facility2);
        });
        facility1 = facilityRepository.getFacility(facility1.id);
        facility2 = facilityRepository.getFacility(facility2.id);
    }

    @Test
    public void report_MaxUtilization_calculatedCorrectly() {
        // From specs
        // Käyttäjä on valinnut 3 päivän ajanjakson tarkasteltavaksi. Jokaiselta päivältä etsitään maksimitäyttöaste (vähiten vapaita paikkoja tarjolla).
        // Ma - 70%; Ti - 100%; Ke - 80%
        // Näistä lasketaan keskimääräinen maksimitäyttöaste päivätyypeittäin. Valitulla jaksolla vain arkipäiviä, joten saadaan tulokseksi,
        // että keskimääräinen maksimitäyttöaste arkena oli (70% + 100% + 80%) / 3 = 83,33%

        // Fac1 + Fac2 total capacity = 100 -> easy
        facilityService.registerUtilization(facility1.id, asList(
                // Only the "min available space" rows are taken into account
                utilize(CAR, 0, mon, facility1),
                utilize(CAR, 0, tue, facility1),
                utilize(CAR, 0, wed, facility1),

                // These are left out, no effect on the calculation
                utilize(CAR, nextInt(10, 50), mon.plusMinutes(1), facility1),
                utilize(CAR, nextInt(10, 50), tue.plusMinutes(1), facility1),
                utilize(CAR, nextInt(10, 50), wed.plusMinutes(1), facility1)
        ), apiUser);

        facilityService.registerUtilization(facility2.id, asList(
                // Only the "min available space" rows are taken into account
                utilize(CAR, 30, mon, facility2),
                utilize(CAR, 0, tue, facility2),
                utilize(CAR, 20, wed, facility2),

                // These are left out, no effect on the calculation
                utilize(CAR, nextInt(30, 50), mon.plusMinutes(1), facility2),
                utilize(CAR, nextInt(30, 50), tue.plusMinutes(1), facility2),
                utilize(CAR, nextInt(30, 50), wed.plusMinutes(1), facility2)
        ), apiUser2);

        final ReportParameters params = baseParams();
        params.startDate = mon.toLocalDate();
        params.endDate = wed.plusDays(1).toLocalDate();
        final Response whenPostingToReportUrl = postToReportUrl(params, MAX_UTILIZATION, adminUser);

        // Only one row
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 0.8333)
        );
    }

    @Test
    public void report_MaxUtilization_asOperator() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, operator1User);

        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {
            assertThat(getSheetNames(workbook)).containsExactly("Tiivistelmäraportti", "Selite");

            checkSheetContents(workbook, 0,
                    headers(),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 50, 0, 1.0),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.SATURDAY, 50, 0, 0.5),
                    hubRow(operator1, PARK_AND_RIDE, CAR, DayType.SUNDAY, 50, 0, 0.2)
            );
        });
    }

    @Test
    public void report_MaxUtilization_asAdmin() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);

        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 100, 0, 0.5),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY, 100, 0, 0.2)
        );
    }

    @Test
    public void report_MaxUtilization_excludeDatesByExceptionalStatus() {
        addMockMaxUtilizations(facility1, apiUser, 50, 50, 50);
        addMockMaxUtilizations(facility2, apiUser2, 0, 0, 0);

        // Sun-Tue -> Inactive; Tue-Mon -> Closed
        facilityHistoryRepository.updateStatusHistory(mon.minusDays(1).toDateTime(), facility1.id, FacilityStatus.INACTIVE, new MultilingualString(""));
        facilityHistoryRepository.updateStatusHistory(tue.toDateTime(), facility1.id, FacilityStatus.TEMPORARILY_CLOSED, new MultilingualString(""));
        facilityHistoryRepository.updateStatusHistory(sun.plusDays(1).toDateTime(), facility1.id, FacilityStatus.IN_OPERATION, new MultilingualString(""));

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);

        // All 100 places, since facility 1 + facility 2 are calculated regardless of status
        // 100% utilization, since facility 2 is fully utilized every day
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 100, 0, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY, 100, 0, 1.0)
        );
    }

    @Test
    public void report_MaxUtilization_allExcluded() {
        addMockMaxUtilizations(facility1, apiUser, 50, 50, 50);
        addMockMaxUtilizations(facility2, apiUser2, 50, 50, 50);
        facilityHistoryRepository.updateStatusHistory(mon.minusDays(1).toDateTime(), facility1.id, FacilityStatus.INACTIVE, new MultilingualString(""));
        facilityHistoryRepository.updateStatusHistory(sun.plusDays(1).toDateTime(), facility1.id, FacilityStatus.IN_OPERATION, new MultilingualString(""));
        facilityHistoryRepository.updateStatusHistory(mon.minusDays(1).toDateTime(), facility2.id, FacilityStatus.INACTIVE, new MultilingualString(""));
        facilityHistoryRepository.updateStatusHistory(sun.plusDays(1).toDateTime(), facility2.id, FacilityStatus.IN_OPERATION, new MultilingualString(""));

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 0.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 100, 0, 0.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY, 100, 0, 0.0)
        );
    }

    @Test
    public void report_MaxUtilization_unavailableCapacities() {
        addMockMaxUtilizations(facility1, apiUser, 0, 0, 0);

        facilityHistoryRepository.updateCapacityHistory(mon, facility1.id, facility1.builtCapacity, singletonList(new UnavailableCapacity(CAR, PARK_AND_RIDE, 10)));
        facilityHistoryRepository.updateCapacityHistory(tue, facility1.id, facility1.builtCapacity, singletonList(new UnavailableCapacity(CAR, PARK_AND_RIDE, 15)));
        facilityHistoryRepository.updateCapacityHistory(wed, facility1.id, facility1.builtCapacity, singletonList(new UnavailableCapacity(CAR, PARK_AND_RIDE, 5)));
        facilityHistoryRepository.updateCapacityHistory(fri, facility1.id, facility1.builtCapacity, singletonList(new UnavailableCapacity(CAR, PARK_AND_RIDE, 7)));
        facilityHistoryRepository.updateCapacityHistory(sat.withTime(18, 0, 0, 0), facility1.id, facility1.builtCapacity, emptyList());

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 50, 15, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 50, 7, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY, 50, 0, 1.0)
        );
    }

    @Test
    public void report_MaxUtilization_unavailableCapacities_withLotsOfData() {
        Facility f = withDate(DateTime.parse("2015-08-01T00:00:00.000"), () -> {
            final Facility fac = dummies.createFacility(operator1.id, facility1.contacts);
            fac.status = FacilityStatus.IN_OPERATION;
            return facilityRepository.getFacility(facilityRepository.insertFacility(fac));
        });
        hub.facilityIds = singleton(f.id);
        hubService.updateHub(hub.id, hub, adminUser);

        Stream.of(
                mockUnavailable("2015-09-01T01:00:00.000", 1),
                mockUnavailable("2015-09-25T06:30:00.000", 0),
                mockUnavailable("2015-09-27T06:30:00.000", 0),
                mockUnavailable("2015-10-02T07:50:00.000", 5),
                mockUnavailable("2015-10-03T07:50:00.000", 10),
                mockUnavailable("2015-10-04T07:50:00.000", 49),
                mockUnavailable("2015-10-09T06:01:00.000", 10),
                mockUnavailable("2015-10-10T06:01:00.000", 1),
                mockUnavailable("2015-10-11T06:01:00.000", 0),
                mockUnavailable("2015-10-16T06:15:00.000", 1),
                mockUnavailable("2015-10-16T07:15:00.000", 10),
                mockUnavailable("2015-10-16T08:30:00.000", 19),
                mockUnavailable("2015-10-16T09:00:00.000", 1),
                mockUnavailable("2015-10-17T07:59:00.000", 20),
                mockUnavailable("2015-10-18T07:59:00.000", 19),
                mockUnavailable("2015-10-23T07:00:00.000", 5),
                mockUnavailable("2015-10-24T07:00:00.000", 5),
                mockUnavailable("2015-10-25T07:00:00.000", 40),
                mockUnavailable("2015-10-30T09:59:00.000", 0)
        ).forEachOrdered(consumingEntry((date, uc) -> {
            withDate(date, () -> {
                f.unavailableCapacities = singletonList(uc);
                facilityRepository.updateFacility(f.id, f);
            });
        }));

        facilityService.registerUtilization(f.id, asList(
            mockUtilize(f, "2015-09-25T08:37:26", 0),
            mockUtilize(f, "2015-09-26T08:35:26", 1),
            mockUtilize(f, "2015-09-27T08:37:26", 20),
            mockUtilize(f, "2015-09-28T08:37:26", 20),
            mockUtilize(f, "2015-09-29T08:37:26", 21),
            mockUtilize(f, "2015-09-30T08:37:26", 21),
            mockUtilize(f, "2015-10-01T08:37:26", 21),
            mockUtilize(f, "2015-10-02T08:37:26", 5),
            mockUtilize(f, "2015-10-03T08:37:26", 4),
            mockUtilize(f, "2015-10-04T08:37:26", 1),
            mockUtilize(f, "2015-10-05T08:37:26", 21),
            mockUtilize(f, "2015-10-06T08:37:26", 21),
            mockUtilize(f, "2015-10-07T08:37:26", 21),
            mockUtilize(f, "2015-10-08T08:37:26", 21),
            mockUtilize(f, "2015-10-09T08:30:26", 15),
            mockUtilize(f, "2015-10-09T08:57:26", 10),
            mockUtilize(f, "2015-10-09T09:37:26", 25),
            mockUtilize(f, "2015-10-09T10:37:26", 5),
            mockUtilize(f, "2015-10-09T11:37:26", 20),
            mockUtilize(f, "2015-10-10T08:37:26", 30),
            mockUtilize(f, "2015-10-11T08:37:26", 30),
            mockUtilize(f, "2015-10-12T08:37:26", 21),
            mockUtilize(f, "2015-10-13T08:37:26", 21),
            mockUtilize(f, "2015-10-14T08:37:26", 21),
            mockUtilize(f, "2015-10-15T08:37:26", 21),
            mockUtilize(f, "2015-10-16T08:37:26", 20),
            mockUtilize(f, "2015-10-17T08:37:26", 3),
            mockUtilize(f, "2015-10-18T08:37:26", 1),
            mockUtilize(f, "2015-10-19T08:37:26", 15),
            mockUtilize(f, "2015-10-20T08:37:26", 15),
            mockUtilize(f, "2015-10-21T08:37:26", 15),
            mockUtilize(f, "2015-10-22T08:37:26", 15),
            mockUtilize(f, "2015-10-23T08:37:26", 10),
            mockUtilize(f, "2015-10-24T08:37:26", 5),
            mockUtilize(f, "2015-10-25T07:37:26", 30),
            mockUtilize(f, "2015-10-26T07:37:26", 5),
            mockUtilize(f, "2015-10-27T07:37:26", 5),
            mockUtilize(f, "2015-10-28T07:37:26", 5),
            mockUtilize(f, "2015-10-29T07:37:26", 5),
            mockUtilize(f, "2015-10-30T07:37:26", 25)
        ), apiUser);


        final ReportParameters params = baseParams();
        params.startDate = new LocalDate("2015-10-01");
        params.endDate   = new LocalDate("2015-10-30");
        final Response whenPostingToReportUrl = postToReportUrl(params, MAX_UTILIZATION, adminUser);

        // Assert correct values as tested manually
        withWorkbook(whenPostingToReportUrl, wb -> {
            assertThat(getDataFromColumn(wb.getSheetAt(0), UNAVAILABLE_COLUMN))
                    .containsExactly("Tilapäisesti poissa käytöstä", "49", "20", "49");
        });
    }

    @Test
    public void testThat_startAndEndDateAreInclusive() {
        final ReportParameters params = baseParams();
        params.startDate = new LocalDate(2015, 10, 1); // Thursday
        params.endDate = new LocalDate(2015, 10, 31); // Saturday

        facilityService.registerUtilization(facility1.id, asList(
                utilize(CAR, 10, params.startDate.toDateTimeAtCurrentTime(), facility1),
                utilize(CAR, 20, params.endDate.toDateTimeAtCurrentTime(), facility1)
        ), apiUser);
        facilityService.registerUtilization(facility2.id, asList(
                utilize(CAR, 0, params.startDate.toDateTimeAtCurrentTime(), facility2),
                utilize(CAR, 0, params.endDate.toDateTimeAtCurrentTime(), facility2)
        ), apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(params, MAX_UTILIZATION, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 0.9),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY, 100, 0, 0.8)
        );
    }

    private Utilization mockUtilize(Facility f, String date, int spaces) {
        return utilize(CAR, spaces, DateTime.parse(date), f);
    }


    private Map.Entry<DateTime, UnavailableCapacity> mockUnavailable(String date, int val) {
        return new AbstractMap.SimpleImmutableEntry<>(DateTime.parse(date), new UnavailableCapacity(CAR, PARK_AND_RIDE, val));
    }

    @Test
    public void report_MaxUtilization_changingCapacity() {
        withDate(fri, () -> {
            facility1.builtCapacity = ImmutableMap.of(CAR, 100);
            facilityRepository.updateFacility(facility1.id, facility1);
        });
        addMockMaxUtilizations(facility1, apiUser,  50, 50, 50);
        addMockMaxUtilizations(facility2, apiUser2, 0, 50, 10);

        // Monday   = 1 - ((50 +  0) / (  50 + 50)) = 50%
        // Saturday = 1 - ((50 + 50) / ( 100 + 50)) = 33.33%
        // Sunday   = 1 - ((50 + 10) / ( 100 + 50)) = 40%
        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 150, 0, 0.5),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY,     150, 0, 0.3333),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY,       150, 0, 0.6)
        );
    }

    @Test
    public void report_MaxUtilization_exceptionalSituation() {
        addMockMaxUtilizations(facility1, apiUser, 0, 0, 0);
        addMockMaxUtilizations(facility2, apiUser2, 0, 0, 0);
        withDate(sat.withTime(7, 0, 0, 0), () -> {
            facility2.status = FacilityStatus.EXCEPTIONAL_SITUATION;
            facilityRepository.updateFacility(facility2.id, facility2);
        });

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), MAX_UTILIZATION, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headers(),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.BUSINESS_DAY, 100, 0, 1.0),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SATURDAY,     100, 0, 1.0, true),
                hubRow(asList(operator1, operator2), PARK_AND_RIDE, CAR, DayType.SUNDAY,       100, 0, 1.0, true)
        );
    }

    private List<String> headers() {
        return asList("Alueen nimi",
                "Kunta",
                "Operaattori",
                "Käyttötapa",
                "Ajoneuvotyyppi",
//                "Status",
                "Pysäköintipaikkojen määrä",
                "Tilapäisesti poissa käytöstä",
                "Päivätyyppi",
                "Keskimääräinen maksimitäyttöaste",
                ""
        );
    }

    private void addMockMaxUtilizations(Facility f, User apiUser) {
        final Integer capacity = f.builtCapacity.get(CAR);
        // 50/50 = 100%  business day
        // 25/50 =  50%  Saturday
        // 10/50 =  20%  Sunday
        addMockMaxUtilizations(f, apiUser, 0, capacity - (capacity / 2), capacity - (capacity / 5));
    }

    private void addMockMaxUtilizations(Facility f, User apiUser, int businessDay, int saturday, int sunday) {
        facilityService.registerUtilization(f.id, asList(
                utilize(CAR, businessDay, mon, f),
                utilize(CAR, saturday, sat, f),
                utilize(CAR, sunday, sun, f),

                // BICYCLE_SECURE_SPACE does not exist in built capacity, should not fail
                utilize(BICYCLE_SECURE_SPACE, 0, baseDate.withDayOfWeek(MONDAY), f)
        ), apiUser);
    }

    private List<String> hubRow(Operator operator, Usage usage, CapacityType type, DayType dayType, Integer totalCapacity, Integer unavailableCapacity, Double percentage) {
        return hubRow(singletonList(operator), usage, type, dayType, totalCapacity, unavailableCapacity, percentage);
    }

    private List<String> hubRow(List<Operator> operators, Usage usage, CapacityType type, DayType dayType, Integer totalCapacity, Integer unavailableCapacity, Double percentage) {
        return hubRow(operators, usage, type, dayType, totalCapacity, unavailableCapacity, percentage, false);
    }

    private List<String> hubRow(List<Operator> operators, Usage usage, CapacityType type, DayType dayType, Integer totalCapacity, Integer unavailableCapacity, Double percentage, boolean hasHadExceptionalSituation) {
        return asList(
                hub.name.fi,
                "Helsinki",
                operators.stream().map(o -> o.name.fi).collect(joining(", ")),
                translationService.translate(usage),
                translationService.translate(type),
//                translationService.translate(facility1.status),
                totalCapacity.toString(),
                unavailableCapacity.toString(),
                translationService.translate(dayType),
                String.format(Locale.ENGLISH, "%.2f %%", percentage * 100.0),
                hasHadExceptionalSituation ? "Huom! Ajanjaksolla ollut poikkeustilanne" : ""
        );
    }

}
