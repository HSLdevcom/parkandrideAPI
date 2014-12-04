package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

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
        given()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(new Facility())
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", is(HttpStatus.BAD_REQUEST.value()))
            .body("exception", is(ValidationException.class.getCanonicalName()))
        ;
    }

    @Test
    public void httpMessageNotReadableException() {
        given()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body("{ \"name\": \"foo\", \"location\": \"invalid location\"  }")
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", is(HttpStatus.BAD_REQUEST.value()))
            .body("exception", is(HttpMessageNotReadableException.class.getCanonicalName()))
        ;
    }

    @Test
    public void httpRequestMethodNotSupportedException() {
        given()
            .log().all()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(new Facility())
        .when()
            .put(UrlSchema.FACILITIES)
        .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", is(HttpStatus.BAD_REQUEST.value()))
            .body("exception", is(HttpRequestMethodNotSupportedException.class.getCanonicalName()))
        ;
    }

    // HttpMessageNotReadableException_jsonMappingException: unclear how to trigger
    // HttpMediaTypeException: unclear how to trigger
    // bindException: unclear how to trigger
    // exception: unclear how to trigger
}
