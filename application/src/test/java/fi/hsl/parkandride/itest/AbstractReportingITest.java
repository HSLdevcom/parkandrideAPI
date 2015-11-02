// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import fi.hsl.parkandride.front.UrlSchema;
import junit.framework.AssertionFailedError;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.SoftAssertionError;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;
import static java.util.Arrays.asList;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.joda.time.DateTimeConstants.MONDAY;

public abstract class AbstractReportingITest extends AbstractIntegrationTest {
    protected static final DateTime BASE_DATE_TIME = LocalDate.now().minusMonths(1).withDayOfMonth(15).toDateTime(LocalTime.parse("12:37"));
    protected static final DateTime ROUNDED_BASE_DATETIME = BASE_DATE_TIME.withMinuteOfHour(0);
    protected static final LocalDate BASE_DATE = BASE_DATE_TIME.toLocalDate();
    protected static final String MONTH_FORMAT = "M/yyyy";
    protected static final String DATE_FORMAT = "d.M.yyyy";
    protected static final String DATETIME_FORMAT = "d.M.yyyy HH:mm";

    protected static final DateTime baseDate = BASE_DATE.toDateTime(new LocalTime("7:59")).withDayOfMonth(15);
    protected static final DateTime mon = baseDate.withDayOfWeek(MONDAY);
    protected static final DateTime tue = mon.plusDays(1);
    protected static final DateTime wed = mon.plusDays(2);
    protected static final DateTime fri = mon.plusDays(4);
    protected static final DateTime sat = mon.plusDays(5);
    protected static final DateTime sun = mon.plusDays(6);
    protected static final DateTime initial = mon.minusMonths(1);

    protected User adminUser;
    protected User apiUser;
    protected Hub hub;
    protected Facility facility1;
    protected Facility facility2;
    protected Operator operator1;
    protected Operator operator2;
    protected User operator1User;
    protected User operator2User;
    protected User apiUser2;

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;
    @Inject HubService hubService;
    @Inject OperatorService operatorService;
    @Inject TranslationService translationService;
    @Inject MessageSource messageSource;

    @Before
    public void initialize() {
        initFixture();
    }

    protected final void initFixture() {
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
        operator2User = devHelper.createOrUpdateUser(new NewUser(2L, "operator2", OPERATOR, facility2.operatorId, "operator"));

        apiUser = devHelper.createOrUpdateUser(new NewUser(100L, "operator_api", OPERATOR_API, facility1.operatorId, "operator"));
        apiUser2 = devHelper.createOrUpdateUser(new NewUser(101L, "Ooppera_api", OPERATOR_API, facility2.operatorId, "Ooppera"));

        adminUser = devHelper.createOrUpdateUser(new NewUser(10L, "admin", ADMIN, null, "admin"));
    }

    // ---------------------
    // HELPER METHODS
    // ---------------------

    protected static ReportParameters baseParams() {
        return baseParams(BASE_DATE);
    }

    protected static ReportParameters baseParams(LocalDate referenceDate) {
        final ReportParameters params = new ReportParameters();
        params.startDate = referenceDate.dayOfMonth().withMinimumValue();
        params.endDate = referenceDate.dayOfMonth().withMaximumValue();
        return params;
    }

    static void printSheet(Sheet sheet) {
        final DataFormatter dataFormatter = new DataFormatter();
        List<List<String>> rows = new ArrayList<>();
        for (Row row : sheet) {
            List<String> r = new ArrayList<>();
            for (Cell cell : row) {
                r.add(dataFormatter.formatCellValue(cell));
            }
            rows.add(r);
        }
        printSheet(rows);
    }

    static void printSheet(List<List<String>> rows) {
        for (List<String> row : rows) {
            for (String cell : row) {
                System.out.printf("%-30s", cell);
            }
            System.out.printf("%n");
        }
    }

    protected static com.jayway.restassured.response.Header authorization(String authToken) {
        return new com.jayway.restassured.response.Header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
    }

