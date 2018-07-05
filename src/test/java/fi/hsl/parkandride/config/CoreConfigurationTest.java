// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.config;

import fi.hsl.parkandride.core.service.AuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class CoreConfigurationTest {

    @Test
    public void returns_a_long_enough_tokenSecret_as_is() {
        String goodSecret = StringUtils.repeat('x', AuthenticationService.SECRET_MIN_LENGTH);
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.tokenSecret = goodSecret;

        assertThat(configuration.tokenSecret(), is(goodSecret));
    }

    @Test
    public void replaces_a_too_short_tokenSecret_with_a_randomly_generated_value() {
        String badSecret = "bad secret";
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.tokenSecret = badSecret;

        assertThat(configuration.tokenSecret(), is(not(badSecret)));
        assertThat("random value every time", configuration.tokenSecret(), is(not(configuration.tokenSecret())));
    }
}
