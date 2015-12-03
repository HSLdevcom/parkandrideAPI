// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.domain.prediction.HubPredictionResult;
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
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.DayType.SATURDAY;
import static fi.hsl.parkandride.core.domain.DayType.SUNDAY;
import static fi.hsl.parkandride.core.domain.PricingMethod.CUSTOM;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Role.ADMIN;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.HSL_TRAVEL_CARD;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

public class PredictionITest extends AbstractIntegrationTest {

    private static final int SPACES_AVAILABLE = 42;
    @Inject Dummies dummies;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;

    private long facilityId;
    private Facility f;
    private User user;
    private final DateTime now = new DateTime();
    private User adminUser;

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        facilityId = dummies.createFacility();
        f = facilityService.getFacility(facilityId);

        Long operatorId = f.operatorId;
        user = devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR_API, operatorId, "operator"));

        adminUser = devHelper.createOrUpdateUser(new NewUser(100l, "admin", ADMIN, "admin"));

        // Ensure validation passes
        f.pricingMethod = PricingMethod.PARK_AND_RIDE_247_FREE;
        f.pricing = emptyList();
        facilityService.updateFacility(f.id, f, adminUser);
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

        f.builtCapacity = ImmutableMap.of(CAR, 1000);
        f.pricingMethod = CUSTOM;
        f.pricing = asList(
                new Pricing(CAR, HSL_TRAVEL_CARD, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, HSL_TRAVEL_CARD, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, HSL_TRAVEL_CARD, 1000, SUNDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SUNDAY, "00", "24", "0")
        );
        facilityService.updateFacility(f.id, f, adminUser);

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

    @Test
    public void does_not_show_prediction_if_no_built_capacity() {
        makeDummyPredictions();

        assertThat(getPredictions(facilityId)).hasSize(1)
            .extracting(pr -> pr.capacityType)
            .containsExactly(CapacityType.CAR);

        f.builtCapacity = ImmutableMap.of(CapacityType.BICYCLE, 1);
        facilityService.updateFacility(f.id, f, adminUser);

        assertThat(getPredictions(facilityId)).isEmpty();
    }

    @Test
    public void does_not_show_prediction_if_no_usage() {
        makeDummyPredictions(COMMERCIAL);
        assertThat(getPredictions(facilityId)).isEmpty();
    }

    // predictions for hubs

    @Test
    public void hub_sums_predictions_for_facilities() {
        final long facility2Id = dummies.createFacility();
        final Long operator2Id = facilityService.getFacility(facility2Id).operatorId;
        final User user2 = devHelper.createOrUpdateUser(new NewUser(2L, "operator2", OPERATOR_API, operator2Id, "operator"));
        final long hubId = dummies.createHub(facilityId, facility2Id);

        makeDummyPredictions(Usage.PARK_AND_RIDE, facilityId, user);
        makeDummyPredictions(Usage.PARK_AND_RIDE, facility2Id, user2);
        makeDummyPredictions(Usage.COMMERCIAL, facility2Id, user2);

        final Facility f2 = facilityService.getFacility(facility2Id);
        f2.builtCapacity = ImmutableMap.of(CAR, 1000);
        f2.pricing = asList(
                new Pricing(CAR, PARK_AND_RIDE, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, PARK_AND_RIDE, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, PARK_AND_RIDE, 1000, SUNDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SUNDAY, "00", "24", "0")
        );;
        f2.pricingMethod = PricingMethod.CUSTOM;
        facilityService.updateFacility(f2.id, f2, adminUser);

        final HubPredictionResult[] predictionsForHub = getPredictionsForHub(hubId);
        assertThat(predictionsForHub).hasSize(2);

        assertThat(predictionsForHub[0].hubId).isEqualTo(hubId);
        assertThat(predictionsForHub[0].capacityType).isEqualTo(CapacityType.CAR);
        assertIsNear(DateTime.now(), predictionsForHub[0].timestamp);

        // The predictions should be sums of facilities' predictions
        final HubPredictionResult parkAndRide = stream(predictionsForHub)
                .filter(pred -> pred.usage == Usage.PARK_AND_RIDE)
                .findFirst().get();
        assertThat(parkAndRide.spacesAvailable).isEqualTo(SPACES_AVAILABLE * 2);

        final HubPredictionResult commercial = stream(predictionsForHub)
                .filter(pred -> pred.usage == Usage.COMMERCIAL)
                .findFirst().get();
        assertThat(commercial.spacesAvailable).isEqualTo(SPACES_AVAILABLE);
    }

    @Test
    public void hub_excludes_prediction_if_no_built_capacity() {
        final long facility2Id = dummies.createFacility();
        final Long operator2Id = facilityService.getFacility(facility2Id).operatorId;
        final User user2 = devHelper.createOrUpdateUser(new NewUser(2L, "operator2", OPERATOR_API, operator2Id, "operator"));
        final long hubId = dummies.createHub(facilityId, facility2Id);

        makeDummyPredictions(Usage.PARK_AND_RIDE, facilityId, user);
        makeDummyPredictions(Usage.PARK_AND_RIDE, facility2Id, user2);
        makeDummyPredictions(Usage.COMMERCIAL, facility2Id, user2);

        final Facility f2 = facilityService.getFacility(facility2Id);
        f2.pricing = emptyList();
        f2.pricingMethod = PricingMethod.PARK_AND_RIDE_247_FREE;
        f2.builtCapacity = ImmutableMap.of(CapacityType.BICYCLE, 1);
        facilityService.updateFacility(f2.id, f2, adminUser);

        final HubPredictionResult[] predictionsForHub = getPredictionsForHub(hubId);
        assertThat(predictionsForHub).hasSize(1);

        assertThat(predictionsForHub[0].hubId).isEqualTo(hubId);
        assertThat(predictionsForHub[0].capacityType).isEqualTo(CapacityType.CAR);
        assertIsNear(DateTime.now(), predictionsForHub[0].timestamp);


        // Only first facility is taken into account, since the second one does not have built capacity
        final HubPredictionResult parkAndRide = stream(predictionsForHub)
                .filter(pred -> pred.usage == Usage.PARK_AND_RIDE)
                .findFirst().get();
        assertThat(parkAndRide.spacesAvailable).isEqualTo(SPACES_AVAILABLE);
    }

    @Test
    public void hub_excludes_prediction_if_no_usage() {
        final long facility2Id = dummies.createFacility();
        final Long operator2Id = facilityService.getFacility(facility2Id).operatorId;
        final User user2 = devHelper.createOrUpdateUser(new NewUser(2L, "operator2", OPERATOR_API, operator2Id, "operator"));
        final long hubId = dummies.createHub(facilityId, facility2Id);

        makeDummyPredictions(Usage.PARK_AND_RIDE, facilityId, user);
        makeDummyPredictions(Usage.PARK_AND_RIDE, facility2Id, user2);
        makeDummyPredictions(Usage.COMMERCIAL, facility2Id, user2);

        final Facility f2 = facilityService.getFacility(facility2Id);
        f2.builtCapacity = ImmutableMap.of(CAR, 1000);
        f2.pricingMethod = PARK_AND_RIDE_247_FREE;
        f2.pricing = emptyList();
        facilityService.updateFacility(f2.id, f2, adminUser);

        final HubPredictionResult[] predictionsForHub = getPredictionsForHub(hubId);

        assertThat(predictionsForHub[0].hubId).isEqualTo(hubId);
        assertThat(predictionsForHub[0].capacityType).isEqualTo(CapacityType.CAR);
        assertIsNear(DateTime.now(), predictionsForHub[0].timestamp);


        // Commer
        assertThat(predictionsForHub).hasSize(1)
            .extracting(hpr -> hpr.usage).containsExactly(PARK_AND_RIDE);
        final HubPredictionResult parkAndRide = predictionsForHub[0];
        assertThat(parkAndRide.spacesAvailable).isEqualTo(SPACES_AVAILABLE * 2);
    }

    @Test
    public void hub_with_no_facilities_returns_empty_prediction() {
        final long hubId = dummies.createHub();
        assertThat(getPredictionsForHub(hubId)).isEmpty();
    }

    @Test
    public void hub_with_no_predictions_returns_empty_prediction() {
        final long facility2Id = dummies.createFacility();
        final long hubId = dummies.createHub(facilityId, facility2Id);

        assertThat(getPredictionsForHub(hubId)).isEmpty();
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

    private static HubPredictionResult[] getPredictionsForHub(long hubId) {
        return toHubPredictions(when().get(UrlSchema.HUB_PREDICTION, hubId));
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

    private static HubPredictionResult[] toHubPredictions(Response response) {
        return response.then().assertThat().statusCode(HttpStatus.OK.value())
                .extract().as(HubPredictionResult[].class);
    }

    private Utilization makeDummyPredictions() {
        return makeDummyPredictions(Usage.PARK_AND_RIDE);
    }

    private Utilization makeDummyPredictions(Usage usage) {
        return makeDummyPredictions(usage, facilityId, user);
    }

    private Utilization makeDummyPredictions(Usage usage, long facilityId, User user) {
        Utilization u = new Utilization();
        u.facilityId = facilityId;
        u.capacityType = CapacityType.CAR;
        u.usage = usage;
        u.timestamp = now;
        u.spacesAvailable = SPACES_AVAILABLE;
        facilityService.registerUtilization(facilityId, Collections.singletonList(u), user);
        predictionService.updatePredictions();
        return u;
    }
}
