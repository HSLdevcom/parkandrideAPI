// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;
import static java.util.Arrays.asList;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;

public class ReportingITest extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;
    @Inject HubService hubService;
    @Inject OperatorService operatorService;
    @Inject TranslationService translationService;

    private long hubId;
    private long facilityId;

    private Hub hub;
    private Facility facility;
    private Operator operator;

    private User user;
    private User apiUser;
    private String authToken;

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        facilityId = dummies.createFacility();
        hubId = dummies.createHub(facilityId);

        hub = hubService.getHub(hubId);
        facility = facilityService.getFacility(facilityId);
        Long operatorId = facility.operatorId;
        operator = operatorService.getOperator(operatorId);

        user = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR, operatorId, "operator"));
        apiUser = devHelper.createOrUpdateUser(new NewUser(2L, "operator_api", OPERATOR_API, operatorId, "operator"));
        authToken = devHelper.login(user.username).token;
    }

    @Test
    public void report_FacilityUsage() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = LocalDate.now().dayOfMonth().withMaximumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.interval = 60;

        final Response whenPostingToReportUrl = postToReportUrl(params, "FacilityUsage");

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(getSheetNames(workbook))
                .containsExactly("Käyttöasteraportti", "Selite");


    }

    @Test
    public void report_HubsAndFacilities() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = params.startDate;
        final Response whenPostingToReportUrl = postToReportUrl(params, "HubsAndFacilities");

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
        assertThat(getSheetNames(workbook))
                .containsExactly("Alueet", "Pysäköintipaikat", "Selite");


        // Check that hub info is displayed correctly
        final Sheet hubs = workbook.getSheetAt(0);
        assertThat(hubs.getPhysicalNumberOfRows()).isEqualTo(2);

        final List<String> hubInfo = getDataFromRow(hubs, 1);
        assertThat(hubInfo).containsExactly(
                hub.name.fi,
                String.join(", ", toArray(hub.address.streetAddress.fi, hub.address.postalCode, hub.address.city.fi)),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getX()),
                String.format(Locale.ENGLISH, "%.4f", hub.location.getY()),
                "" + facility.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility.builtCapacity.getOrDefault(CAR, 0),
                "" + facility.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0),
                facility.name.fi
        );

        // Check that facility info is displayed correctly
        final Sheet facilities = workbook.getSheetAt(1);
        assertThat(facilities.getPhysicalNumberOfRows()).isEqualTo(2);
        final List<String> facilityInfo = getDataFromRow(facilities, 1);
        assertThat(facilityInfo).containsSequence(
                facility.name.fi,
                String.join(", ", facility.aliases),
                hub.name.fi,
                operator.name.fi,
                translationService.translate(facility.status),
                facility.statusDescription.fi,
                String.format(Locale.ENGLISH, "%.4f", facility.location.getCentroid().getX()),
                String.format(Locale.ENGLISH, "%.4f", facility.location.getCentroid().getY()),
                "",
                "08:00 - 18:00",
                "08:00 - 18:00",
                facility.openingHours.info.fi,
                "" + facility.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(motorCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility.builtCapacity.entrySet().stream()
                        .filter(entry -> asList(bicycleCapacities).contains(entry.getKey()))
                        .mapToInt(entry -> entry.getValue())
                        .sum(),
                "" + facility.builtCapacity.getOrDefault(CAR, 0),
                "" + facility.builtCapacity.getOrDefault(DISABLED, 0),
                "" + facility.builtCapacity.getOrDefault(ELECTRIC_CAR, 0),
                "" + facility.builtCapacity.getOrDefault(MOTORCYCLE, 0),
                "" + facility.builtCapacity.getOrDefault(BICYCLE, 0),
                "" + facility.builtCapacity.getOrDefault(BICYCLE_SECURE_SPACE, 0)
        );
    }

    @Test
    public void report_MaxUtilization() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = LocalDate.now().dayOfMonth().withMaximumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);


        final Integer capacity = facility.builtCapacity.get(CAR);
        // Record mock usage data
        facilityService.registerUtilization(facilityId, asList(
                // 50/50 = 100%
                utilize(CAR, 0, DateTime.now().withDayOfMonth(15).withDayOfWeek(MONDAY)),
                // 25/50 =  50%
                utilize(CAR, capacity - (capacity / 2), DateTime.now().withDayOfMonth(15).withDayOfWeek(SATURDAY)),
                // 10/50 =  20%
                utilize(CAR, capacity - (capacity / 5), DateTime.now().withDayOfMonth(15).withDayOfWeek(SUNDAY))
        ), apiUser);

        final Response whenPostingToReportUrl = postToReportUrl(params, "MaxUtilization");

        // If this succeeds, the response was a valid excel file
        final Workbook workbook = readWorkbookFrom(whenPostingToReportUrl);
        assertThat(getSheetNames(workbook))
                .containsExactly("Tiivistelmäraportti", "Selite");

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
                operator.name.fi,
                translationService.translate(facility.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility.status),
                ""+facility.builtCapacity.get(CAR),
                translationService.translate(DayType.BUSINESS_DAY),
                "100%"
        );

        final List<String> saturday = getDataFromRow(utilization, 2);
        assertThat(saturday).containsExactly(
                hub.name.fi,
                "Helsinki", // The region name
                operator.name.fi,
                translationService.translate(facility.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility.status),
                ""+facility.builtCapacity.get(CAR),
                translationService.translate(DayType.SATURDAY),
                "50%"
        );

        final List<String> sunday = getDataFromRow(utilization, 3);
        assertThat(sunday).containsExactly(
                hub.name.fi,
                "Helsinki", // The region name
                operator.name.fi,
                translationService.translate(facility.usages.first()),
                translationService.translate(CAR),
                translationService.translate(facility.status),
                "" + facility.builtCapacity.get(CAR),
                translationService.translate(DayType.SUNDAY),
                "20%"
        );
    }

    @Test
    public void report_accessDenied() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(apiUser.username).token))
                .body(new ReportParameters())
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
    }

    @Test
    public void report_withException_resultsInBadRequest() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void report_incorrectType_resultsInBadRequest() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .when()
                .post(UrlSchema.REPORT, "foobar")
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static Header authorization(String authToken) {
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
    }

    private Utilization utilize(CapacityType capacityType, Integer spacesAvailable, DateTime ts) {
        final Utilization utilization = new Utilization();
        utilization.facilityId = facilityId;
        utilization.capacityType = capacityType;
        utilization.spacesAvailable = spacesAvailable;
        utilization.usage = facility.usages.first();
        utilization.timestamp = ts;
        return utilization;
    }

    private List<String> getSheetNames(Workbook workbook) {
        return IntStream.range(0, workbook.getNumberOfSheets())
                .mapToObj(i -> workbook.getSheetName(i))
                .collect(toList());
    }

    private Response postToReportUrl(ReportParameters params, String reportType) {
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
        } catch (IOException|InvalidFormatException e) {
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
}
