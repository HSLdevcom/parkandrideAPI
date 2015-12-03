// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.inject.Inject;
import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.DayType.SATURDAY;
import static fi.hsl.parkandride.core.domain.DayType.SUNDAY;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static fi.hsl.parkandride.core.domain.PricingMethod.CUSTOM;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

public class UtilizationITest extends AbstractIntegrationTest {

    private interface Key {
        String FACILITY_ID = "facilityId";
        String CAPACITY_TYPE = "capacityType";
        String USAGE = "usage";
        String SPACES_AVAILABLE = "spacesAvailable";
        String TIMESTAMP = "timestamp";
    }

    @Inject ContactDao contactDao;
    @Inject FacilityDao facilityDao;
    @Inject OperatorDao operatorDao;

    private Facility f;
    private String authToken;
    private DateTimeZone originalDateTimeZone;

    @Before
    public void setTimezone() {
        originalDateTimeZone = DateTimeZone.getDefault();
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    @After
    public void restoreTimezone() {
        DateTimeZone.setDefault(originalDateTimeZone);
    }

    @Before
    public void initFixture() {
        devHelper.deleteAll();

        Operator o = new Operator();
        o.id = 1l;
        o.name = new MultilingualString("smooth operator");

        Contact c = new Contact();
        c.id = 1L;
        c.name = new MultilingualString("minimal contact");

        f = new Facility();
        f.id = 1L;
        f.status = IN_OPERATION;
        f.pricingMethod = PARK_AND_RIDE_247_FREE;
        f.name = new MultilingualString("minimal facility");
        f.operatorId = 1l;
        f.location = Spatial.fromWktPolygon("POLYGON((25.010822 60.25054, 25.010822 60.250023, 25.012479 60.250337, 25.011449 60.250885, 25.010822 60.25054))");
        f.contacts = new FacilityContacts(c.id, c.id);
        f.builtCapacity = ImmutableMap.of(
                CAR, 1000,
                BICYCLE, 100,
                ELECTRIC_CAR, 10
        );

        operatorDao.insertOperator(o, o.id);
        contactDao.insertContact(c, c.id);
        facilityDao.insertFacility(f, f.id);

        devHelper.createOrUpdateUser(new NewUser(1l, "operator", OPERATOR_API, f.operatorId, "operator"));
        authToken = devHelper.login("operator").token;
    }

    @Test
    public void cannot_update_other_operators_facility() {
        Operator o = new Operator();
        o.id = 2l;
        o.name = new MultilingualString("another operator");

        Facility f2 = new Facility();
        f2.id = 2L;
        f2.status = IN_OPERATION;
        f2.pricingMethod = PARK_AND_RIDE_247_FREE;
        f2.name = new MultilingualString("another facility");
        f2.operatorId = 2l;
        f2.location = Spatial.fromWktPolygon("POLYGON((25.010822 60.25054, 25.010822 60.250023, 25.012479 60.250337, 25.011449 60.250885, 25.010822 60.25054))");
        f2.contacts = new FacilityContacts(1l, 1l);

        operatorDao.insertOperator(o, o.id);
        facilityDao.insertFacility(f2, f2.id);

        submitUtilization(FORBIDDEN, f2.id, minValidPayload());
    }

    @Test
    public void accepts_ISO8601_UTC_timestamp() {
        submitUtilization(OK, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void accepts_ISO8601_non_UTC_timestamp() {
        submitUtilization(OK, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T13:00:00.000+02:00"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void rejects_epoch_timestamps() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, System.currentTimeMillis() / 1000)); // in seconds
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, System.currentTimeMillis())); // in milliseconds
    }

    @Test
    public void returns_timestamps_in_default_timezone() {
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(8));

        submitUtilization(OK, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

        assertThat(getUtilizationTimestampString()).isEqualTo("2015-01-01T19:00:00.000+08:00");
    }

    @Test
    public void accepts_unset_optional_values_with_null_value() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        multiCapacityCreate();
    }

    @Test
    public void accepts_unset_optional_values_to_be_absent() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        multiCapacityCreate();
    }

