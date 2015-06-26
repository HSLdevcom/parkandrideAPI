// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.config;

import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.prediction.Predictor;
import fi.hsl.parkandride.core.domain.prediction.SameAsLatestPredictor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TestConfiguration {

    @Bean
    public Dummies dummies() {
        return new Dummies();
    }

    @Bean
    public Predictor[] predictors() {
        // for testing purposes we prefer a simple and predictable predictor
        return new Predictor[]{new SameAsLatestPredictor()};
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        return new TransactionTemplate(manager);
    }
}
