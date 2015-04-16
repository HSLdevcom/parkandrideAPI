// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import fi.hsl.parkandride.Application;
import fi.hsl.parkandride.DevApiProfileAppender;
import fi.hsl.parkandride.dev.DevHelper;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(resolver = DevApiProfileAppender.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public abstract class AbstractIntegrationTest {
    @Value("${local.server.port}")
    protected int port;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        RestAssured.port = port;

        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        RestAssured.config = config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Inject
    protected DevHelper devHelper;

    public static String resourceAsString(String resourcePath) {
        try {
            return Resources.toString(Resources.getResource(resourcePath), Charsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError("Loading of resource '" + resourcePath + "' failed: " + e);
        }
    }

    public static RequestSpecification givenWithContent() {
        return givenWithContent(null);
    }

    public static RequestSpecification givenWithContent(String authToken) {
        RequestSpecification spec = given().spec(new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build());
        if (authToken != null) {
            return spec.header("Authorization", "Bearer " + authToken);
        } else {
            return spec;
        }
    }

    public static ResponseSpecification assertResponse(Class<?> exClass) {
        return assertResponse(null, exClass);
    }

    public static ResponseSpecification assertResponse(HttpStatus status, Class<?> exClass) {
        Matcher<Integer> statusMatcher = (status == null)
                ? is(greaterThanOrEqualTo(HttpStatus.BAD_REQUEST.value()))
                : is(status.value());
        return new ResponseSpecBuilder()
                .expectStatusCode(statusMatcher)
                .expectBody("status", statusMatcher)
                .expectBody("exception", is(exClass.getCanonicalName()))
                .expectBody("timestamp", new ISO8601UTCTimestampMatcher())
                .build();
    }
}
