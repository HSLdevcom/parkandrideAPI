// Copyright © 2015 HSL

package fi.hsl.parkandride.config;

import javax.inject.Inject;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.HubDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.back.UserDao;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.service.*;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
@Import(JdbcConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class CoreConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CoreConfiguration.class);

    private static final String SECURITY_TOKEN_SECRET = "security.token.secret";

    @Inject
    PostgresQueryFactory queryFactory;

    @Value("${" + SECURITY_TOKEN_SECRET + "}")
    String tokenSecret;

    @Value("${security.token.expires}")
    String tokenExpires;

    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(
                userRepository(),
                passwordEncryptor(),
                tokenSecret(),
                periodFormatter.parsePeriod(tokenExpires)
        );
    }

    String tokenSecret() {
        String secret = tokenSecret;
        int minLength = AuthenticationService.SECRET_MIN_LENGTH;
        if (secret.length() < minLength) {
            log.warn("The value of {} is shorter than {} characters; replacing it with a randomly generated value. " +
                    "This may cause tokens to expire prematurely.", SECURITY_TOKEN_SECRET, minLength);
            int[] chars = new SecureRandom()
                    .ints(minLength)
                    .map(i -> (char) i)
                    .toArray();
            return new String(chars, 0, chars.length);
        } else {
            return secret;
        }
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
    public FacilityService facilityService() {
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
