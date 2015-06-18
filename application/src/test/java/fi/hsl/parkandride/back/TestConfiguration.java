// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.config.CoreConfiguration;
import fi.hsl.parkandride.dev.DevHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

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

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        return new TransactionTemplate(manager);
    }
}
