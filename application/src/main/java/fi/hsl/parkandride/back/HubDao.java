// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.querydsl.spatial.GeometryExpressions.dwithin;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.StoreClause;
import com.mysema.query.group.GroupBy;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLSubQuery;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

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


    private final PostgreSQLQueryFactory queryFactory;

    public HubDao(PostgreSQLQueryFactory queryFactory) {
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
        PostgreSQLQuery qry = queryFactory.from(qHub);
        qry.limit(search.getLimit() + 1);
        qry.offset(search.getOffset());

        buildWhere(search, qry);

        orderBy(search.getSort(), qry);

        Map<Long, Hub> hubs = qry.map(qHub.id, hubMapping);

        fetchFacilityIds(hubs);

        return SearchResults.of(hubs.values(), search.getLimit());
    }

    private void buildWhere(HubSearch search, PostgreSQLQuery qry) {
        if (search.getGeometry() != null) {
            if (search.getMaxDistance() != null && search.getMaxDistance() > 0) {
                qry.where(dwithin(qHub.location, ConstantImpl.create(search.getGeometry()), search.getMaxDistance()));
            } else {
                qry.where(qHub.location.intersects(search.getGeometry()));
            }
        }
        if (search.getIds() != null && !search.getIds().isEmpty()) {
            qry.where(qHub.id.in(search.getIds()));
        }
        if (search.getFacilityIds() != null && !search.getFacilityIds().isEmpty()) {
            SQLSubQuery hasFacilityId = queryFactory
                    .subQuery(qHubFacility)
                    .where(qHubFacility.hubId.eq(qHub.id), qHubFacility.facilityId.in(search.getFacilityIds()));
            qry.where(hasFacilityId.exists());
        }
    }

    private void fetchFacilityIds(Map<Long, Hub> hubs) {
        if (!hubs.isEmpty()) {
            PostgreSQLQuery qry = queryFactory.from(qHubFacility);
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

    private void orderBy(Sort sort, PostgreSQLQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.getBy(), DEFAULT_SORT.getBy())) {
            case "name.fi": sortField = qHub.nameFi.lower(); break;
            case "name.sv": sortField = qHub.nameSv.lower(); break;
            case "name.en": sortField = qHub.nameEn.lower(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.getDir())) {
            qry.orderBy(sortField.desc(), qHub.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qHub.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }
}
