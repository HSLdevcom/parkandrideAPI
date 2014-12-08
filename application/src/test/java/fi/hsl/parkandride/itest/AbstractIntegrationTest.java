package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import fi.hsl.parkandride.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public abstract class AbstractIntegrationTest {
    @Value("${local.server.port}")
    protected int port;

    @Before
    public void setup() {
        RestAssured.port = port;

        RestAssured.config =  RestAssuredConfig.config().objectMapperConfig(ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(
                new Jackson2ObjectMapperFactory() {
                    @Override
                    public ObjectMapper create(Class cls, String charset) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JodaModule());
                        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                        return objectMapper;
                    }
                }
        ));
    }

    public static String resourceAsString(String resourcePath) {
        try {
            return Resources.toString(Resources.getResource(resourcePath), Charsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError("Loading of resource '" + resourcePath + "' failed: " + e);
        }
    }

    public static RequestSpecification givenWithContent() {
        return given().spec(new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build());
    }

    public static ResponseSpecification assertResponse(HttpStatus status, Class<?> exClass) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status.value())
                .expectBody("status", is(status.value()))
                .expectBody("exception", is(exClass.getCanonicalName()))
                .expectBody("timestamp", is(notNullValue()))
                .build();
    }
}
