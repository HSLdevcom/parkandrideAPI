package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.FacilityStatus;
import fi.hsl.parkandride.core.domain.FacilityStatusEnum;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;

public class FacilityStatusITest extends AbstractIntegrationTest {

    interface Key {
        String CAPACITY_TYPE = "capacityType";
        String SPACES_AVAILABLE = "spacesAvailable";
        String STATUS = "status";
        String TIMESTAMP = "timestamp";
    }

    @Test
    public void returns_ISO8601_UTC_timestamp() {
        when()
            .get(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(Key.TIMESTAMP, new ISO8601UTCTimestampMatcher())
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
        givenWithContent()
            .body(builder.asArray())
        .when()
            .put(UrlSchema.FACILITY_STATUS, 42)
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
        FacilityStatus spacesOnly = new FacilityStatus();
        spacesOnly.timestamp = Instant.now();
        spacesOnly.spacesAvailable = 1;
        spacesOnly.capacityType = CapacityType.CAR;

        FacilityStatus statusOnly = new FacilityStatus();
        statusOnly.timestamp = Instant.now();
        statusOnly.status = FacilityStatusEnum.FULL;
        statusOnly.capacityType = CapacityType.BICYCLE;

        FacilityStatus spacesAndStatus = new FacilityStatus();
        spacesAndStatus.timestamp = Instant.now();
        spacesAndStatus.spacesAvailable = 2;
        spacesAndStatus.status = FacilityStatusEnum.SPACES_AVAILABLE;
        spacesAndStatus.capacityType = CapacityType.PARK_AND_RIDE;

        List<FacilityStatus> payload = Lists.newArrayList(spacesOnly, statusOnly, spacesAndStatus);

        givenWithContent()
            .body(payload)
        .when()
            .log().all()
            .put(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .statusCode(HttpStatus.OK.value())
        ;
    }

    @Test
    public void timestamp_is_required() {
        givenWithContent()
            .body(minValidPayload().put(Key.TIMESTAMP, null).asArray())
        .when()
            .put(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
            .body("violations[0].path", is(Key.TIMESTAMP))
            .body("violations[0].type", is("NotNull"))
        ;
    }

    @Test
    public void capacity_type_is_required() {
        givenWithContent()
            .body(minValidPayload().put(Key.CAPACITY_TYPE, null).asArray())
        .when()
            .put(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
            .body("violations[0].path", is(Key.CAPACITY_TYPE))
            .body("violations[0].type", is("NotNull"))
        ;
    }

    @Test
    public void spaces_or_status_is_required() {
        givenWithContent()
            .body(minValidPayload()
                    .put(Key.SPACES_AVAILABLE, null)
                    .put(Key.STATUS, null)
                    .asArray())
        .when()
            .put(UrlSchema.FACILITY_STATUS, 42)
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
}
