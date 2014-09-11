package fi.hsl.parkandride.outbound;

import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.outbound.sql.QFacility;

public class FacilityDao implements FacilityRepository {

    private static final QFacility qFacility = QFacility.facility;

    private static final SimpleExpression<Long> nextFacilityId = SQLExpressions.nextval("facility_id_seq");

    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertFacility(Facility facility) {
        SQLInsertClause insert = insert();
        setFields(facility, insert);
        insert.set(qFacility.id, nextFacilityId);
        return insert.executeWithKey(qFacility.id);
    }

    private void setFields(Facility facility, StoreClause store) {
        store.set(qFacility.name, facility.name);
        store.set(qFacility.border, facility.border);
    }

    private SQLInsertClause insert() {
        return queryFactory.insert(qFacility);
    }

}
