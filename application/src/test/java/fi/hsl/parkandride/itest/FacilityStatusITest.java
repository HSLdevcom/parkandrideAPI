package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import fi.hsl.parkandride.front.UrlSchema;

public class FacilityStatusITest extends AbstractIntegrationTest {
    @Test
    public void returns_ISO8601_UTC_timestamp() {
        when()
            .get(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("timestamp", new ISO8601UTCTimestampMatcher())
        ;
    }

    @Test
    public void accepts_ISO8601_UTC_timestamp() {
        test_accept_timestamp("[ { \"timestamp\": \"" + DateTime.now(DateTimeZone.forOffsetHours(0)) + "\" } ]");
    }

    @Test
    public void accepts_ISO8601_non_UTC_timestamp() {
        test_accept_timestamp("[ { \"timestamp\": \"" + DateTime.now(DateTimeZone.forOffsetHours(2)) + "\" } ]");
    }

    @Test
    public void accepts_epoch_timestamp() {
        test_accept_timestamp("[ { \"timestamp\": " + DateTime.now().getMillis() + " } ]");
    }

    private void test_accept_timestamp(String body) {
        givenWithContent()
            .body(body)
        .when()
            .put(UrlSchema.FACILITY_STATUS, 42)
        .then()
            .statusCode(HttpStatus.OK.value())
        ;
    }
}
