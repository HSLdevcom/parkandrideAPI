package fi.hsl.parkandride.dev;

import static fi.hsl.parkandride.back.ContactDao.CONTACT_ID_SEQ;
import static fi.hsl.parkandride.back.FacilityDao.FACILITY_ID_SEQ;
import static fi.hsl.parkandride.back.HubDao.HUB_ID_SEQ;
import static java.lang.String.format;

import javax.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.service.TransactionalWrite;

@Component
@Profile({"dev_api"})
public class DevHelper {
    private final PostgresQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    private static QFacility qFacility = QFacility.facility;
    private static QHub qHub = QHub.hub;
    private static QContact qContact = QContact.contact;

    @Inject
    public DevHelper(PostgresQueryFactory queryFactory, JdbcTemplate jdbcTemplate) {
        this.queryFactory = queryFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    @TransactionalWrite
    public void clear(RelationalPath... tables) {
        for (RelationalPath table : tables) {
            queryFactory.delete(table).execute();
        }
    }

    @TransactionalWrite
    public void resetContacts() {
        clear(QContact.contact);
    }

    @TransactionalWrite
    public void resetFacilities() {
        clear(
                QFacilityStatus.facilityStatus,
                QFacilityService.facilityService,
                QFacilityAlias.facilityAlias,
                QCapacity.capacity,
                QPort.port,
                QFacility.facility)
        ;
    }

    @TransactionalWrite
    public void resetHubs() {
        clear(QHubFacility.hubFacility, QHub.hub);
    }

    @TransactionalWrite
    public void resetHubSequence() {
        resetSequence(HUB_ID_SEQ, queryFactory.from(qHub).singleResult(qHub.id.max()));
    }

    @TransactionalWrite
    public void resetFacilitySequence() {
        resetSequence(FACILITY_ID_SEQ, queryFactory.from(qFacility).singleResult(qFacility.id.max()));
    }

    @TransactionalWrite
    public void resetContactSequence() {
        resetSequence(CONTACT_ID_SEQ, queryFactory.from(qContact).singleResult(qContact.id.max()));
    }

    @TransactionalWrite
    public void resetSequence(String sequence) {
        resetSequence(sequence, 0l);
    }

    @TransactionalWrite
    public void resetSequence(String sequence, Long currentMax) {
        if (currentMax == null) {
            currentMax = 0l;
        }
        jdbcTemplate.execute(format("drop sequence %s", sequence));
        jdbcTemplate.execute(format("create sequence %s increment by 1 start with %s", sequence, currentMax+1));
    }
}
