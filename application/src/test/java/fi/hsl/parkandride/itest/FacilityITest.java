package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

public class FacilityITest extends AbstractIntegrationTest{
    @Test
    public void facilities_can_queried() {
        when()
            .get("api/v1/facilities")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat()
                .body("results", hasItems())
                .body("hasMore", is(false));
        ;
    }
}
