// Copyright © 2016 HSL <https://www.hsl.fi>
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
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
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

    private static final int CAR_BUILT_CAPACITY = 1000;

    private interface Key {
        String FACILITY_ID = "facilityId";
        String CAPACITY_TYPE = "capacityType";
        String USAGE = "usage";
        String TIMESTAMP = "timestamp";
        String SPACES_AVAILABLE = "spacesAvailable";
        String CAPACITY = "capacity";
    }

    @Inject ContactDao contactDao;
    @Inject FacilityDao facilityDao;
    @Inject OperatorDao operatorDao;

    private Facility facility;
    private Operator operator;
    private Contact contact;
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
        operator = createOperator(1, "smooth operator");
        contact = createContact(1, "minimal contact");
        facility = createFacility(1, "minimal facility", operator, contact);
        devHelper.createOrUpdateUser(new NewUser(1L, "operator", OPERATOR_API, facility.operatorId, "operator"));
        authToken = devHelper.login("operator").token;
    }

    @Test
    public void cannot_update_other_operators_facility() {
        Operator operator2 = createOperator(2, "another operator");
        Facility facility2 = createFacility(2, "another facility", operator2, contact);

        submitUtilization(FORBIDDEN, facility2.id, minValidPayload());
    }

    @Test
    public void accepts_ISO8601_UTC_timestamp() {
        submitUtilization(OK, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void accepts_ISO8601_non_UTC_timestamp() {
        submitUtilization(OK, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T13:00:00.000+02:00"));

        assertThat(getUtilizationTimestamp()).isEqualTo(new DateTime(2015, 1, 1, 11, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void rejects_epoch_timestamps() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, System.currentTimeMillis() / 1000)); // in seconds
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, System.currentTimeMillis())); // in milliseconds
    }

    @Test
    public void returns_timestamps_in_default_timezone() {
        DateTimeZone.setDefault(DateTimeZone.forOffsetHours(8));

        submitUtilization(OK, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000Z"));

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
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void timestamp_must_have_timezone() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00.000"))
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));
    }

    @Test
    public void timestamp_must_have_at_least_second_precision() {
        submitUtilization(OK, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00:00Z")); // second precision

        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01T11:00Z")) // minute precision
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));

        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, "2015-01-01")) // date precision
                .spec(assertResponse(HttpMessageNotReadableException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("TypeMismatch"))
                .body("violations[0].message", containsString("expected ISO 8601 date time with timezone"));
    }

    @Test
    public void timestamp_may_be_a_little_into_the_future() { // in case the server clocks are in different time
        submitUtilization(OK, facility.id, minValidPayload().put(Key.TIMESTAMP, DateTime.now().plusMinutes(2)));
    }

    @Test
    public void timestamp_must_not_be_far_into_the_future() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.TIMESTAMP, DateTime.now().plusMinutes(3)))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.TIMESTAMP))
                .body("violations[0].type", is("NotFuture"));
    }

    @Test
    public void capacity_type_is_required() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.CAPACITY_TYPE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.CAPACITY_TYPE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void usage_is_required() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.USAGE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.USAGE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void spaces_available_is_required() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.SPACES_AVAILABLE, null))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.SPACES_AVAILABLE))
                .body("violations[0].type", is("NotNull"));
    }

    @Test
    public void facility_id_in_playload_is_optional() {
        submitUtilization(OK, facility.id, minValidPayload().put(Key.FACILITY_ID, null));
        submitUtilization(OK, facility.id, minValidPayload().put(Key.FACILITY_ID, facility.id));
    }

    @Test
    public void facility_id_in_playload_cannot_be_different_from_facility_id_in_path() {
        submitUtilization(BAD_REQUEST, facility.id, minValidPayload().put(Key.FACILITY_ID, facility.id + 1))
                .spec(assertResponse(ValidationException.class))
                .body("violations[0].path", is("[0]." + Key.FACILITY_ID))
                .body("violations[0].type", is("NotEqual"));
    }

    @Test
    public void capacity_defaults_to_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .remove(Key.CAPACITY));

        Utilization[] utilizations = getFacilityUtilization(facility.id);
        assertThat(utilizations).hasSize(1);
        assertThat(utilizations[0].capacity).as("capacity").isEqualTo(CAR_BUILT_CAPACITY);
    }

    @Test
    public void capacity_defaults_to_spaces_available_if_no_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, MOTORCYCLE.name())
                .put(Key.SPACES_AVAILABLE, 666)
                .remove(Key.CAPACITY));
        assertThat(getFacilityUtilization(facility.id)).isEmpty();            // check that motorcycle doesn't yet have built capacity
        facility.builtCapacity = ImmutableMap.of(MOTORCYCLE, 1);   // give motorcycle some capacity to make its utilization visible
        facilityDao.updateFacility(facility.id, facility);

        Utilization[] utilizations = getFacilityUtilization(facility.id);
        assertThat(utilizations).hasSize(1);
        assertThat(utilizations[0].capacity).as("capacity").isEqualTo(666);
    }

    @Test
    public void updating_capacity_will_NOT_initialize_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, MOTORCYCLE.name())
                .put(Key.CAPACITY, 100));

        FacilityInfo facility = facilityDao.getFacilityInfo(this.facility.id);
        assertThat(facility.builtCapacity).doesNotContainKey(MOTORCYCLE);
    }

    @Test
    public void updating_capacity_may_increase_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, CAR.name())
                .put(Key.CAPACITY, CAR_BUILT_CAPACITY + 50));

        FacilityInfo facility = facilityDao.getFacilityInfo(this.facility.id);
        assertThat(facility.builtCapacity).containsEntry(CAR, CAR_BUILT_CAPACITY + 50);
    }

    @Test
    public void updating_capacity_will_NOT_decrease_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, CAR.name())
                .put(Key.CAPACITY, CAR_BUILT_CAPACITY - 50));

        FacilityInfo facility = facilityDao.getFacilityInfo(this.facility.id);
        assertThat(facility.builtCapacity).containsEntry(CAR, CAR_BUILT_CAPACITY);
    }

    @Test
    public void updating_capacity_may_increase_temporarily_unavailable_spaces() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, CAR.name())
                .put(Key.CAPACITY, CAR_BUILT_CAPACITY - 50));

        Facility facility = facilityDao.getFacility(this.facility.id);
        assertThat(facility.unavailableCapacities).contains(new UnavailableCapacity(CAR, PARK_AND_RIDE, 50));
    }

    @Test
    public void updating_capacity_may_decrease_temporarily_unavailable_spaces() {
        facility.unavailableCapacities.add(new UnavailableCapacity(CAR, PARK_AND_RIDE, 50));
        facilityDao.updateFacility(facility.id, facility);

        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.CAPACITY_TYPE, CAR.name())
                .put(Key.CAPACITY, CAR_BUILT_CAPACITY - 40));

        Facility facility = facilityDao.getFacility(this.facility.id);
        assertThat(facility.unavailableCapacities).contains(new UnavailableCapacity(CAR, PARK_AND_RIDE, 40));
    }

    @Test
    public void accepts_spaces_available_which_is_larger_than_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.SPACES_AVAILABLE, CAR_BUILT_CAPACITY + 666)
                .put(Key.CAPACITY, CAR_BUILT_CAPACITY));

        FacilityInfo facility = facilityDao.getFacilityInfo(this.facility.id);
        assertThat(facility.builtCapacity).containsEntry(CAR, CAR_BUILT_CAPACITY);
    }

    @Test
    public void accepts_spaces_available_which_is_larger_than_built_capacity() {
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.SPACES_AVAILABLE, CAR_BUILT_CAPACITY + 666)
                .remove(Key.CAPACITY));

        FacilityInfo facility = facilityDao.getFacilityInfo(this.facility.id);
        assertThat(facility.builtCapacity).containsEntry(CAR, CAR_BUILT_CAPACITY);
    }

    @Test
    public void does_not_show_utilizations_without_built_capacities() {
        multiCapacityCreate();

        facility.builtCapacity = ImmutableMap.of(CAR, 1000);
        facilityDao.updateFacility(facility.id, facility);

        Utilization[] results = getFacilityUtilization(facility.id);

        assertThat(results)
                .extracting("facilityId", "capacityType", "usage", "spacesAvailable")
                .containsOnly(tuple(facility.id, CAR, PARK_AND_RIDE, 1));
    }

    @Test
    public void does_not_show_utilizations_without_usages() {
        multiCapacityCreate();

        facility.builtCapacity = ImmutableMap.of(CAR, 1000);
        facility.pricingMethod = CUSTOM;
        facility.pricing = asList(
                new Pricing(CAR, COMMERCIAL, 1000, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, COMMERCIAL, 1000, SUNDAY, "00", "24", "0")
        );
        facilityDao.updateFacility(facility.id, facility);

        Utilization[] results = getFacilityUtilization(facility.id);

        assertThat(results).isEmpty();
    }

    @Test
    public void does_not_show_utilizations_when_usage_for_different_capacity_type() {
        // CAR & COMMERCIAL should not show up in the list
        // ELECTRIC_CAR & PARK_AND_RIDE should not show up in the list
        registerUtilizations(asList(
                utilize(CAR, PARK_AND_RIDE),
                utilize(ELECTRIC_CAR, PARK_AND_RIDE),
                utilize(CAR, COMMERCIAL),
                utilize(ELECTRIC_CAR, COMMERCIAL)
        ));
        facility.pricing = asList(
                new Pricing(CAR, PARK_AND_RIDE, 100, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(CAR, PARK_AND_RIDE, 100, SATURDAY, "00", "24", "0"),
                new Pricing(CAR, PARK_AND_RIDE, 100, SUNDAY, "00", "24", "0"),
                new Pricing(ELECTRIC_CAR, COMMERCIAL, 50, BUSINESS_DAY, "00", "24", "0"),
                new Pricing(ELECTRIC_CAR, COMMERCIAL, 50, SATURDAY, "00", "24", "0"),
                new Pricing(ELECTRIC_CAR, COMMERCIAL, 50, SUNDAY, "00", "24", "0")
        );
        facility.pricingMethod = CUSTOM;
        facilityDao.updateFacility(facility.id, facility);

        final Utilization[] results = getFacilityUtilization(facility.id);

        assertThat(results)
                .extracting(u -> tuple(u.capacityType, u.usage))
                .hasSize(2)
                .contains(
                        tuple(CAR, PARK_AND_RIDE),
                        tuple(ELECTRIC_CAR, COMMERCIAL)
                );
    }

    @Test
    public void lists_latest_utilization_for_all_facilities() {
        Facility facility2 = createFacility(2, "another facility", operator, contact);
        DateTime t1 = DateTime.now().minusHours(1);
        DateTime t2 = t1.plusMinutes(1);
        DateTime t3 = t1.plusMinutes(2);
        DateTime t4 = t1.plusMinutes(3);

        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.TIMESTAMP, t1)
                .put(Key.SPACES_AVAILABLE, 5));
        submitUtilization(OK, facility.id, minValidPayload()
                .put(Key.TIMESTAMP, t2)
                .put(Key.SPACES_AVAILABLE, 15));
        submitUtilization(OK, facility2.id, minValidPayload()
                .put(Key.TIMESTAMP, t3)
                .put(Key.SPACES_AVAILABLE, 25));
        submitUtilization(OK, facility2.id, minValidPayload()
                .put(Key.TIMESTAMP, t4)
                .put(Key.SPACES_AVAILABLE, 35));

        Utilization u2 = new Utilization();
        u2.facilityId = facility.id;
        u2.timestamp = t2;
        u2.spacesAvailable = 15;
        u2.capacityType = CAR;
        u2.usage = PARK_AND_RIDE;
        u2.capacity = CAR_BUILT_CAPACITY;
        Utilization u4 = new Utilization();
        u4.facilityId = facility2.id;
        u4.timestamp = t4;
        u4.spacesAvailable = 35;
        u4.capacityType = CAR;
        u4.usage = PARK_AND_RIDE;
        u4.capacity = CAR_BUILT_CAPACITY;
        assertThat(getUtilizations()).containsOnly(u2, u4);
    }


    // helpers

    private static JSONObjectBuilder minValidPayload() {
        return new JSONObjectBuilder()
                .put(Key.CAPACITY_TYPE, CapacityType.CAR)
                .put(Key.USAGE, Usage.PARK_AND_RIDE)
                .put(Key.TIMESTAMP, DateTime.now())
                .put(Key.SPACES_AVAILABLE, 42);
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
        Response response = when().get(UrlSchema.FACILITY_UTILIZATION, facility.id);
        response.then().assertThat().body(".", hasSize(1));
        return response.body().jsonPath().getString("[0].timestamp");
    }

    private void multiCapacityCreate() {
        multiCapacityCreate(Usage.PARK_AND_RIDE, Usage.PARK_AND_RIDE, Usage.PARK_AND_RIDE);
    }

    private void multiCapacityCreate(Usage usageCar, Usage usageBike, Usage usageElectric) {
        DateTime now = DateTime.now();

        Utilization u1 = new Utilization();
        u1.timestamp = now;
        u1.spacesAvailable = 1;
        u1.capacityType = CapacityType.CAR;
        u1.usage = usageCar;

        Utilization u2 = new Utilization();
        u2.timestamp = now.minusSeconds(10);
        u2.spacesAvailable = 1;
        u2.capacityType = CapacityType.BICYCLE;
        u2.usage = usageBike;

        Utilization u3 = new Utilization();
        u3.timestamp = now.minusSeconds(20);
        u3.spacesAvailable = 2;
        u3.capacityType = CapacityType.ELECTRIC_CAR;
        u3.usage = usageElectric;

        List<Utilization> payload = Lists.newArrayList(u1, u2, u3);

        registerUtilizations(payload);

        Utilization[] results = getFacilityUtilization(facility.id);

        assertThat(results)
                .extracting("facilityId", "capacityType", "usage", "spacesAvailable", "timestamp")
                .contains(
                        tuple(facility.id, u1.capacityType, u1.usage, u1.spacesAvailable, u1.timestamp.toInstant()),
                        tuple(facility.id, u2.capacityType, u2.usage, u2.spacesAvailable, u2.timestamp.toInstant()),
                        tuple(facility.id, u3.capacityType, u3.usage, u3.spacesAvailable, u3.timestamp.toInstant()));
    }

    private Utilization[] getFacilityUtilization(long facilityId) {
        return when().get(UrlSchema.FACILITY_UTILIZATION, facilityId)
                .then().statusCode(OK.value())
                .extract().as(Utilization[].class);
    }

    private Utilization[] getUtilizations() {
        return when().get(UrlSchema.UTILIZATIONS)
                .then().statusCode(OK.value())
                .extract().as(Utilization[].class);
    }

    private Utilization utilize(CapacityType capacityType, Usage usage) {
        final Utilization utilization = new Utilization();
        utilization.facilityId = facility.id;
        utilization.capacityType = capacityType;
        utilization.spacesAvailable = 50;
        utilization.usage = usage;
        utilization.timestamp = DateTime.now().minusSeconds(10);
        return utilization;
    }

    private void registerUtilizations(List<Utilization> payload) {
        givenWithContent(authToken)
                .body(payload)
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, facility.id)
                .then()
                .statusCode(OK.value());
    }

    public Facility createFacility(long id, String name, Operator operator, Contact contact) {
        Facility f = new Facility();
        f.id = id;
        f.status = IN_OPERATION;
        f.pricingMethod = PARK_AND_RIDE_247_FREE;
        f.name = new MultilingualString(name);
        f.operatorId = operator.id;
        f.location = Spatial.fromWktPolygon("POLYGON((25.010822 60.25054, 25.010822 60.250023, 25.012479 60.250337, 25.011449 60.250885, 25.010822 60.25054))");
        f.contacts = new FacilityContacts(contact.id, contact.id);
        f.builtCapacity = ImmutableMap.of(
                CAR, CAR_BUILT_CAPACITY,
                BICYCLE, 100,
                ELECTRIC_CAR, 10
        );
        facilityDao.insertFacility(f, f.id);
        return f;
    }

    public Contact createContact(long id, String name) {
        Contact c = new Contact();
        c.id = id;
        c.name = new MultilingualString(name);
        contactDao.insertContact(c, c.id);
        return c;
    }

    public Operator createOperator(long id, String name) {
        Operator o = new Operator();
        o.id = id;
        o.name = new MultilingualString(name);
        operatorDao.insertOperator(o, o.id);
        return o;
    }
}
