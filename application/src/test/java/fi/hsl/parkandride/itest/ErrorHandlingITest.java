package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.io.Serializable;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.http.ContentType;

import fi.hsl.parkandride.core.domain.Facility;

public class ErrorHandlingITest extends AbstractIntegrationTest {
    @Test
    public void notFound() {
        when()
            .get("api/v1/facilities/42")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body(is(""))
        ;
    }

    @Test
    public void validationException() {
        given()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(new Facility())
        .when()
            .post("/api/v1/facilities")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", is(HttpStatus.BAD_REQUEST.value()))
            .body("message", is("Invalid data. See violations for details."))
        ;
    }

    // bindException
    // jsonException
    // methodNotSupportedException
    // exception
}
