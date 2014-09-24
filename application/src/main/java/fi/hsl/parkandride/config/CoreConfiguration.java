package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.outbound.HubRepository;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.outbound.FacilityDao;
import fi.hsl.parkandride.outbound.HubDao;

@Configuration
@Import(JdbcConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class CoreConfiguration {

    @Inject PostgresQueryFactory queryFactory;

    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityDao(queryFactory);
    }

    @Bean
    public FacilityService facilityService () {
        return new FacilityService(facilityRepository());
    }

    @Bean
    public HubRepository hubRepository() {
        return new HubDao(queryFactory);
    }
}
