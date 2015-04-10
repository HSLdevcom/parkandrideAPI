// Copyright © 2015 HSL

package fi.hsl.parkandride.itest;

import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.PredictionService;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.front.UrlSchema;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class PredictionITest extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;

    private long facilityId;
    private String authToken;
    private User user;

    @Before
    @TransactionalWrite
    public void initFixture() {
        devHelper.deleteAll();
        facilityId = dummies.createFacility();
        Long operatorId = facilityService.getFacility(facilityId).operatorId;
        user = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR_API, operatorId, "operator"));
        authToken = devHelper.login(user.username).token;
    }

    @Test
    public void prediction_contents() {
        Utilization u = makeDummyPredictions();

        Response response = getPrediction(facilityId);
        long facilityId = response.jsonPath().getLong("[0].facilityId");
        String capacityType = response.jsonPath().getString("[0].capacityType");
        String usage = response.jsonPath().getString("[0].usage");
        OffsetDateTime timestamp = OffsetDateTime.parse(response.jsonPath().getString("[0].timestamp"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        int spacesAvailable = response.jsonPath().getInt("[0].spacesAvailable");

        assertThat(facilityId).as("facilityId").isEqualTo(u.facilityId);
        assertThat(capacityType).as("capacityType").isEqualTo(u.capacityType.name());
        assertThat(usage).as("usage").isEqualTo(u.usage.name());
        assertThat(timestamp.getOffset()).as("time should be in local timezone")
                .isEqualTo(ZoneOffset.systemDefault().getRules().getOffset(timestamp.toInstant()));
        assertThat(spacesAvailable).as("spacesAvailable").isEqualTo(u.spacesAvailable);
    }

    @Test
    public void predictions_are_symmetric_with_utilizations() {
        makeDummyPredictions();

        // TODO: make both APIs either return a plain array or wrap them both in a results field
        Map<String, Object> utilization = when().get(UrlSchema.FACILITY_UTILIZATION, facilityId).jsonPath().getMap("results[0]");
        Map<String, Object> prediction = when().get(UrlSchema.FACILITY_PREDICTION, facilityId).jsonPath().getMap("[0]");

        assertThat(utilization).as("utilization").isNotEmpty();
        assertThat(prediction).as("prediction").isNotEmpty();
        assertThat(prediction.keySet()).as("prediction's fields should be a superset of utilization's fields")
                .containsAll(utilization.keySet());
    }

    // TODO: returns all capacity types and usages

    @Test
    public void defaults_to_prediction_for_current_time() {
        makeDummyPredictions();

        Response response = getPrediction(facilityId);
        OffsetDateTime timestamp = OffsetDateTime.parse(response.jsonPath().getString("[0].timestamp"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Duration d = Duration.between(OffsetDateTime.now(), timestamp).abs();
        assertThat(d.toMinutes()).as("distance to now").isLessThanOrEqualTo(5);
    }

    // TODO: can find by absolute time
    // TODO: timezone is required for absolute time
    // TODO: can find by relative time
    // TODO: hours are optional for relative time, can use minutes >60


    private static Response getPrediction(long facilityId) {
        Response response = when().get(UrlSchema.FACILITY_PREDICTION, facilityId);
        response.then()
                .assertThat().statusCode(200)
                .assertThat().body("results", hasSize(1));
        return response;
    }

    private Utilization makeDummyPredictions() {
        Utilization u = new Utilization();
        u.facilityId = facilityId;
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.PARK_AND_RIDE;
        u.timestamp = new DateTime();
        u.spacesAvailable = 42;
        facilityService.registerUtilization(facilityId, Collections.singletonList(u), user);
        predictionService.enablePrediction(SameAsLatestPredictor.TYPE, u.getUtilizationKey());
        predictionService.updatePredictions();
        return u;
    }
}
