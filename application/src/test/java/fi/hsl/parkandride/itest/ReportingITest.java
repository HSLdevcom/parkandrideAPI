// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.core.service.reporting.ReportServiceSupport;
import fi.hsl.parkandride.front.ReportParameters;
import fi.hsl.parkandride.front.UrlSchema;
import junit.framework.AssertionFailedError;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.jayway.restassured.RestAssured.given;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.Role.*;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeConstants.*;

public class ReportingITest extends AbstractIntegrationTest {

    private static final LocalDate BASE_DATE = LocalDate.now().minusMonths(1);
    private static final int FACILITYUSAGE_FIRST_TIME_COLUMN = 12;

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;
    @Inject HubService hubService;
    @Inject OperatorService operatorService;

    @Inject TranslationService translationService;
    private Hub hub;
    private Facility facility1;

    private Facility facility2;
    private Operator operator1;

    private Operator operator2;
    private User operator1User;
    private User operator2User;
    private User apiUser;
    private User apiUser2;
    private User adminUser;

    private static Header authorization(String authToken) {
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
    }

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        long facilityId1 = dummies.createFacility();
        long facilityId2 = dummies.createFacility();
        long hubId = dummies.createHub(facilityId1, facilityId2);

        hub = hubService.getHub(hubId);
        facility1 = facilityService.getFacility(facilityId1);
        facility2 = facilityService.getFacility(facilityId2);

        operator1 = operatorService.getOperator(facility1.operatorId);
        operator2 = operatorService.getOperator(facility2.operatorId);

