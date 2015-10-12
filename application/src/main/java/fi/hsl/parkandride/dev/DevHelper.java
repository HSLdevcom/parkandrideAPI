// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.dev;

import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.FeatureProfile;
import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.back.UserRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Objects;

import static fi.hsl.parkandride.back.ContactDao.CONTACT_ID_SEQ;
import static fi.hsl.parkandride.back.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.back.HubDao.HUB_ID_SEQ;
import static fi.hsl.parkandride.back.OperatorDao.OPERATOR_ID_SEQ;
import static fi.hsl.parkandride.back.UserDao.USER_ID_SEQ;
import static fi.hsl.parkandride.back.prediction.PredictorDao.PREDICTOR_ID_SEQ;
import static java.lang.String.format;

@Component
@Profile({FeatureProfile.DEV_API})
public class DevHelper {
    private final PostgreSQLQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Resource UserRepository userRepository;

    @Resource UserService userService;

    @Resource AuthenticationService authenticationService;

    @Resource BatchingRequestLogService batchingRequestLogService;

    @Inject
    public DevHelper(PostgreSQLQueryFactory queryFactory, JdbcTemplate jdbcTemplate) {
        this.queryFactory = queryFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    @TransactionalWrite
    public void deleteAll() {
        deleteHubs();
        deleteFacilities();
        deleteContacts();
        deleteUsers();
        deleteOperators();
        deleteRequestLog();
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
            if (newUser.operatorId != null && !Objects.equals(newUser.operatorId, userSecret.user.operatorId)) {
                throw new IllegalArgumentException("Tried to create user '" + newUser.username + "' with operatorId " + newUser.operatorId
                        + ", but there already was a user with same name and operatorId " + userSecret.user.operatorId
                        + " and we can't change the operatorId afterwards");
            }
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
                QFacilityPredictionHistory.facilityPredictionHistory,
                QPredictor.predictor,
                QFacilityUtilization.facilityUtilization,
                QFacilityService.facilityService,
                QFacilityPaymentMethod.facilityPaymentMethod,
                QFacilityAlias.facilityAlias,
                QPricing.pricing,
                QPort.port,
                QUnavailableCapacity.unavailableCapacity,
                QFacility.facility);
        resetPredictorSequence();
        resetFacilitySequence();
    }

    @TransactionalWrite
    public void deleteHubs() {
        delete(QHubFacility.hubFacility, QHub.hub);
        resetHubSequence();
    }

    @TransactionalWrite
    private void deleteRequestLog() {
        batchingRequestLogService.clearLogStash();
        delete(QRequestLog.requestLog);
        delete(QRequestLogSource.requestLogSource);
        delete(QRequestLogUrl.requestLogUrl);
    }

    @TransactionalWrite
    public void resetHubSequence() {
        resetSequence(HUB_ID_SEQ, queryFactory.from(QHub.hub).select(QHub.hub.id.max()).fetchOne());
    }

    @TransactionalWrite
    public void resetFacilitySequence() {
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(QFacility.facility).select(QFacility.facility.id.max()).fetchOne());
    }

    @TransactionalWrite
    public void resetPredictorSequence() {
        resetSequence(PREDICTOR_ID_SEQ, queryFactory.from(QPredictor.predictor).select(QPredictor.predictor.id.max()).fetchOne());
    }

    @TransactionalWrite
    public void resetContactSequence() {
        resetSequence(CONTACT_ID_SEQ, queryFactory.from(QContact.contact).select(QContact.contact.id.max()).fetchOne());
    }

    @TransactionalWrite
    public void resetUserSequence() {
        resetSequence(USER_ID_SEQ, queryFactory.from(QAppUser.appUser).select(QAppUser.appUser.id.max()).fetchOne());
    }

    @TransactionalWrite
    public void resetOperatorSequence() {
        resetSequence(OPERATOR_ID_SEQ, queryFactory.from(QOperator.operator).select(QOperator.operator.id.max()).fetchOne());
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
