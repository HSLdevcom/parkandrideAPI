// Copyright © 2015 HSL

package fi.hsl.parkandride.dev;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.hsl.parkandride.FeatureProfile;
import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.AuthenticationService;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.inject.Inject;

import static fi.hsl.parkandride.back.ContactDao.CONTACT_ID_SEQ;
import static fi.hsl.parkandride.back.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.back.HubDao.HUB_ID_SEQ;
import static fi.hsl.parkandride.back.OperatorDao.OPERATOR_ID_SEQ;
import static fi.hsl.parkandride.back.PredictorDao.PREDICTOR_ID_SEQ;
import static fi.hsl.parkandride.back.UserDao.USER_ID_SEQ;
import static java.lang.String.format;

@Component
@Profile({FeatureProfile.DEV_API})
public class DevHelper {
    private final PostgresQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Resource UserRepository userRepository;

    @Resource UserService userService;

    @Resource AuthenticationService authenticationService;

    @Inject
    public DevHelper(PostgresQueryFactory queryFactory, JdbcTemplate jdbcTemplate) {
        this.queryFactory = queryFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    @TransactionalWrite
    public void deleteAll() {
        deletePredictors();
        deleteHubs();
        deleteFacilities();
        deleteContacts();
        deleteUsers();
        deleteOperators();
    }

    @TransactionalWrite
    public void deleteContacts() {
        delete(QContact.contact);
        resetContactSequence();
    }

    @TransactionalWrite
    public void deleteUsers() {
        delete(QAppUser.appUser);
        resetUserSequence();
    }

    @TransactionalWrite
    public User createOrUpdateUser(NewUser newUser) {
        UserSecret userSecret;
        try {
            userSecret = userRepository.getUser(newUser.username);
            if (newUser.role != userSecret.user.role) {
                userRepository.updateUser(userSecret.user.id, newUser);
            }
            if (newUser.password != null) {
                userRepository.updatePassword(userSecret.user.id, authenticationService.encryptPassword(newUser.password));
            }
        } catch (NotFoundException e) {
            userSecret = new UserSecret();
            userSecret.user = userService.createUserNoValidate(newUser);
        }
        return userSecret.user;
    }

    @TransactionalRead
    public Login login(String username) {
        UserSecret userSecret = userRepository.getUser(username);
        Login login = new Login();
        login.token = authenticationService.token(userSecret.user);
        login.username = userSecret.user.username;
        login.role = userSecret.user.role;
        login.operatorId = userSecret.user.operatorId;
        login.permissions = login.role.permissions;
        return login;
    }

    @TransactionalWrite
    public void deleteOperators() {
        delete(QOperator.operator);
        resetOperatorSequence();
    }

    @TransactionalWrite
    public void deleteFacilities() {
        delete(
                QFacilityPrediction.facilityPrediction,
                QFacilityUtilization.facilityUtilization,
                QFacilityService.facilityService,
                QFacilityPaymentMethod.facilityPaymentMethod,
                QFacilityAlias.facilityAlias,
                QPricing.pricing,
                QPort.port,
                QUnavailableCapacity.unavailableCapacity,
                QFacility.facility);
        resetFacilitySequence();
    }

    @TransactionalWrite
    public void deleteHubs() {
        delete(QHubFacility.hubFacility, QHub.hub);
        resetHubSequence();
    }

    @TransactionalWrite
    public void deletePredictors() {
        delete(QPredictor.predictor);
        resetPredictorSequence();
    }

    @TransactionalWrite
    public void resetHubSequence() {
        resetSequence(HUB_ID_SEQ, queryFactory.from(QHub.hub).singleResult(QHub.hub.id.max()));
    }

    @TransactionalWrite
    public void resetFacilitySequence() {
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(QFacility.facility).singleResult(QFacility.facility.id.max()));
    }

    @TransactionalWrite
    public void resetPredictorSequence() {
        resetSequence(PREDICTOR_ID_SEQ, queryFactory.from(QPredictor.predictor).singleResult(QPredictor.predictor.id.max()));
    }

    @TransactionalWrite
    public void resetContactSequence() {
        resetSequence(CONTACT_ID_SEQ, queryFactory.from(QContact.contact).singleResult(QContact.contact.id.max()));
    }

    @TransactionalWrite
    public void resetUserSequence() {
        resetSequence(USER_ID_SEQ, queryFactory.from(QAppUser.appUser).singleResult(QAppUser.appUser.id.max()));
    }

    @TransactionalWrite
    public void resetOperatorSequence() {
        resetSequence(OPERATOR_ID_SEQ, queryFactory.from(QOperator.operator).singleResult(QOperator.operator.id.max()));
    }

    private void delete(RelationalPath... tables) {
        for (RelationalPath table : tables) {
            queryFactory.delete(table).execute();
        }
    }

    private void resetSequence(String sequence, Long currentMax) {
        if (currentMax == null) {
            currentMax = 0L;
        }
        jdbcTemplate.execute(format("drop sequence %s", sequence));
        jdbcTemplate.execute(format("create sequence %s increment by 1 start with %s", sequence, currentMax + 1));
    }
}
