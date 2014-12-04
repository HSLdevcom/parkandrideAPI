package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;

public class ErrorHandlingITest extends AbstractIntegrationTest {
    @Test
    public void notFound() {
        when()
            .get(UrlSchema.FACILITIES + "/42")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body(is(""))
        ;
    }

    @Test
    public void validationException() {
        givenWithContent()
            .body(new Facility())
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
        ;
    }

    @Test
    public void httpMessageNotReadableException() {
        givenWithContent()
            .body("{ \"name\": \"foo\", \"location\": \"this is not readable location\"  }")
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, HttpMessageNotReadableException.class))
        ;
    }

    @Test
    public void httpMessageNotReadableException_jsonMappingException() throws IOException {
        givenWithContent()
            .body(resourceAsString("facility.create.JsonMappingException.json"))
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, HttpMessageNotReadableException.class))
        ;
    }

    @Test
    public void httpRequestMethodNotSupportedException() {
        givenWithContent()
            .body(new Facility())
        .when()
            .put(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, HttpRequestMethodNotSupportedException.class))
            .body("message", is("Request method 'PUT' not supported"))
        ;
    }

    // HttpMediaTypeException: unclear how to trigger
    // bindException: unclear how to trigger
    // exception: unclear how to trigger
}
