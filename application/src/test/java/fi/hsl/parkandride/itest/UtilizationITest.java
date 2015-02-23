package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.is;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;

public class UtilizationITest extends AbstractIntegrationTest {

    interface Key {
        String CAPACITY_TYPE = "capacityType";
        String SPACES_AVAILABLE = "spacesAvailable";
        String STATUS = "status";
        String TIMESTAMP = "timestamp";
    }

    @Inject
    private ContactDao contactDao;

    @Inject
    private FacilityDao facilityDao;

    @Inject
    private OperatorDao operatorDao;

    private Facility f;

    private String authToken;

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
    public void returns_ISO8601_UTC_timestamp() {
        JSONObjectBuilder expected = minValidPayload();
        givenWithContent(authToken)
            .body(expected.asArray())
        .when()
            .put(UrlSchema.FACILITY_UTILIZATION, f.id)
        .then()
            .statusCode(HttpStatus.OK.value())
        ;

        when()
            .get(UrlSchema.FACILITY_UTILIZATION, f.id)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("results[0]." + Key.TIMESTAMP, new ISO8601UTCTimestampMatcher())
            .body("results[0]." + Key.CAPACITY_TYPE, is(expected.jsonObject.get(Key.CAPACITY_TYPE).toString()))
            .body("results[0]." + Key.SPACES_AVAILABLE, is(expected.jsonObject.get(Key.SPACES_AVAILABLE)))
        ;
    }

    @Test
    public void accepts_ISO8601_UTC_timestamp() {
        test_accept_timestamp(minValidPayload().put(Key.TIMESTAMP, DateTime.now(DateTimeZone.forOffsetHours(0))));
    }

    @Test
    public void accepts_ISO8601_non_UTC_timestamp() {
        test_accept_timestamp(minValidPayload().put(Key.TIMESTAMP, DateTime.now(DateTimeZone.forOffsetHours(2))));
    }

    @Test
    public void accepts_epoch_timestamp() {
        test_accept_timestamp(minValidPayload().put(Key.TIMESTAMP, DateTime.now().getMillis()));
    }

    private void test_accept_timestamp(JSONObjectBuilder builder) {
        givenWithContent(authToken)
            .body(builder.asArray())
        .when()
            .put(UrlSchema.FACILITY_UTILIZATION, f.id)
        .then()
            .statusCode(HttpStatus.OK.value())
        ;
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

        Utilization spacesOnly = new Utilization();
        spacesOnly.timestamp = now;
        spacesOnly.spacesAvailable = 1;
        spacesOnly.capacityType = CapacityType.CAR;

        Utilization statusOnly = new Utilization();
        statusOnly.timestamp = now.minusSeconds(10);
        statusOnly.status = UtilizationStatus.FULL;
        statusOnly.capacityType = CapacityType.BICYCLE;

        Utilization spacesAndStatus = new Utilization();
        spacesAndStatus.timestamp = now.minusSeconds(20);
        spacesAndStatus.spacesAvailable = 2;
        spacesAndStatus.status = UtilizationStatus.SPACES_AVAILABLE;
        spacesAndStatus.capacityType = CapacityType.ELECTRIC_CAR;

        List<Utilization> payload = Lists.newArrayList(spacesOnly, statusOnly, spacesAndStatus);

        givenWithContent(authToken)
            .body(payload)
                .when()
                .put(UrlSchema.FACILITY_UTILIZATION, f.id)
        .then()
            .statusCode(HttpStatus.OK.value())
        ;

        StatusResults r =
            when()
                .get(UrlSchema.FACILITY_UTILIZATION, f.id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(StatusResults.class)
                ;

        assertThat(r.results)
                .extracting("capacityType", "spacesAvailable", "status", "timestamp")
                .contains(
                        tuple(spacesOnly.capacityType, spacesOnly.spacesAvailable, spacesOnly.status, spacesOnly.timestamp.toInstant()),
                        tuple(statusOnly.capacityType, statusOnly.spacesAvailable, statusOnly.status, statusOnly.timestamp.toInstant()),
                        tuple(spacesAndStatus.capacityType, spacesAndStatus.spacesAvailable, spacesAndStatus.status, spacesAndStatus.timestamp.toInstant())
                )
        ;
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
            .body("violations[0].type", is("NotNull"))
        ;
    }

    @Test
    public void spaces_or_status_is_required() {
        givenWithContent(authToken)
            .body(minValidPayload()
                    .put(Key.SPACES_AVAILABLE, null)
                    .put(Key.STATUS, null)
                    .asArray())
        .when()
            .put(UrlSchema.FACILITY_UTILIZATION, f.id)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
            .body("violations[0].type", is("SpacesAvailableOrStatusRequired"))
        ;
    }

    private JSONObjectBuilder minValidPayload() {
        return new JSONObjectBuilder()
                .put(Key.CAPACITY_TYPE, CapacityType.CAR)
                .put(Key.SPACES_AVAILABLE, 42)
                .put(Key.TIMESTAMP, DateTime.now().getMillis());
    }

    public static class StatusResults {
        public List<Utilization> results;
    }
}
