// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.config;

import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.back.*;
import fi.hsl.parkandride.back.prediction.PredictionDao;
import fi.hsl.parkandride.back.prediction.PredictorDao;
import fi.hsl.parkandride.core.back.*;
import fi.hsl.parkandride.core.domain.prediction.Predictor;
import fi.hsl.parkandride.core.domain.prediction.RelativizedAverageOfPreviousWeeksPredictor;
import fi.hsl.parkandride.core.service.*;
import fi.hsl.parkandride.core.service.reporting.*;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import java.security.SecureRandom;

@Configuration
@Import(JdbcConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
public class CoreConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CoreConfiguration.class);

    private static final String SECURITY_TOKEN_SECRET = "security.token.secret";

    @Inject PostgreSQLQueryFactory queryFactory;
    @Inject PlatformTransactionManager transactionManager;
    @Value("${" + SECURITY_TOKEN_SECRET + "}") String tokenSecret;
    @Value("${security.token.expires}") String tokenExpires;
    @Value("${password.expires}") String passwordExpires;
    @Value("${password.reminder}") String passwordReminder;

    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(
                userRepository(),
                passwordEncryptor(),
                tokenSecret(),
                periodFormatter.parsePeriod(tokenExpires),
                periodFormatter.parsePeriod(passwordExpires),
                periodFormatter.parsePeriod(passwordReminder)
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
    public TranslationService translationService() {
        return new TranslationService();
    }

    @Bean
    public ExcelUtil excelUtil(MessageSource messageSource) {
        return new ExcelUtil(messageSource);
    }

    @Bean
    public HubsAndFacilitiesReportService hubsAndFacilitiesReportService() {
        return new HubsAndFacilitiesReportService(facilityService(), operatorService(), contactService(), hubService(), utilizationRepository(), regionRepository(), translationService(), facilityHistoryService());
    }

    @Bean
    public RequestLogReportService requestLogReportService() {
        return new RequestLogReportService();
    }

    @Bean
    public FacilityUsageReportService facilityUsageReportService() {
        return new FacilityUsageReportService(facilityService(), operatorService(), contactService(), hubService(), utilizationRepository(), regionRepository(), translationService(), facilityHistoryService());
    }

    @Bean
    public MaxUtilizationReportService maxUtilizationReportService() {
        return new MaxUtilizationReportService(facilityService(), operatorService(), contactService(), hubService(), utilizationRepository(), regionRepository(), translationService(), facilityHistoryService());
    }

    @Bean
    public FacilityHistoryService facilityHistoryService() {
        return new FacilityHistoryService(facilityHistoryRepository(), facilityRepository());
    }

    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityDao(queryFactory, facilityHistoryRepository());
    }

    @Bean
    public FacilityHistoryRepository facilityHistoryRepository() {
        return new FacilityHistoryDao(queryFactory);
    }

    @Bean
    public FacilityService facilityService() {
        return new FacilityService(facilityRepository(), utilizationRepository(), contactRepository(), validationService(), predictionService());
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

    @Bean
    public PredictionService predictionService() {
        return new PredictionService(utilizationRepository(), predictionRepository(), predictorRepository(), transactionManager, predictors());
    }

    @Bean
    public Predictor[] predictors() {
        return new Predictor[]{new RelativizedAverageOfPreviousWeeksPredictor()};
    }

    @Bean
    public UtilizationRepository utilizationRepository() {
        return new UtilizationDao(queryFactory);
    }

    @Bean
    public RegionRepository regionRepository() {
        return new RegionDao(queryFactory);
    }

    @Bean
    public PredictionRepository predictionRepository() {
        return new PredictionDao(queryFactory, validationService());
    }

    @Bean
    public PredictorRepository predictorRepository() {
        return new PredictorDao(queryFactory, validationService());
    }

    @Bean
    public RequestLogRepository requestLogRepository() {
      return new RequestLogDao(queryFactory);
    }

    @Bean
    public BatchingRequestLogService batchingRequestLogService(RequestLogRepository requestLogRepository) {
      return new BatchingRequestLogService(requestLogRepository);
    }
}