        operator1User = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR, facility1.operatorId, "operator"));
        operator2User = devHelper.createOrUpdateUser(new NewUser(2L, "Ooppera", OPERATOR, facility2.operatorId, "Ooppera"));

        apiUser = devHelper.createOrUpdateUser(new NewUser(100L, "operator_api", OPERATOR_API, facility1.operatorId, "operator"));
        apiUser2 = devHelper.createOrUpdateUser(new NewUser(101L, "Ooppera_api", OPERATOR_API, facility2.operatorId, "Ooppera"));

        adminUser = devHelper.createOrUpdateUser(new NewUser(10L, "admin", ADMIN, null, "admin"));
    }

    // ---------------------
    // FACILITY USAGE REPORT
    // ---------------------

    @Test
    public void report_FacilityUsage_asOperator2() {
        final ReportParameters params = baseParams();
        params.interval = 3*60;
        registerMockFacilityUsages(facility1, apiUser);
        registerMockFacilityUsages(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(params, "FacilityUsage", operator2User);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(getSheetNames(workbook)).containsExactly("Käyttöasteraportti", "Selite");

        final Sheet usages = workbook.getSheetAt(0);
        // Header and one for each usage type
        assertThat(usages.getPhysicalNumberOfRows()).isEqualTo(3);

        // Only operator2 visible
        assertThat(getDataFromColumn(usages, 3))
            .containsOnly("Operaattori", operator2.name.fi)
            .doesNotContain(operator1.name.fi);

        final List<String> headers = getDataFromRow(usages, 0);
        assertThat(headers.subList(FACILITYUSAGE_FIRST_TIME_COLUMN, headers.size()))
                .containsExactly("00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00");

        // Get the hourly utilizations for CAR
        // Results are not interpolated.
        final List<String> row = getDataFromRow(usages, 1);
        assertThat(row.subList(FACILITYUSAGE_FIRST_TIME_COLUMN, row.size()))
                .containsExactly("24", "24", "24", "24", "0", "0", "0", "24");
    }

    @Test
    public void report_FacilityUsage_asAdmin() {
        final ReportParameters params = baseParams();
        params.interval = 180;
        registerMockFacilityUsages(facility1, apiUser);
        registerMockFacilityUsages(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(params, "FacilityUsage", adminUser);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);

        final Sheet usages = workbook.getSheetAt(0);
        // Header and one for each usage type for both facilities
        assertThat(usages.getPhysicalNumberOfRows()).isEqualTo(5);

        assertThat(getDataFromColumn(usages, 3))
                .containsExactly("Operaattori", operator1.name.fi, operator1.name.fi, operator2.name.fi, operator2.name.fi);
    }

    @Test
    public void report_FacilityUsage_asAdmin_oneFacility() {
        final ReportParameters params = baseParams();
        params.interval = 180;
        params.facilities = singleton(facility1.id);
        params.capacityTypes = singleton(CAR);
        params.usages = singleton(Usage.PARK_AND_RIDE);
        params.operators = singleton(facility1.operatorId);
        registerMockFacilityUsages(facility1, apiUser);
        registerMockFacilityUsages(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(params, "FacilityUsage", adminUser);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        final Sheet usages = workbook.getSheetAt(0);
        // Header + one row
        assertThat(getDataFromColumn(usages, 0))
                .containsExactly("Pysäköintipaikan nimi", facility1.name.fi);
        assertThat(getDataFromColumn(usages, 4))
                .containsExactly("Käyttötapa", "Liityntä");
        assertThat(usages.getPhysicalNumberOfRows()).isEqualTo(2);
    }

    private void registerMockFacilityUsages(Facility facility, User user) {
        DateTime startOfDay = BASE_DATE.toDateTimeAtStartOfDay();
        facilityService.registerUtilization(facility.id, asList(
                utilize(CAR, 24, startOfDay, facility),
                utilize(CAR,  0, startOfDay.plusHours(12), facility),
                utilize(CAR, 24, startOfDay.secondOfDay().withMaximumValue(), facility),

                utilize(ELECTRIC_CAR, 2, startOfDay, facility),
                utilize(ELECTRIC_CAR, 0, startOfDay.plusHours(12), facility),
                utilize(ELECTRIC_CAR, 2, startOfDay.secondOfDay().withMaximumValue(), facility)
        ), user);
    }

    // ---------------------
    // HUBS AND FACILITIES REPORT
    // ---------------------

    @Test
    public void report_HubsAndFacilities_asOperator() {
        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), "HubsAndFacilities", operator1User);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
        assertThat(getSheetNames(workbook)).containsExactly("Alueet", "Pysäköintipaikat", "Selite");

        // Check that only operator1 is displayed
        checkHubsAndFacilities_operatorsAre(workbook, operator1);

        // Check that hub info is displayed correctly
        checkHubsAndFacilities_hubInfo(workbook);
        // Check that facility info is displayed correctly
        checkHubsAndFacilities_facilityInfo(workbook);
    }

    @Test
    public void report_HubsAndFacilities_asAdmin() {
        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), "HubsAndFacilities", adminUser);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        // For admin, both operators should be visible
        checkHubsAndFacilities_operatorsAre(workbook, operator1, operator2);
    }


    private void checkHubsAndFacilities_operatorsAre(Workbook workbook, Operator... operators) {
        final Sheet facilities = workbook.getSheetAt(1);
        final List<String> expectedColumns = newArrayList("Operaattori");
        expectedColumns.addAll(Arrays.stream(operators).map(o -> o.name.fi).collect(toList()));
        assertThat(getDataFromColumn(facilities, 3)).containsExactlyElementsOf(expectedColumns);
    }

    private void checkHubsAndFacilities_facilityInfo(Workbook workbook) {
        final Sheet facilities = workbook.getSheetAt(1);
        assertThat(facilities.getPhysicalNumberOfRows()).isEqualTo(2);
        final List<String> facilityInfo = getDataFromRow(facilities, 1);
        assertThat(facilityInfo).containsSequence(
                facility1.name.fi,
                String.join(", ", facility1.aliases),
                hub.name.fi,
                operator1.name.fi,
                translationService.translate(facility1.status),
                facility1.statusDescription.fi,
                String.format(Locale.ENGLISH, "%.4f", facility1.location.getCentroid().getX()),
                String.format(Locale.ENGLISH, "%.4f", facility1.location.getCentroid().getY()),
                "",
                "08:00 - 18:00",
                "08:00 - 18:00",
                facility1.openingHours.info.fi,
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.getOrDefault(CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility1.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0)
        );
    }

    private void checkHubsAndFacilities_hubInfo(Workbook workbook) {
        final Sheet hubs = workbook.getSheetAt(0);
        assertThat(hubs.getPhysicalNumberOfRows()).isEqualTo(2);

        final List<String> hubInfo = getDataFromRow(hubs, 1);
        assertThat(hubInfo).containsExactly(
                hub.name.fi,
                String.join(", ", toArray(hub.address.streetAddress.fi, hub.address.postalCode, hub.address.city.fi)),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getX()),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getY()),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility1.builtCapacity.getOrDefault(CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility1.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility1.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility1.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0),
                facility1.name.fi
        );
    }


    // ---------------------
    // MAX UTILIZATION REPORT
    // ---------------------

    @Test
    public void report_MaxUtilization_asOperator() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), "MaxUtilization", operator1User);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(getSheetNames(workbook)).containsExactly("Tiivistelmäraportti", "Selite");
        checkMaxUtilization_rows(workbook);

        // Only operator1 is displayed
        assertThat(getDataFromColumn(workbook.getSheetAt(0), 2))
                .contains("Operaattori", operator1.name.fi)
                .doesNotContain(operator2.name.fi);
    }

    @Test
    public void report_MaxUtilization_asAdmin() {
        // Record mock usage data
        addMockMaxUtilizations(facility1, apiUser);
        addMockMaxUtilizations(facility2, apiUser2);

        final Response whenPostingToReportUrl = postToReportUrl(baseParams(), "MaxUtilization", adminUser);

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);


        // Both operators are displayed with joined name
        assertThat(getDataFromColumn(workbook.getSheetAt(0), 2))
                .hasSize(4) // header + rows for working days, Sat, and Sun
                .containsOnly("Operaattori", String.join(", ", operator1.name.fi, operator2.name.fi));
    }

    private void addMockMaxUtilizations(Facility f, User apiUser) {
        final Integer capacity = f.builtCapacity.get(CAR);
        // Day 15 to ensure that weekdays stay in the same month
        final DateTime baseDate = BASE_DATE.toDateTimeAtCurrentTime().withDayOfMonth(15);
        facilityService.registerUtilization(f.id, asList(
                // 50/50 = 100%
                utilize(CAR, 0, baseDate.withDayOfWeek(MONDAY), f),
                // 25/50 =  50%
                utilize(CAR, capacity - (capacity / 2), baseDate.withDayOfWeek(SATURDAY), f),
                // 10/50 =  20%
                utilize(CAR, capacity - (capacity / 5), baseDate.withDayOfWeek(SUNDAY), f)
        ), apiUser);
    }

    private void checkMaxUtilization_rows(Workbook workbook) {
        /*
        EXAMPLE:
        Hubi	Helsinki	X-Park	Liityntä	Henkilöauto	Toiminnassa	50	Arkipäivä	100 %
        Hubi	Helsinki	X-Park	Liityntä	Henkilöauto	Toiminnassa	50	Lauantai	100 %
        Hubi	Helsinki	X-Park	Liityntä	Henkilöauto	Toiminnassa	50	Sunnuntai	100 %
         */

        final Sheet utilization = workbook.getSheetAt(0);
        assertThat(utilization.getPhysicalNumberOfRows()).isEqualTo(4);
        final List<String> businessDay = getDataFromRow(utilization, 1);
        assertThat(businessDay).containsExactly(
                hub.name.fi,
                "Helsinki", // The region name
                operator1.name.fi,
                translationService.translate(facility1.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility1.status),
                "" + facility1.builtCapacity.get(CAR),
                translationService.translate(DayType.BUSINESS_DAY),
                "100%"
        );

        final List<String> saturday = getDataFromRow(utilization, 2);
        assertThat(saturday).containsExactly(
                hub.name.fi,
                "Helsinki", // The region name
                operator1.name.fi,
                translationService.translate(facility1.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility1.status),
                "" + facility1.builtCapacity.get(CAR),
                translationService.translate(DayType.SATURDAY),
                "50%"
        );

        final List<String> sunday = getDataFromRow(utilization, 3);
        assertThat(sunday).containsExactly(
                hub.name.fi,
                "Helsinki", // The region name
                operator1.name.fi,
                translationService.translate(facility1.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility1.status),
                "" + facility1.builtCapacity.get(CAR),
                translationService.translate(DayType.SUNDAY),
                "20%"
        );
    }

    // ---------------------
    // MISC. REPORT TESTS
    // ---------------------

    @Test
    public void report_accessDenied() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(apiUser.username).token))
                .body(new ReportParameters())
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void report_withException_resultsInBadRequest() {
        // Negative interval should throw exception
        final ReportParameters params = baseParams();
        params.interval = -1;
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(adminUser.username).token))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void report_withConflictingDates_resultsInBadRequest() {
        // No params given -> IllegalArgumentException from fi.hsl.parkandride.core.service.reporting.FacilityUsageReportService
        final ReportParameters params = baseParams();
        params.interval = 100;
        params.startDate = BASE_DATE.toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = BASE_DATE.minusDays(1).toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(adminUser.username).token))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void report_incorrectType_resultsInBadRequest() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(adminUser.username).token))
                .body(baseParams())
                .when()
                .post(UrlSchema.REPORT, "foobar")
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // ---------------------
    // HELPER METHODS
    // ---------------------

    private Utilization utilize(CapacityType capacityType, Integer spacesAvailable, DateTime ts, Facility f) {
        final Utilization utilization = new Utilization();
        utilization.facilityId = f.id;
        utilization.capacityType = capacityType;
        utilization.spacesAvailable = spacesAvailable;
        utilization.usage = f.usages.first();
        utilization.timestamp = ts;
        return utilization;
    }

    private List<String> getSheetNames(Workbook workbook) {
        return IntStream.range(0, workbook.getNumberOfSheets())
                .mapToObj(i -> workbook.getSheetName(i))
                .collect(toList());
    }

    private Response postToReportUrl(ReportParameters params, String reportType, User user) {
        final String authToken = devHelper.login(user.username).token;
        final Response whenPostingToReportUrl = given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(authToken))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, reportType);
        whenPostingToReportUrl
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
        return whenPostingToReportUrl;
    }

    private Workbook readWorkbookFrom(Response whenPostingToReportUrl) {
        try {
            return WorkbookFactory.create(new ByteArrayInputStream(whenPostingToReportUrl.asByteArray()));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            throw new AssertionFailedError(e.getMessage());
        }
    }

    private List<String> getDataFromRow(Sheet sheet, int rownum) {
        final DataFormatter dataFormatter = new DataFormatter();
        return stream(spliteratorUnknownSize(sheet.getRow(rownum).cellIterator(), ORDERED), false)
                .map(cell -> dataFormatter.formatCellValue(cell))
                .collect(toList());
    }

    private List<String> getDataFromColumn(Sheet sheet, int colnum) {
        final DataFormatter dataFormatter = new DataFormatter();
        return stream(spliteratorUnknownSize(sheet.rowIterator(), ORDERED), false)
                .map(row -> row.getCell(colnum))
                .map(cell -> dataFormatter.formatCellValue(cell))
                .collect(toList());
    }

    private static ReportParameters baseParams() {
        final ReportParameters params = new ReportParameters();
        params.startDate = BASE_DATE.dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = BASE_DATE.dayOfMonth().withMaximumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        return params;
    }
}
