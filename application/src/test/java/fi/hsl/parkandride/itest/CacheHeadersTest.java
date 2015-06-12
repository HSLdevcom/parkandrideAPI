// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import fi.hsl.parkandride.back.Dummies;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import static com.jayway.restassured.RestAssured.when;

public class CacheHeadersTest extends AbstractIntegrationTest {

    @Inject Dummies dummies;

    @Test
    public void api_urls_are_not_cached() {
        when().get("api/v1/facilities").then()
                .statusCode(HttpStatus.OK.value())
                .header("Cache-Control", "no-cache");
    }

    @Test
    public void api_urls_are_not_cached__deep() {
        long facilityId = dummies.createFacility();

        when().get("api/v1/facilities/" + facilityId).then()
                .statusCode(HttpStatus.OK.value())
                .header("Cache-Control", "no-cache");
    }
}
