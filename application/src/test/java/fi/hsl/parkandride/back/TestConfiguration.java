// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.config.CoreConfiguration;
import fi.hsl.parkandride.dev.DevHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@Import(CoreConfiguration.class)
@ComponentScan(basePackageClasses = DevHelper.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class TestConfiguration {

    @Bean
    public Dummies dummies() {
        return new Dummies();
    }
}
