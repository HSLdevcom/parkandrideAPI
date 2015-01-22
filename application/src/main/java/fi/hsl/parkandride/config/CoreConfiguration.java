package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${security.token.secret}")
    String tokenSecret;

    @Value("${security.token.expires}")
    String tokenExpires;

    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(
                userRepository(),
                passwordEncryptor(),
                tokenSecret,
                periodFormatter.parsePeriod(tokenExpires)
        );
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository(), authenticationService(), validationService());
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
    public ContactRepository contactRepository() {
        return new ContactDao(queryFactory);
    }


    @Bean
    public ContactService contactService() {
        return new ContactService(contactRepository(), validationService());
    }

    @Bean
    public OperatorRepository operatorRepository() {
        return new OperatorDao(queryFactory);
    }

    @Bean
    public OperatorService operatorService() {
        return new OperatorService(operatorRepository(), validationService());
    }

    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityDao(queryFactory);
    }

    @Bean
    public FacilityService facilityService () {
        return new FacilityService(facilityRepository(), contactRepository(), validationService());
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
