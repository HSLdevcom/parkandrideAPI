// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.BatchingRequestLogService;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import fi.hsl.parkandride.core.service.reporting.RequestLogInterval;
import fi.hsl.parkandride.front.UrlSchema;
import junit.framework.AssertionFailedError;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;
import static fi.hsl.parkandride.front.RequestLoggingInterceptor.SOURCE_HEADER;
import static fi.hsl.parkandride.front.UrlSchema.FACILITY;
import static fi.hsl.parkandride.front.UrlSchema.HUB;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestLogITest extends AbstractIntegrationTest {

    private static final DateTime BASE_DATE_TIME = LocalDate.now().minusMonths(1).withDayOfMonth(15).toDateTime(LocalTime.parse("12:37"));
    private static final DateTime ROUNDED_BASE_DATETIME = BASE_DATE_TIME.withMinuteOfHour(0);
    private static final LocalDate BASE_DATE = BASE_DATE_TIME.toLocalDate();

    private static final String MONTH_FORMAT = "M/yyyy";
    private static final String DATE_FORMAT = "d.M.yyyy";
    private static final String DATETIME_FORMAT = "d.M.yyyy HH:mm";
    private static final String WEB_UI_SOURCE = "liipi-ui";
    private static final String REQUEST_LOG = "RequestLog";

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;

    @Inject BatchingRequestLogService batchingRequestLogService;
    @Inject MessageSource messageSource;
    private String unknownSource;
    private User adminUser;
    private User apiUser;

    private static Header authorization(String authToken) {
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
    }

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        unknownSource = messageSource.getMessage("reports.requestlog.unknownSource", null, new Locale("fi"));

        final long facilityId = dummies.createFacility();
        Facility facility = facilityService.getFacility(facilityId);

        apiUser = devHelper.createOrUpdateUser(new NewUser(100L, "operator_api", OPERATOR_API, facility.operatorId, "operator"));
        adminUser = devHelper.createOrUpdateUser(new NewUser(10L, "admin", ADMIN, null, "admin"));
    }

    // ---------------------
    // REQUEST LOG REPORT
    // ---------------------
    @Test
    public void report_RequestLog_emptyReport() {
        generateDummyRequestLog();

        // Defaults to DAY interval, the month is empty so report should be empty
        final ReportParameters params = baseParams(BASE_DATE_TIME.minusMonths(2).toLocalDate());
        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {
            assertThat(workbook.getSheetName(0)).isEqualTo("Rajapintakutsut");
            assertThat(workbook.getSheetName(1)).isEqualTo("Selite");

            final Sheet sheet = workbook.getSheetAt(0);
            assertThat(getDataFromRow(sheet, 0))
                    .containsExactly("Päivämäärä", "Lähde", "Polku", "Kutsujen määrä");
            assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(1);
        });
    }

    @Test
    public void report_RequestLog_byHour() {
        generateDummyRequestLog();

        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        params.requestLogInterval = RequestLogInterval.HOUR;

        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        withWorkbook(whenPostingToReportUrl, workbook -> {
            final Sheet sheet = workbook.getSheetAt(0);

            try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
                // Headings
                softly.assertThat(getDataFromRow(sheet, 0))
                        .containsExactly("Aika", "Lähde", "Polku", "Kutsujen määrä");

                // Check rows
                softly.assertThat(getDataFromColumn(sheet, 0)).containsExactly(
                        "Aika",
                        ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT),
                        ROUNDED_BASE_DATETIME.plusHours(1).toString(DATETIME_FORMAT),
                        ROUNDED_BASE_DATETIME.plusDays(1).toString(DATETIME_FORMAT)
                );
                softly.assertThat(getDataFromColumn(sheet, 1)).containsExactly(
                        "Lähde", unknownSource, unknownSource, WEB_UI_SOURCE, WEB_UI_SOURCE, WEB_UI_SOURCE, WEB_UI_SOURCE
                );
                softly.assertThat(getDataFromColumn(sheet, 2)).containsExactly(
                        "Polku", FACILITY, HUB, FACILITY, HUB, FACILITY, FACILITY
                );
                softly.assertThat(getDataFromColumn(sheet, 3)).containsExactly(
                        "Kutsujen määrä", "12", "8", "12", "8", "12", "12"
                );
            }
        });

    }

    @Test
    public void report_RequestLog_byDay() {
        generateDummyRequestLog();

        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        params.requestLogInterval = RequestLogInterval.DAY;

        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        withWorkbook(whenPostingToReportUrl, workbook -> {
            final Sheet sheet = workbook.getSheetAt(0);

            try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
                // Headings
                softly.assertThat(getDataFromRow(sheet, 0))
                        .containsExactly("Päivämäärä", "Lähde", "Polku", "Kutsujen määrä");

                // Check rows, one less that in hourly report since the current+1 hour has been summed up with the current
                softly.assertThat(getDataFromColumn(sheet, 0)).containsExactly(
                        "Päivämäärä",
                        ROUNDED_BASE_DATETIME.toString(DATE_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATE_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATE_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(DATE_FORMAT),
                        ROUNDED_BASE_DATETIME.plusDays(1).toString(DATE_FORMAT)
                );
                softly.assertThat(getDataFromColumn(sheet, 1)).containsExactly(
                        "Lähde", unknownSource, unknownSource, WEB_UI_SOURCE, WEB_UI_SOURCE, WEB_UI_SOURCE
                );
                softly.assertThat(getDataFromColumn(sheet, 2)).containsExactly(
                        "Polku", FACILITY, HUB, FACILITY, HUB, FACILITY
                );
                softly.assertThat(getDataFromColumn(sheet, 3)).containsExactly(
                        "Kutsujen määrä", "12", "8", "24", "8", "12"
                );
            }
        });
    }

    @Test
    public void report_RequestLog_byMonth() {
        generateDummyRequestLog();

        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        params.requestLogInterval = RequestLogInterval.MONTH;
        params.startDate = BASE_DATE_TIME.minusMonths(1).withDayOfMonth(1).toLocalDate();

        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        withWorkbook(whenPostingToReportUrl, workbook -> {
            final Sheet sheet = workbook.getSheetAt(0);

            try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
                // Headings
                softly.assertThat(getDataFromRow(sheet, 0))
                        .containsExactly("Kuukausi", "Lähde", "Polku", "Kutsujen määrä");

                // Check rows
                softly.assertThat(getDataFromColumn(sheet, 0)).containsExactly(
                        "Kuukausi",
                        ROUNDED_BASE_DATETIME.minusMonths(1).toString(MONTH_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT),
                        ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT)
                );
                softly.assertThat(getDataFromColumn(sheet, 1)).containsExactly(
                        "Lähde", WEB_UI_SOURCE, unknownSource, unknownSource, WEB_UI_SOURCE, WEB_UI_SOURCE
                );
                softly.assertThat(getDataFromColumn(sheet, 2)).containsExactly(
                        "Polku", FACILITY, FACILITY, HUB, FACILITY, HUB
                );
                softly.assertThat(getDataFromColumn(sheet, 3)).containsExactly(
                        "Kutsujen määrä", "12", "12", "8", "36", "8"
                );
            }
        });
    }

    @Test
    public void report_RequestLog_emptyParams() {
        final Response requestLog = whenPostingToReportUrl(new ReportParameters(), REQUEST_LOG, adminUser);
        requestLog.then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void report_RequestLog_nonExistentUrl_andUrlOutsideApi() {
        DateTimeUtils.setCurrentMillisFixed(BASE_DATE_TIME.getMillis());
        when().get(UrlSchema.API + "/foobarbazqux");
        when().get(UrlSchema.DOCS);
        DateTimeUtils.setCurrentMillisSystem();
        batchingRequestLogService.updateRequestLogs();

        // Defaults to DAY interval, the month is empty so report should be empty
        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        // If this succeeds, the response was a valid excel file
        withWorkbook(whenPostingToReportUrl, workbook -> {
            // No requests logged
            final Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(1);
        });
    }

    @Test
    public void report_RequestLog_unauthorized() {
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(apiUser.username).token))
                .body(new ReportParameters())
                .when()
                .post(UrlSchema.REPORT, REQUEST_LOG)
                .then()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void illegalApplicationId_resultsInBadRequest() {
        given().header(SOURCE_HEADER, "ömmöm").when().get(UrlSchema.FACILITY, 1)
            .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }


    private List<String> findRowWithColumn(Sheet sheet, int columnNumber, String content) {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            final Row row = sheet.getRow(i);
            final List<String> dataFromRow = getDataFromRow(row);
            if (dataFromRow.contains(content)) {
                return dataFromRow;
            }
        }
        throw new NoSuchElementException(String.format("Row with column at %d: <%s> not found", columnNumber, content));
    }

    private void generateDummyRequestLog() {
        // Today
        DateTimeUtils.setCurrentMillisFixed(BASE_DATE_TIME.getMillis());
        IntStream.range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, i));
        IntStream.range(0, 8).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.HUB, i));

        // Without Source header
        IntStream.range(0, 12).forEach(i -> when().get(UrlSchema.FACILITY, i));
        IntStream.range(0, 8).forEach(i -> when().get(UrlSchema.HUB, i));

        // An hour after now
        DateTimeUtils.setCurrentMillisFixed(BASE_DATE_TIME.plusHours(1).getMillis());
        IntStream.range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, i));

        // A day after now
        DateTimeUtils.setCurrentMillisFixed(BASE_DATE_TIME.plusDays(1).getMillis());
        IntStream.range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, i));

        // A month before now
        DateTimeUtils.setCurrentMillisFixed(BASE_DATE_TIME.minusMonths(1).getMillis());
        IntStream.range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, i));

        DateTimeUtils.setCurrentMillisSystem();

        // Store the batch to database
        batchingRequestLogService.updateRequestLogs();

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
    public void report_withConflictingDates_resultsInBadRequest() {
        // No params given -> IllegalArgumentException from fi.hsl.parkandride.core.service.reporting.FacilityUsageReportService
        final ReportParameters params = baseParams();
        params.interval = 100;
        params.startDate = BASE_DATE;
        params.endDate = BASE_DATE.minusDays(1);
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(devHelper.login(adminUser.username).token))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, REQUEST_LOG)
                .then()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // ---------------------
    // HELPER METHODS
    // ---------------------

    private Response postToReportUrl(ReportParameters params, String reportType, User user) {
        final Response whenPostingToReportUrl = whenPostingToReportUrl(params, reportType, user);
        whenPostingToReportUrl
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
        return whenPostingToReportUrl;
    }

    private Response whenPostingToReportUrl(ReportParameters params, String reportType, User user) {
        final RequestSpecification createRequest = createRequest(params, user);
        return whenPostingToReportUrl(reportType, createRequest);
    }

    private Response whenPostingToReportUrl(String reportType, RequestSpecification createRequest) {
        return createRequest.when().post(UrlSchema.REPORT, reportType);
    }

    private RequestSpecification createRequest(ReportParameters params, User user) {
        final String authToken = devHelper.login(user.username).token;
        return given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(authToken))
                .body(params);
    }

    private void withWorkbook(Response response, Consumer<Workbook> workbookConsumer) {
        try (Workbook workbook = readWorkbookFrom(response)) {
            workbookConsumer.accept(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionFailedError(e.getMessage());
        }
    }

    private Workbook readWorkbookFrom(Response whenPostingToReportUrl) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(whenPostingToReportUrl.asByteArray())){
            return WorkbookFactory.create(bais);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            throw new AssertionFailedError(e.getMessage());
        }
    }

    private List<String> getDataFromRow(Sheet sheet, int rownum) {
        return getDataFromRow(sheet.getRow(rownum));
    }

    private List<String> getDataFromRow(Row row) {
        final DataFormatter dataFormatter = new DataFormatter();
        return stream(spliteratorUnknownSize(row.cellIterator(), ORDERED), false)
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
        return baseParams(BASE_DATE);
    }

    private static ReportParameters baseParams(LocalDate referenceDate) {
        final ReportParameters params = new ReportParameters();
        params.startDate = referenceDate.dayOfMonth().withMinimumValue();
        params.endDate = referenceDate.dayOfMonth().withMaximumValue();
        return params;
    }


    private void printSheet(Sheet sheet) {
        final DataFormatter dataFormatter = new DataFormatter();
        for (Row row : sheet) {
            for (Cell cell : row) {
                System.out.printf("%-30s", dataFormatter.formatCellValue(cell));
            }
            System.out.printf("%n");
        }
    }
}
