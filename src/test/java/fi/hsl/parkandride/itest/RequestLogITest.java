// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.RequestLogDao;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.service.BatchingRequestLogService;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import fi.hsl.parkandride.core.service.reporting.RequestLogInterval;
import fi.hsl.parkandride.front.UrlSchema;
import junit.framework.AssertionFailedError;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;
import static fi.hsl.parkandride.front.RequestLoggingInterceptor.SOURCE_HEADER;
import static fi.hsl.parkandride.front.UrlSchema.FACILITY;
import static fi.hsl.parkandride.front.UrlSchema.HUB;
import static fi.hsl.parkandride.test.DateTimeTestUtils.withDate;
import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestLogITest extends AbstractReportingITest {

    private static final String WEB_UI_SOURCE = "liipi-ui";
    private static final String REQUEST_LOG = "RequestLog";

    @Inject BatchingRequestLogService batchingRequestLogService;
    @Inject RequestLogDao requestLogDao;
    private String unknownSource;

    @Before
    public void init() {
        unknownSource = messageSource.getMessage("reports.requestlog.unknownSource", null, new Locale("fi"));
        batchingRequestLogService.staggeredUpdateMaxDelay = Duration.ZERO;
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
        checkSheetContents(whenPostingToReportUrl, 0,
                headerWithTime("Aika"),
                asList(ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT), unknownSource, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT), unknownSource, HUB, "8"),
                asList(ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT), WEB_UI_SOURCE, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.toString(DATETIME_FORMAT), WEB_UI_SOURCE, HUB, "8"),
                asList(ROUNDED_BASE_DATETIME.plusHours(1).toString(DATETIME_FORMAT), WEB_UI_SOURCE, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.plusDays(1).toString(DATETIME_FORMAT), WEB_UI_SOURCE, FACILITY, "12")
        );
    }

    @Test
    public void report_RequestLog_byDay() {
        generateDummyRequestLog();

        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        params.requestLogInterval = RequestLogInterval.DAY;

        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headerWithTime("Päivämäärä"),
                asList(ROUNDED_BASE_DATETIME.toString(DATE_FORMAT), unknownSource, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.toString(DATE_FORMAT), unknownSource, HUB, "8"),
                asList(ROUNDED_BASE_DATETIME.toString(DATE_FORMAT), WEB_UI_SOURCE, FACILITY, "24"),
                asList(ROUNDED_BASE_DATETIME.toString(DATE_FORMAT), WEB_UI_SOURCE, HUB, "8"),
                asList(ROUNDED_BASE_DATETIME.plusDays(1).toString(DATE_FORMAT), WEB_UI_SOURCE, FACILITY, "12")
        );
    }

    @Test
    public void report_RequestLog_byMonth() {
        generateDummyRequestLog();

        final ReportParameters params = baseParams(BASE_DATE_TIME.toLocalDate());
        params.requestLogInterval = RequestLogInterval.MONTH;
        params.startDate = BASE_DATE_TIME.minusMonths(1).withDayOfMonth(1).toLocalDate();

        final Response whenPostingToReportUrl = postToReportUrl(params, REQUEST_LOG, adminUser);
        checkSheetContents(whenPostingToReportUrl, 0,
                headerWithTime("Kuukausi"),
                asList(ROUNDED_BASE_DATETIME.minusMonths(1).toString(MONTH_FORMAT), WEB_UI_SOURCE, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT), unknownSource, FACILITY, "12"),
                asList(ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT), unknownSource, HUB, "8"),
                asList(ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT), WEB_UI_SOURCE, FACILITY, "36"),
                asList(ROUNDED_BASE_DATETIME.toString(MONTH_FORMAT), WEB_UI_SOURCE, HUB, "8")
        );
    }

    @Test
    public void report_RequestLog_emptyParams() {
        final Response requestLog = whenPostingToReportUrl(new ReportParameters(), REQUEST_LOG, adminUser);
        requestLog.then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void report_RequestLog_nonExistentUrl_andUrlOutsideApi() {
        withDate(BASE_DATE_TIME, () -> {
            when().get(UrlSchema.API + "/foobarbazqux");
            when().get(UrlSchema.DOCS);
        });
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

    @Test
    public void generateConcurrent() {
        concurrentlyGenerateLogs(1000, 10);
        batchingRequestLogService.updateRequestLogs();
        final List<RequestLogEntry> logEntriesBetween = requestLogDao.getLogEntriesBetween(DateTime.now().millisOfDay().withMinimumValue(), DateTime.now().millisOfDay().withMaximumValue());
        assertThat(logEntriesBetween).hasSize(1)
                .containsExactly(new RequestLogEntry(UrlSchema.CAPACITY_TYPES, WEB_UI_SOURCE, DateTime.now().withTime(12, 0, 0, 0), 1000l));
    }

    private void generateDummyRequestLog() {
        // Today
        withDate(BASE_DATE_TIME, () -> {
            range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, facility1.id));
            range(0, 8).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.HUB, hub.id));

            // Without Source header
            range(0, 12).forEach(i -> when().get(UrlSchema.FACILITY, facility1.id));
            range(0, 8).forEach(i -> when().get(UrlSchema.HUB, hub.id));
        });

        // An hour after now
        withDate(BASE_DATE_TIME.plusHours(1), () -> {
            range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, facility1.id));
        });

        // A day after now
        withDate(BASE_DATE_TIME.plusDays(1), () -> {
            range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, facility1.id));
        });

        // A month before now
        withDate(BASE_DATE_TIME.minusMonths(1), () -> {
            range(0, 12).forEach(i -> given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.FACILITY, facility1.id));
        });

        // Store the batch to database
        batchingRequestLogService.updateRequestLogs();
    }

    private void concurrentlyGenerateLogs(int numberOfRequests, int numberOfUpdates) {
        withDate(DateTime.now().withTime(12, 2, 0, 0), () -> {
            final Stream<CompletableFuture<Integer>> statusCodes = range(0, numberOfRequests).parallel().mapToObj(i -> {
                final Response response = given().header(SOURCE_HEADER, WEB_UI_SOURCE).when().get(UrlSchema.CAPACITY_TYPES)
                        .thenReturn();
                return CompletableFuture.completedFuture(response.statusCode());
            });

            final Stream<CompletableFuture<Integer>> updates = range(0, numberOfUpdates).parallel().mapToObj(i -> {
                batchingRequestLogService.updateRequestLogs();
                return CompletableFuture.completedFuture(0);
            });

            try {
                CompletableFuture.allOf(Stream.concat(statusCodes, updates).toArray(i -> new CompletableFuture[i])).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new AssertionFailedError(e.getMessage());
            }
        });
    }


    private List<String> headerWithTime(String timeColumnHeader) {
        return asList(timeColumnHeader, "Lähde", "Polku", "Kutsujen määrä");
    }

}
