package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.FacilityStatus;
import fi.hsl.parkandride.front.UrlSchema;

public class FacilityStatusITest extends AbstractIntegrationTest {

    interface Key {
        String CAPACITY_TYPE = "capacityType";
        String SPACES_AVAILABLE = "spacesAvailable";
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

    private JSONObjectBuilder minValidPayload() {
        return new JSONObjectBuilder()
                .put(Key.CAPACITY_TYPE, CapacityType.CAR)
                .put(Key.SPACES_AVAILABLE, 42)
                .put(Key.TIMESTAMP, DateTime.now().getMillis());
    }
}
