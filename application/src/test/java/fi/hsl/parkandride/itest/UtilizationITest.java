// Copyright © 2015 HSL

package fi.hsl.parkandride.itest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.jayway.restassured.response.Response;
import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
    @TransactionalWrite
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

        givenWithContent(authToken)
                .body(minValidPayload().asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f2.id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void accepts_ISO8601_UTC_timestamp() {
        submitUtilization(minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void accepts_ISO8601_non_UTC_timestamp() {
        submitUtilization(minValidPayload().put(Key.TIMESTAMP, "2015-01-01T13:00:00.000+02:00"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void accepts_epoch_timestamp_in_milliseconds() {
        submitUtilization(minValidPayload().put(Key.TIMESTAMP, 1420110000123L));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, 123, DateTimeZone.UTC));
    }

    @Test
    public void returns_timestamps_in_default_timezone() {
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(8));

        submitUtilization(minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

        assertThat(getUtilizationTimestampString()).isEqualTo("2015-01-01T19:00:00.000+08:00");
    }

    private void submitUtilization(JSONObjectBuilder builder) {
        givenWithContent(authToken).body(builder.asArray())
                .when().put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then().statusCode(HttpStatus.OK.value());
    }

    private DateTime getUtilizationTimestamp() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(getUtilizationTimestampString());
    }

    private String getUtilizationTimestampString() {
        Response response = when().get(UrlSchema.FACILITY_UTILIZATION, f.id);
        response.then().assertThat().body("results", hasSize(1));
        return response.body().jsonPath().getString("results[0].timestamp");
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
                .statusCode(HttpStatus.OK.value());

        StatusResults r =
                when()
                        .get(UrlSchema.FACILITY_UTILIZATION, f.id)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(StatusResults.class);

        assertThat(r.results)
                .extracting("facilityId", "capacityType", "usage", "spacesAvailable", "timestamp")
                .contains(
                        tuple(f.id, u1.capacityType, u1.usage, u1.spacesAvailable, u1.timestamp.toInstant()),
                        tuple(f.id, u2.capacityType, u2.usage, u2.spacesAvailable, u2.timestamp.toInstant()),
                        tuple(f.id, u3.capacityType, u3.usage, u3.spacesAvailable, u3.timestamp.toInstant()));
    }

    @Test
    public void timestamp_is_required() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.TIMESTAMP, null).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
                .body("violations[0].path", is(Key.TIMESTAMP))
                .body("violations[0].type", is("NotNull"))
        ;
    }

    @Test
    public void capacity_type_is_required() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.CAPACITY_TYPE, null).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
                .body("violations[0].path", is(Key.CAPACITY_TYPE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void usage_is_required() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.USAGE, null).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
                .body("violations[0].path", is(Key.USAGE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void spaces_available_is_required() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.SPACES_AVAILABLE, null).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
                .body("violations[0].path", is(Key.SPACES_AVAILABLE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void facility_id_in_playload_is_optional() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.FACILITY_ID, null).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .statusCode(HttpStatus.OK.value());

        givenWithContent(authToken)
                .body(minValidPayload().put(Key.FACILITY_ID, f.id).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void facility_id_in_playload_cannot_be_different_from_facility_id_in_path() {
        givenWithContent(authToken)
                .body(minValidPayload().put(Key.FACILITY_ID, f.id + 1).asArray())
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
                .then()
                .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
                .body("violations[0].path", is(Key.FACILITY_ID))
                .body("violations[0].type", is("NotEqual"));
    }

    private static JSONObjectBuilder minValidPayload() {
        return new JSONObjectBuilder()
                .put(Key.CAPACITY_TYPE, CapacityType.CAR)
                .put(Key.USAGE, Usage.PARK_AND_RIDE)
                .put(Key.SPACES_AVAILABLE, 42)
                .put(Key.TIMESTAMP, DateTime.now().getMillis());
    }

    public static class StatusResults {
        public List<Utilization> results;
    }
}
