package fi.hsl.parkandride.outbound;

import static com.mysema.query.types.Projections.fields;
import static java.lang.String.format;

import java.util.List;
import java.util.Set;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.group.GroupBy;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.QBean;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.HubNotFoundException;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;
import fi.hsl.parkandride.core.outbound.HubRepository;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.sql.QHub;
import fi.hsl.parkandride.outbound.sql.QHubFacility;

public class HubDao implements HubRepository {

    private static final SimpleExpression<Long> nextHubId = SQLExpressions.nextval("hub_id_seq");

    private static final QHub qHub = QHub.hub;

    private static final QHubFacility qHubFacility = QHubFacility.hubFacility;

    private static final QBean<Hub> hubMapping = fields(Hub.class,
            qHub.id, qHub.name, qHub.location,
            GroupBy.set(qHubFacility.facilityId).as("facilityIds"));


    private final PostgresQueryFactory queryFactory;

    public HubDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalWrite
    public long insertHub(Hub hub) {
        long hubId = queryFactory.query().singleResult(nextHubId);

        SQLInsertClause insert = queryFactory.insert(qHub);
        insert.set(qHub.id, hubId);
        populate(hub, insert);
        insert.execute();

        insertHubFacilities(hubId, hub.facilityIds);
        return hubId;
    }

    private void populate(Hub hub, StoreClause store) {
        store.set(qHub.name, hub.name);
        store.set(qHub.location, hub.location);
    }

    private void insertHubFacilities(long hubId, Set<Long> facilityIds) {
        if (facilityIds != null && !facilityIds.isEmpty()) {
            SQLInsertClause insertBatch = queryFactory.insert(qHubFacility);
            for (Long facilityId : facilityIds) {
                insertBatch.set(qHubFacility.hubId, hubId);
                insertBatch.set(qHubFacility.facilityId, facilityId);
                insertBatch.addBatch();
            }
            insertBatch.execute();
        }
    }

    @Override
    @TransactionalWrite
    public void updateHub(long hubId, Hub hub) {
        SQLUpdateClause update = queryFactory.update(qHub);
        update.where(qHub.id.eq(hubId));
        populate(hub, update);
        if (update.execute() != 1) {
            throw new HubNotFoundException(hubId);
        }

        deleteHubFacilities(hubId);
        insertHubFacilities(hubId, hub.facilityIds);
    }

    private void deleteHubFacilities(long hubId) {
        queryFactory.delete(qHubFacility).where(qHubFacility.hubId.eq(hubId)).execute();
    }

    @Override
    @TransactionalRead
    public Hub getHub(long hubId) {
        List<Hub> results = findAll(new BooleanBuilder(qHub.id.eq(hubId)));
        if (results.isEmpty()) {
            throw new HubNotFoundException(hubId);
        }
        return results.get(0);
    }

    private List<Hub> findAll(BooleanBuilder where) {
        PostgresQuery qry = queryFactory.from(qHub)
                .leftJoin(qHub._hubFacilityHubIdFk, qHubFacility);

        if (where.hasValue()) {
            qry.where(where);
        }

        return qry.transform(GroupBy.groupBy(qHub.id).list(hubMapping));
    }

    @Override
    @TransactionalRead
    public SearchResults<Hub> findHubs(SpatialSearch search) {
        BooleanBuilder where = new BooleanBuilder();
        if (search.intersecting != null) {
            where.and(qHub.location.intersects(search.intersecting));
        }
        return SearchResults.of(findAll(where));
    }
}
