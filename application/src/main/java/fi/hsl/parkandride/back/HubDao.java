package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.mysema.query.spatial.GeometryExpressions.dwithin;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.group.GroupBy;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QHub;
import fi.hsl.parkandride.back.sql.QHubFacility;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

public class HubDao implements HubRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    public static final String HUB_ID_SEQ = "hub_id_seq";

    private static final SimpleExpression<Long> nextHubId = SQLExpressions.nextval(HUB_ID_SEQ);

    private static final QHub qHub = QHub.hub;

    private static final QHubFacility qHubFacility = QHubFacility.hubFacility;

    private static final Expression<Set<Long>> facilityIdsMapping = GroupBy.set(qHubFacility.facilityId);

    private static final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qHub.nameFi, qHub.nameSv, qHub.nameEn);

    private static final AddressMapping addressMapping = new AddressMapping(qHub);

    private static final MappingProjection<Hub> hubMapping = new MappingProjection<Hub>(Hub.class, qHub.all()) {

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
            hub.address = addressMapping.map(row);
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
        Hub hub = queryFactory.from(qHub).where(qHub.id.eq(hubId)).singleResult(hubMapping);
        if (hub == null) {
            throw new HubNotFoundException(hubId);
        }
        fetchFacilityIds(ImmutableMap.of(hubId, hub));
        return hub;
    }

    @Override
    @TransactionalRead
    public SearchResults<Hub> findHubs(HubSearch search) {
        PostgresQuery qry = queryFactory.from(qHub);
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        buildWhere(search, qry);

        orderBy(search.sort, qry);

        Map<Long, Hub> hubs = qry.map(qHub.id, hubMapping);

        fetchFacilityIds(hubs);

        return SearchResults.of(hubs.values(), search.limit);
    }

    private void buildWhere(HubSearch search, PostgresQuery qry) {
        if (search.geometry != null) {
            if (search.maxDistance != null && search.maxDistance > 0) {
                qry.where(dwithin(qHub.location, ConstantImpl.create(search.geometry), search.maxDistance));
            } else {
                qry.where(qHub.location.intersects(search.geometry));
            }
        }
        if (search.ids != null && !search.ids.isEmpty()) {
            qry.where(qHub.id.in(search.ids));
        }
        if (search.facilityIds != null && !search.facilityIds.isEmpty()) {
            SQLSubQuery hasFacilityId = queryFactory
                    .subQuery(qHubFacility)
                    .where(qHubFacility.hubId.eq(qHub.id), qHubFacility.facilityId.in(search.facilityIds));
            qry.where(hasFacilityId.exists());
        }
    }

    private void fetchFacilityIds(Map<Long, Hub> hubs) {
        if (!hubs.isEmpty()) {
            PostgresQuery qry = queryFactory.from(qHubFacility);
            qry.where(qHubFacility.hubId.in(hubs.keySet()));
            Map<Long, Set<Long>> hubFacilityIds = qry.transform(GroupBy.groupBy(qHubFacility.hubId).as(facilityIdsMapping));
            for (Map.Entry<Long, Set<Long>> entry : hubFacilityIds.entrySet()) {
                hubs.get(entry.getKey()).facilityIds = entry.getValue();
            }
        }
    }

    private void populate(Hub hub, StoreClause store) {
        store.set(qHub.location, hub.location);
        nameMapping.populate(hub.name, store);
        addressMapping.populate(hub.address, store);
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

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qHub.nameFi.toUpperCase(); break;
            case "name.sv": sortField = qHub.nameSv.toUpperCase(); break;
            case "name.en": sortField = qHub.nameEn.toUpperCase(); break;
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
