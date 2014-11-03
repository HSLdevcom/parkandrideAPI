package fi.hsl.parkandride.outbound;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.mysema.query.types.Projections.fields;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import java.util.List;
import java.util.Set;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.group.GroupBy;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.QBean;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.DslExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.outbound.HubRepository;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.outbound.sql.QHub;
import fi.hsl.parkandride.outbound.sql.QHubFacility;

public class HubDao implements HubRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    public static final String HUB_ID_SEQ = "hub_id_seq";

    private static final SimpleExpression<Long> nextHubId = SQLExpressions.nextval(HUB_ID_SEQ);

    private static final QHub qHub = QHub.hub;

    private static final QHubFacility qHubFacility = QHubFacility.hubFacility;

    private static final Expression<Set<Long>> facilityIdsMapping = GroupBy.set(qHubFacility.facilityId);

    private static final MappingProjection<Hub> hubMapping = new MappingProjection<Hub>(Hub.class, qHub.id, qHub.location, qHub.nameFi, qHub.nameSv,
            qHub.nameEn, facilityIdsMapping) {
        private final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qHub.nameFi, qHub.nameSv, qHub.nameEn);
        @Override
        protected Hub map(Tuple row) {
            Long id = row.get(qHub.id);
            if (id == null) {
                return null;
            }
            Hub hub = new Hub();
            hub.id = id;
            hub.location = row.get(qHub.location);
            hub.name = nameMapping.map(row);
            hub.facilityIds = row.get(facilityIdsMapping);
            return hub;
        }
    };


    private final PostgresQueryFactory queryFactory;

    public HubDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalWrite
    public long insertHub(Hub hub) {
        return insertHub(hub, queryFactory.query().singleResult(nextHubId));
    }

    @TransactionalWrite
    public long insertHub(Hub hub, long hubId) {
        SQLInsertClause insert = queryFactory.insert(qHub);
        insert.set(qHub.id, hubId);
        populate(hub, insert);
        insert.execute();

        insertHubFacilities(hubId, hub.facilityIds);
        return hubId;
    }

    private void populate(Hub hub, StoreClause store) {
        store.set(qHub.nameFi, hub.name.fi);
        store.set(qHub.nameSv, hub.name.sv);
        store.set(qHub.nameEn, hub.name.en);
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
        List<Hub> results = findAll(new BooleanBuilder(qHub.id.eq(hubId)), null);
        if (results.isEmpty()) {
            throw new HubNotFoundException(hubId);
        }
        return results.get(0);
    }

    @Override
    @TransactionalRead
    public SearchResults<Hub> findHubs(SpatialSearch search) {
        BooleanBuilder where = new BooleanBuilder();
        if (search.intersecting != null) {
            where.and(qHub.location.intersects(search.intersecting));
        }
        return SearchResults.of(findAll(where, search.sort));
    }

    private List<Hub> findAll(BooleanBuilder where, Sort sort) {
        PostgresQuery qry = queryFactory.from(qHub)
                .leftJoin(qHub._hubFacilityHubIdFk, qHubFacility);

        if (where.hasValue()) {
            qry.where(where);
        }
        orderBy(sort, qry);

        return qry.transform(GroupBy.groupBy(qHub.id).list(hubMapping));
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qHub.nameFi; break;
            case "name.sv": sortField = qHub.nameSv; break;
            case "name.en": sortField = qHub.nameEn; break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qHub.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qHub.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }
}
