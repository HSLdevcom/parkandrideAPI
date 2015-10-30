// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import fi.hsl.parkandride.core.service.reporting.ReportParameters;
import fi.hsl.parkandride.front.UrlSchema;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.given;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;

public class GenericReportITest extends AbstractReportingITest {

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
        params.startDate = BASE_DATE;
        params.endDate = BASE_DATE.minusDays(1);
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

}
