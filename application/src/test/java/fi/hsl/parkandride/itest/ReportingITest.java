// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.core.domain.User;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.PredictionService;
import fi.hsl.parkandride.core.service.ReportServiceSupport;
import fi.hsl.parkandride.front.ReportParameters;
import fi.hsl.parkandride.front.UrlSchema;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import static com.jayway.restassured.RestAssured.given;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.front.ReportController.MEDIA_TYPE_EXCEL;

public class ReportingITest extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;

    private long facilityId;
    private User user;
    private User apiUser;
    private String authToken;

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        facilityId = dummies.createFacility();
        Long operatorId = facilityService.getFacility(facilityId).operatorId;
        user = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR, operatorId, "operator"));
        apiUser = devHelper.createOrUpdateUser(new NewUser(2L, "operator_api", OPERATOR_API, operatorId, "operator"));
        authToken = devHelper.login(user.username).token;
    }

    @Test
    public void report_FacilityUsage() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = params.startDate;
        params.interval = 60;
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(authToken))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, "FacilityUsage")
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
    }

    @Test
    public void report_HubsAndFacilities() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = params.startDate;
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(authToken))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, "HubsAndFacilities")
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
    }

    @Test
    public void report_MaxUtilization() {
        final ReportParameters params = new ReportParameters();
        params.startDate = LocalDate.now().dayOfMonth().withMinimumValue().toString(ReportServiceSupport.FINNISH_DATE_PATTERN);
        params.endDate = params.startDate;
        given().contentType(ContentType.JSON)
                .accept(MEDIA_TYPE_EXCEL)
                .header(authorization(authToken))
                .body(params)
                .when()
                .post(UrlSchema.REPORT, "MaxUtilization")
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().contentType(MEDIA_TYPE_EXCEL);
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
}
