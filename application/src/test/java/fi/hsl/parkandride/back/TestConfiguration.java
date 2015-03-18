// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fi.hsl.parkandride.config.CoreConfiguration;
import fi.hsl.parkandride.dev.DevHelper;

@Configuration
@EnableAutoConfiguration
@Import(CoreConfiguration.class)
@ComponentScan(basePackageClasses = DevHelper.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class TestConfiguration {
}
