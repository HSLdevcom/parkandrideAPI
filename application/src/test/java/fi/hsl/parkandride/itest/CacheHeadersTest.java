// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.jayway.restassured.RestAssured.when;

public class CacheHeadersTest extends AbstractIntegrationTest {

    @Test
    public void api_urls_are_not_cached() {
        when().get("api/v1/facilities").then()
                .statusCode(HttpStatus.OK.value())
                .header("Cache-Control", "no-cache");
    }
}