    protected static Utilization utilize(CapacityType capacityType, Integer spacesAvailable, DateTime ts, Facility f) {
        final Utilization utilization = new Utilization();
        utilization.facilityId = f.id;
        utilization.capacityType = capacityType;
        utilization.spacesAvailable = spacesAvailable;
        utilization.usage = f.usages.first();
        utilization.timestamp = ts;
        return utilization;
    }

    protected List<String> findRowWithColumn(Sheet sheet, int columnNumber, String content) {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            final Row row = sheet.getRow(i);
            final List<String> dataFromRow = getDataFromRow(row);
            if (dataFromRow.contains(content)) {
                return dataFromRow;
            }
        }
        throw new NoSuchElementException(String.format("Row with column at %d: <%s> not found", columnNumber, content));
    }

    protected Response postToReportUrl(ReportParameters params, String reportType, User user) {
        final Response whenPostingToReportUrl = whenPostingToReportUrl(params, reportType, user);
        whenPostingToReportUrl
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
        return whenPostingToReportUrl;
    }

    protected Response whenPostingToReportUrl(ReportParameters params, String reportType, User user) {
        final RequestSpecification createRequest = createRequest(params, user);
        return whenPostingToReportUrl(reportType, createRequest);
    }

    protected Response whenPostingToReportUrl(String reportType, RequestSpecification createRequest) {
        return createRequest.when().post(UrlSchema.REPORT, reportType);
    }

    protected RequestSpecification createRequest(ReportParameters params, User user) {
        final String authToken = devHelper.login(user.username).token;
        return given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(RequestLogITest.authorization(authToken))
                .body(params);
    }

    protected void withWorkbook(Response response, Consumer<Workbook> workbookConsumer) {
        try (Workbook workbook = readWorkbookFrom(response)) {
            workbookConsumer.accept(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionFailedError(e.getMessage());
        }
    }

    protected Workbook readWorkbookFrom(Response whenPostingToReportUrl) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(whenPostingToReportUrl.asByteArray())){
            return WorkbookFactory.create(bais);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            throw new AssertionFailedError(e.getMessage());
        }
    }

    protected List<String> getDataFromRow(Sheet sheet, int rownum) {
        final Row row = sheet.getRow(rownum);
        return row == null ? null : getDataFromRow(row);
    }

    protected List<String> getDataFromRow(Row row) {
        final DataFormatter dataFormatter = new DataFormatter();
        return stream(spliteratorUnknownSize(row.cellIterator(), ORDERED), false)
                .map(cell -> dataFormatter.formatCellValue(cell))
                .collect(toList());
    }

    protected List<String> getDataFromColumn(Sheet sheet, int colnum) {
        final DataFormatter dataFormatter = new DataFormatter();
        return stream(spliteratorUnknownSize(sheet.rowIterator(), ORDERED), false)
                .map(row -> row.getCell(colnum))
                .map(cell -> dataFormatter.formatCellValue(cell))
                .collect(toList());
    }

    protected List<String> getSheetNames(Workbook workbook) {
        return IntStream.range(0, workbook.getNumberOfSheets())
                .mapToObj(i -> workbook.getSheetName(i))
                .collect(toList());
    }

    void checkSheetContents(Response response, int sheetIndex, List<String>... rows) {
        withWorkbook(response, workbook -> checkSheetContents(workbook, sheetIndex, rows));
    }

    void checkSheetContents(Workbook workbook, int sheetIndex, List<String>... rows) {
        try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
            checkSheetContents(softly, workbook, sheetIndex, rows);
        } catch (SoftAssertionError sae) {
            System.out.println("===========\nACTUAL\n===========");
            printSheet(workbook.getSheetAt(sheetIndex));
            System.out.println("===========\nEXPECTED\n===========");
            printSheet(asList(rows));
            throw sae;
        }
    }

    void checkSheetContents(AutoCloseableSoftAssertions softly, Workbook workbook, int sheetIndex, List<String>... rows) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        softly.assertThat(sheet.getPhysicalNumberOfRows()).as("Number of rows").isEqualTo(rows.length);
        for (int i = 0; i < rows.length; i++) {
            final List<String> dataFromRow = getDataFromRow(sheet, i);
            softly.assertThat(dataFromRow).as("Row index %d", i).containsExactlyElementsOf(rows[i]);
        }
    }
}
