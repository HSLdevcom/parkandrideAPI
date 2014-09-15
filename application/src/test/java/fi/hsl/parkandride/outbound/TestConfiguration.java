package fi.hsl.parkandride.outbound;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.config.CoreConfiguration;

@Configuration
@Import(CoreConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class TestConfiguration {

    @Inject
    PostgresQueryFactory queryFactory;

    @Bean
    public TestHelper testHelper() {
        return new TestHelper(queryFactory);
    }

}
