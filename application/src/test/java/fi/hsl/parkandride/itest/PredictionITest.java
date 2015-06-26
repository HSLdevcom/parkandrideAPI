// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.domain.prediction.PredictionResult;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.PredictionService;
import fi.hsl.parkandride.front.UrlSchema;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

public class PredictionITest extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;

    private long facilityId;
    private String authToken;
    private User user;
    private final DateTime now = new DateTime();

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        facilityId = dummies.createFacility();
        Long operatorId = facilityService.getFacility(facilityId).operatorId;
        user = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR_API, operatorId, "operator"));
        authToken = devHelper.login(user.username).token;
    }

    @Test
    public void prediction_JSON_structure() {
        Utilization u = makeDummyPredictions();

        JsonPath json = when().get(UrlSchema.FACILITY_PREDICTION, facilityId).jsonPath();
        long facilityId = json.getLong("[0].facilityId");
        String capacityType = json.getString("[0].capacityType");
        String usage = json.getString("[0].usage");
        OffsetDateTime timestamp = OffsetDateTime.parse(json.getString("[0].timestamp"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        int spacesAvailable = json.getInt("[0].spacesAvailable");

        assertThat(facilityId).as("facilityId").isEqualTo(u.facilityId);
        assertThat(capacityType).as("capacityType").isEqualTo(u.capacityType.name());
        assertThat(usage).as("usage").isEqualTo(u.usage.name());
        assertThat(timestamp.getOffset()).as("time should be in local timezone")
                .isEqualTo(ZoneOffset.systemDefault().getRules().getOffset(timestamp.toInstant()));
        assertThat(spacesAvailable).as("spacesAvailable").isEqualTo(u.spacesAvailable);
    }

    @Test
    public void prediction_API_is_symmetric_with_utilization_API() {
        makeDummyPredictions();

        Map<String, Object> utilization = when().get(UrlSchema.FACILITY_UTILIZATION, facilityId).jsonPath().getMap("[0]");
        Map<String, Object> prediction = when().get(UrlSchema.FACILITY_PREDICTION, facilityId).jsonPath().getMap("[0]");

        assertThat(utilization).as("utilization").isNotEmpty();
        assertThat(prediction).as("prediction").isNotEmpty();
        assertThat(prediction.keySet()).as("prediction's fields should be a superset of utilization's fields")
                .containsAll(utilization.keySet());
    }

    @Test
    public void returns_predictions_for_all_capacity_types_and_usages() {
        makeDummyPredictions(Usage.HSL_TRAVEL_CARD);
        makeDummyPredictions(Usage.COMMERCIAL);

        PredictionResult[] predictions = getPredictions(facilityId);

        assertThat(predictions).hasSize(2);
    }

    @Test
    public void defaults_to_prediction_for_current_time() {
        makeDummyPredictions();

        PredictionResult[] predictions = getPredictions(facilityId);

        assertThat(predictions).hasSize(1);
        assertIsNear(DateTime.now(), predictions[0].timestamp);
    }

    @Test
    public void can_find_predictions_by_absolute_time() {
        makeDummyPredictions();
        DateTime requestedTime = now.plusHours(5);

        PredictionResult[] predictions = getPredictionsAtAbsoluteTime(facilityId, requestedTime);

        assertThat(predictions).hasSize(1);
        assertIsNear(requestedTime, predictions[0].timestamp);
    }

    @Test
    public void timezone_is_required_for_absolute_time() {
        makeDummyPredictions();
        DateTime requestedTime = now.plusHours(5);

        // TODO: make it return the error message "timezone is required" or similar
        when().get(UrlSchema.FACILITY_PREDICTION_ABSOLUTE, facilityId, requestedTime.toLocalDateTime())
                .then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .assertThat().content(containsString("Invalid format"));
    }

    @Test
    public void can_find_predictions_by_relative_time() {
        makeDummyPredictions();
        DateTime requestedTime = now.plusHours(5).plusMinutes(30);

        PredictionResult[] predictions = getPredictionsAfterRelativeTime(facilityId, "5:30");

        assertThat(predictions).hasSize(1);
        assertIsNear(requestedTime, predictions[0].timestamp);
    }

    @Test
    public void relative_time_may_be_expressed_in_minutes() { // i.e. hours are optional and minutes can be over 60
        makeDummyPredictions();
        DateTime requestedTime = now.plusMinutes(180);

        PredictionResult[] predictions = getPredictionsAfterRelativeTime(facilityId, "180");

        assertThat(predictions).hasSize(1);
        assertIsNear(requestedTime, predictions[0].timestamp);
    }


    // helpers

    private static void assertIsNear(DateTime expected, DateTime actual) {
        Instant i1 = Instant.ofEpochMilli(expected.getMillis());
        Instant i2 = Instant.ofEpochMilli(actual.getMillis());
        Duration d = Duration.between(i1, i2).abs();
        assertThat(d.toMinutes()).as("distance to " + expected + " in minutes")
                .isLessThanOrEqualTo(PredictionRepository.PREDICTION_RESOLUTION.getMinutes());
    }

    private static PredictionResult[] getPredictions(long facilityId) {
        return toPredictions(when().get(UrlSchema.FACILITY_PREDICTION, facilityId));
    }

    private static PredictionResult[] getPredictionsAtAbsoluteTime(long facilityId, DateTime time) {
        return toPredictions(when().get(UrlSchema.FACILITY_PREDICTION_ABSOLUTE, facilityId, time));
    }

    private static PredictionResult[] getPredictionsAfterRelativeTime(long facilityId, String hhmm) {
        return toPredictions(when().get(UrlSchema.FACILITY_PREDICTION_RELATIVE, facilityId, hhmm));
    }

    private static PredictionResult[] toPredictions(Response response) {
        return response.then().assertThat().statusCode(HttpStatus.OK.value())
                .extract().as(PredictionResult[].class);
    }

    private Utilization makeDummyPredictions() {
        return makeDummyPredictions(Usage.PARK_AND_RIDE);
    }

    private Utilization makeDummyPredictions(Usage usage) {
        Utilization u = new Utilization();
        u.facilityId = facilityId;
        u.capacityType = CapacityType.CAR;
        u.usage = usage;
        u.timestamp = now;
        u.spacesAvailable = 42;
        facilityService.registerUtilization(facilityId, Collections.singletonList(u), user);
        predictionService.updatePredictions();
        return u;
    }
}