    @Test
    public void timestamp_is_required() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void timestamp_must_have_timezone() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000"))
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));
    }

    @Test
    public void timestamp_must_have_at_least_second_precision() {
        submitUtilization(OK, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00Z")); // second precision

        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00Z")) // minute precision
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));

        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01")) // date precision
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));
    }

    @Test
    public void timestamp_may_be_a_little_into_the_future() { // in case the server clocks are in different time
        submitUtilization(OK, f.id, minValidPayload().put(Key.TIMESTAMP, DateTime.now().plusMinutes(2)));
    }

    @Test
    public void timestamp_must_not_be_far_into_the_future() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.TIMESTAMP, DateTime.now().plusMinutes(3)))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("NotFuture"));
    }

    @Test
    public void capacity_type_is_required() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.CAPACITY_TYPE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.CAPACITY_TYPE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void usage_is_required() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.USAGE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.USAGE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void spaces_available_is_required() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.SPACES_AVAILABLE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.SPACES_AVAILABLE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void facility_id_in_playload_is_optional() {
        submitUtilization(OK, f.id, minValidPayload().put(Key.FACILITY_ID, null));
        submitUtilization(OK, f.id, minValidPayload().put(Key.FACILITY_ID, f.id));
    }

    @Test
    public void facility_id_in_playload_cannot_be_different_from_facility_id_in_path() {
        submitUtilization(BAD_REQUEST, f.id, minValidPayload().put(Key.FACILITY_ID, f.id + 1))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.FACILITY_ID))
                .body("violations[0].type", is("NotEqual"));
    }


    @Test
    public void does_not_show_utilizations_without_built_capacities() {
        multiCapacityCreate();

        f.builtCapacity = ImmutableMap.of(CAR, 1000);
        facilityDao.updateFacility(f.id, f);

        Utilization[] results = getUtilizations();

        assertThat(results)
                .extracting("facilityId", "capacityType", "usage", "spacesAvailable")
                .containsOnly(tuple(f.id, CAR, PARK_AND_RIDE, 1));
    }

    @Test
    public void does_not_show_utilizations_without_usages() {
        multiCapacityCreate();

        f.builtCapacity = ImmutableMap.of(CAR, 1000);
        f.pricingMethod = CUSTOM;
        f.pricing = asList(
                new Pricing(CAR, COMMERCIAL, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SUNDAY, "00", "24", "0")
        );
        facilityDao.updateFacility(f.id, f);

        Utilization[] results = getUtilizations();

        assertThat(results).isEmpty();
    }

    private Utilization[] getUtilizations() {
        return when().get(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then().statusCode(OK.value())
                .extract().as(Utilization[].class);
    }

    // helpers

    private static JSONObjectBuilder minValidPayload() {
        return new JSONObjectBuilder()
                .put(Key.CAPACITY_TYPE, CapacityType.CAR)
                .put(Key.USAGE, Usage.PARK_AND_RIDE)
                .put(Key.SPACES_AVAILABLE, 42)
                .put(Key.TIMESTAMP, DateTime.now());
    }

    private ValidatableResponse submitUtilization(HttpStatus expectedStatus, Long facilityId, JSONObjectBuilder builder) {
        return givenWithContent(authToken).body(builder.asArray())
                .when().put(UrlSchema.FACILITY_UTILIZATION, facilityId)
                .then().statusCode(expectedStatus.value());
    }

    private DateTime getUtilizationTimestamp() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(getUtilizationTimestampString());
    }

    private String getUtilizationTimestampString() {
        Response response = when().get(UrlSchema.FACILITY_UTILIZATION, f.id);
        response.then().assertThat().body(".", hasSize(1));
        return response.body().jsonPath().getString("[0].timestamp");
    }

    private void multiCapacityCreate() {
        DateTime now = DateTime.now();

        Utilization u1 = new Utilization();
        u1.timestamp = now;
        u1.spacesAvailable = 1;
        u1.capacityType = CapacityType.CAR;
        u1.usage = Usage.PARK_AND_RIDE;

        Utilization u2 = new Utilization();
        u2.timestamp = now.minusSeconds(10);
        u2.spacesAvailable = 1;
        u2.capacityType = CapacityType.BICYCLE;
        u2.usage = Usage.PARK_AND_RIDE;

        Utilization u3 = new Utilization();
        u3.timestamp = now.minusSeconds(20);
        u3.spacesAvailable = 2;
        u3.capacityType = CapacityType.ELECTRIC_CAR;
        u3.usage = Usage.PARK_AND_RIDE;

        List<Utilization> payload = Lists.newArrayList(u1, u2, u3);

        givenWithContent(authToken)
                .body(payload)
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .statusCode(OK.value());

        Utilization[] results = getUtilizations();

        assertThat(results)
                .extracting("facilityId", "capacityType", "usage", "spacesAvailable", "timestamp")
                .contains(
                        tuple(f.id, u1.capacityType, u1.usage, u1.spacesAvailable, u1.timestamp.toInstant()),
                        tuple(f.id, u2.capacityType, u2.usage, u2.spacesAvailable, u2.timestamp.toInstant()),
                        tuple(f.id, u3.capacityType, u3.usage, u3.spacesAvailable, u3.timestamp.toInstant()));
    }
}
