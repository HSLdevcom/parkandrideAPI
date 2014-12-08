package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.front.UrlSchema;

public class ErrorHandlingITest extends AbstractIntegrationTest {
    @Test
    public void non_existing_resource_is_communicated_with_status_code_only() {
        when()
            .get(UrlSchema.FACILITIES + "/42")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body(is(""))
        ;
    }

    @Test
    public void validation_errors_are_detailed_as_violations() {
        givenWithContent()
            .body(new Facility())
        .when()
            .post(UrlSchema.FACILITIES)
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, ValidationException.class))
            .body("message", is("Invalid data. See violations for details."))
            .body("violations", is(notNullValue()))
        ;
    }

    @Test
    public void typical_json_mapping_violations_are_detected() {
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
    public void geolatte_json_mapping_violations_are_not_detected() {
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
    public void unsupported_request_methods_are_detected() {
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
    public void request_parameter_binding_errors_are_detailed_as_violations() {
        when()
            .get(UrlSchema.FACILITIES + "?ids=foo")
        .then()
            .spec(assertResponse(HttpStatus.BAD_REQUEST, BindException.class))
            .body("message", is("Invalid request parameters"))
            .body("violations[0].path", is("ids"))
            .body("violations[0].message", is("Failed to convert property value of type 'java.lang.String' to required type 'java.util.Set' for property " +
                    "'ids'; nested exception is java.lang.NumberFormatException: For input string: \"foo\""))
        ;
    }

    @Test
    public void invalid_path_variable_type_is_reported_as_internal_error() {
        when()
            .get(UrlSchema.FACILITIES + "/foo")
        .then()
            .spec(assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, TypeMismatchException.class))
            .body("message", is("Failed to convert value of type 'java.lang.String' to required type 'long'; nested exception is " +
                    "java.lang.NumberFormatException: For input string: \"foo\""))
            .body("violations", is(nullValue()))
        ;
    }

    // HttpMediaTypeException: unclear how to trigger
}
