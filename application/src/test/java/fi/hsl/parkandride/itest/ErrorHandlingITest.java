package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.TypeMismatchException;
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
            .body("violations", is(notNullValue()))
        ;
    }

    @Test
    public void httpMessageNotReadableException_geolatte_violations_are_not_detected() {
        givenWithContent()
            .body("{ \"name\": \"foo\", \"location\": \"this is not readable location\"  }")
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, HttpMessageNotReadableException.class))
            .body("violations", is(nullValue()))
        ;
    }

    @Test
    public void httpMessageNotReadableException_typical_json_mapping_violations_are_detected() throws IOException {
        givenWithContent()
            .body(resourceAsString("facility.create.JsonMappingException.json"))
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, HttpMessageNotReadableException.class))
            .body("violations[0].path", is("capacities.built"))
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

    @Test
    public void invalid_path_variable_type_is_reported_with_status_code_500() {
        when()
            .get(UrlSchema.FACILITIES + "/foo")
        .then()
            .spec(assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, TypeMismatchException.class))
            .body("message", is("Failed to convert value of type 'java.lang.String' to required type 'long'; nested exception is java.lang.NumberFormatException: For input string: \"foo\""))
        ;
    }

    // HttpMediaTypeException: unclear how to trigger
    // bindException: unclear how to trigger
}
