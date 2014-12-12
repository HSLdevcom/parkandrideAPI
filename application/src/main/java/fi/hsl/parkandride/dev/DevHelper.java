package fi.hsl.parkandride.dev;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public class DevHelper {

    private final PostgresQueryFactory queryFactory;

    public DevHelper(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
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
}
