package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.*;
import fi.hsl.parkandride.core.back.*;
import fi.hsl.parkandride.core.service.*;

@Configuration
@Import(JdbcConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class CoreConfiguration {

    @Inject PostgresQueryFactory queryFactory;

    @Bean
    public AuthenticationService userService() {
        return new AuthenticationService(userRepository(), passwordEncryptor());
    }

    @Bean
    public PasswordEncryptor passwordEncryptor() {
        return new StrongPasswordEncryptor();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserDao(queryFactory);
    }

    @Bean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    public ContactRepository contactRepository() {
        return new ContactDao(queryFactory);
    }

    @Bean
    public ContactService contactService() {
        return new ContactService(contactRepository(), validationService(), authService());
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
    public OperatorRepository operatorRepository() {
        return new OperatorDao(queryFactory);
    }

    @Bean
    public OperatorService operatorService() {
        return new OperatorService(operatorRepository());
    }

    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityDao(queryFactory);
    }

    @Bean
    public FacilityService facilityService () {
        return new FacilityService(facilityRepository(), validationService(), authService());
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
        return new HubService(hubRepository(), validationService(), authService());
    }
}
