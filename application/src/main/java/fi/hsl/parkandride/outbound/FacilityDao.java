package fi.hsl.parkandride.outbound;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.List;

import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.QBean;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.outbound.FacilitySearch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.sql.QFacility;

public class FacilityDao implements FacilityRepository {

    private static final QFacility qFacility = QFacility.facility;

    private static final QBean<Facility> facilityMapping = new QBean<>(Facility.class, true, qFacility.all());

    private static final SimpleExpression<Long> nextFacilityId = SQLExpressions.nextval("facility_id_seq");

    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertFacility(Facility facility) {
        SQLInsertClause insert = insert();
        insert.set(qFacility.id, nextFacilityId);
        populate(facility, insert);
        long id = insert.executeWithKey(qFacility.id);
        facility.id = id;
        return id;
    }

    @TransactionalWrite
    @Override
    public void updateFacility(Facility facility) {
        checkNotNull(facility, "facility");
        SQLUpdateClause update = update();
        update.where(qFacility.id.eq(facility.id));
        populate(facility, update);
        if (update.execute() != 1) {
            throw new IllegalArgumentException(format("Facility#%s not found", facility.id));
        }
    }

    @TransactionalRead
    @Override
    public Facility getFacility(long id) {
        Facility facility = query().where(qFacility.id.eq(id)).singleResult(facilityMapping);
        if (facility == null) {
            throw new IllegalArgumentException(format("Facility#%s not found", id));
        }
        return facility;
    }

    @TransactionalRead
    @Override
    public List<Facility> findFacilities(FacilitySearch search) { // TODO: add search and paging parameters
        PostgresQuery qry = query();
        qry.limit(search.limit);
        qry.offset(search.offset);

        if (search.within != null) {
            qry.where(qFacility.border.intersects(search.within));
        }

        return qry.list(facilityMapping);
    }

    private void populate(Facility facility, StoreClause store) {
        store.set(qFacility.name, facility.name);
        store.set(qFacility.border, facility.border);
    }

    private SQLInsertClause insert() {
        return queryFactory.insert(qFacility);
    }

    private SQLUpdateClause update() {
        return queryFactory.update(qFacility);
    }

    private PostgresQuery query() {
        return queryFactory.from(qFacility);
    }

}
