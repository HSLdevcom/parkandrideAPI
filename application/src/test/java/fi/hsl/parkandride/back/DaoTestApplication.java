// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.config.CoreConfiguration;
import fi.hsl.parkandride.config.TestConfiguration;
import fi.hsl.parkandride.dev.DevHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@Import({CoreConfiguration.class, TestConfiguration.class})
@ComponentScan(basePackageClasses = DevHelper.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class DaoTestApplication {
}
