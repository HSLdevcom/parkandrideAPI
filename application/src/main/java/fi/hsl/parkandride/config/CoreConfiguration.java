package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.HubDao;
import fi.hsl.parkandride.back.ServiceDao;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.back.ServiceRepository;
import fi.hsl.parkandride.core.service.ContactService;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.HubService;
import fi.hsl.parkandride.core.service.ServiceService;
import fi.hsl.parkandride.core.service.ValidationService;

@Configuration
@Import(JdbcConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class CoreConfiguration {

    @Inject PostgresQueryFactory queryFactory;

    @Bean
    public ContactRepository contactRepository() {
        return new ContactDao(queryFactory);
    }

    @Bean
    public ContactService contactService() {
        return new ContactService(contactRepository(), validationService());
    }

    @Bean
    public ServiceRepository serviceRepository() {
        return new ServiceDao(queryFactory);
    }

    @Bean
    public ServiceService serviceService() {
        return new ServiceService(serviceRepository());
    }

    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityDao(queryFactory);
    }

    @Bean
    public FacilityService facilityService () {
        return new FacilityService(facilityRepository(), validationService());
    }

    @Bean
    public ValidationService validationService() {
        return new ValidationService();
    }

    @Bean
    public HubRepository hubRepository() {
        return new HubDao(queryFactory);
    }

    @Bean
    public HubService hubService() {
        return new HubService(hubRepository(), validationService());
    }
}
