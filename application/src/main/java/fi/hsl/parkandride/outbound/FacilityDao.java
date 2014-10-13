package fi.hsl.parkandride.outbound;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.group.GroupBy.groupBy;
import static com.mysema.query.group.GroupBy.map;
import static com.mysema.query.group.GroupBy.set;
import static java.lang.String.format;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mysema.query.ResultTransformer;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.group.AbstractGroupExpression;
import com.mysema.query.group.GroupBy;
import com.mysema.query.group.GroupCollector;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.QBean;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.sql.QCapacity;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;

public class FacilityDao implements FacilityRepository {

    private static final QFacility qFacility = QFacility.facility;

    private static final QFacilityAlias qAlias = QFacilityAlias.facilityAlias;

    private static final QCapacity qCapacity = QCapacity.capacity;

    private static final MappingProjection<Capacity> capacityMapping = new MappingProjection<Capacity>(Capacity.class, qCapacity.built, qCapacity.unavailable) {
        @Override
        protected Capacity map(Tuple row) {
            Integer built = row.get(qCapacity.built);
            if (built == null) {
                return null;
            }
            return new Capacity(built, row.get(qCapacity.unavailable));
        }
    };

    private static final NumberExpression<Integer> capacityBuiltSum = qCapacity.built.sum();

    private static final NumberExpression<Integer> capacityUnavailableSum = qCapacity.unavailable.sum();

    private static final MappingProjection<Capacity> capacitySummaryMapping = new MappingProjection<Capacity>(Capacity.class, capacityBuiltSum,
            capacityUnavailableSum) {
        @Override
        protected Capacity map(Tuple row) {
            Integer built = row.get(capacityBuiltSum);
            if (built == null) {
                return null;
            }
            return new Capacity(built, row.get(capacityUnavailableSum));
        }
    };

    public static final ResultTransformer<Map<Long, Set<String>>> aliasesByFacilityIdMapping =
            groupBy(qAlias.facilityId).as(set(qAlias.alias));

    public static final ResultTransformer<Map<Long, Map<CapacityType, Capacity>>> capacitiesByFacilityIdMapping =
            groupBy(qCapacity.facilityId).as(map(qCapacity.capacityType, capacityMapping));

    private static final QBean<Facility> facilityMapping = new QBean<>(Facility.class, true, qFacility.all());

    private static final SimpleExpression<Long> nextFacilityId = SQLExpressions.nextval("facility_id_seq");


    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertFacility(Facility facility) {
        long facilityId = queryFactory.query().singleResult(nextFacilityId);

        SQLInsertClause insert = insertFacility();
        insert.set(qFacility.id, facilityId);
        populate(facility, insert);
        insert.execute();

        insertAliases(facilityId, facility.aliases);
        insertCapacities(facilityId, facility.capacities);

        return facilityId;
    }

    @TransactionalWrite
    @Override
    public void updateFacility(long facilityId, Facility facility) {
        updateFacility(facilityId, facility, getFacility(facility.id, true));
    }

    @TransactionalWrite
    @Override
    public void updateFacility(long facilityId, Facility newFacility, Facility oldFacility) {
        checkNotNull(newFacility, "facility");
        SQLUpdateClause update = updateFacility().where(qFacility.id.eq(facilityId));
        populate(newFacility, update);
        if (update.execute() != 1) {
            throw new FacilityNotFoundException(facilityId);
        }

        updateAliases(facilityId, newFacility.aliases, oldFacility.aliases);
        updateCapacities(facilityId, newFacility.capacities, oldFacility.capacities);
    }

    private void updateCapacities(long facilityId, Map<CapacityType, Capacity> newCapacities, Map<CapacityType, Capacity> oldCapacities) {
        Map<CapacityType, Capacity> toBeRemoved = new HashMap<>(oldCapacities);
        Map<CapacityType, Capacity> addedCapacities = new HashMap<>();
        Map<CapacityType, Capacity> updatedCapacities = new HashMap<>();

        if (newCapacities != null) {
            for (Map.Entry<CapacityType, Capacity> entry : newCapacities.entrySet()) {
                CapacityType type = entry.getKey();
                Capacity newCapacity = entry.getValue();
                Capacity oldCapacity = toBeRemoved.remove(type);
                if (oldCapacity == null) {
                    addedCapacities.put(type, newCapacity);
                } else if (!newCapacity.equals(oldCapacity)) {
                    updatedCapacities.put(type, newCapacity);
                }
            }
        }

        insertCapacities(facilityId, addedCapacities);
        updateCapacities(facilityId, updatedCapacities);
        deleteCapacities(facilityId, toBeRemoved.keySet());
    }

    private void updateCapacities(long facilityId, Map<CapacityType, Capacity> updatedCapacities) {
        for (Map.Entry<CapacityType, Capacity> entry : updatedCapacities.entrySet()) {
            Capacity capacity = entry.getValue();
            SQLUpdateClause update = queryFactory.update(qCapacity)
                    .where(qCapacity.facilityId.eq(facilityId), qCapacity.capacityType.eq(entry.getKey()));
            update.set(qCapacity.built, capacity.built);
            update.set(qCapacity.unavailable, capacity.unavailable);
            update.execute();
        }
    }

    private void deleteCapacities(long facilityId, Set<CapacityType> deletedCapacities) {
        if (!deletedCapacities.isEmpty()) {
            queryFactory.delete(qCapacity)
                    .where(qCapacity.facilityId.eq(facilityId), qCapacity.capacityType.in(deletedCapacities))
                    .execute();
        }
    }

    private void updateAliases(long facilityId, Set<String> newAliases, Set<String> oldAliases) {
        Set<String> toBeRemoved = new HashSet<>(oldAliases);
        Set<String> addedAliases = Sets.newHashSet();
        if (newAliases != null) {
            for (String newAlias : newAliases) {
                if (!toBeRemoved.remove(newAlias)) {
                    addedAliases.add(newAlias);
                }
            }
        }
        insertAliases(facilityId, addedAliases);
        deleteAliases(facilityId, toBeRemoved);
    }

    private void deleteAliases(long facilityId, Set<String> aliases) {
        if (!aliases.isEmpty()) {
            queryFactory.delete(qAlias)
                    .where(qAlias.facilityId.eq(facilityId), qAlias.alias.in(aliases))
                    .execute();
        }
    }

    @TransactionalRead
    @Override
    public Facility getFacility(long id) {
        return getFacility(id, false);
    }

    @TransactionalWrite
    @Override
    public Facility getFacilityForUpdate(long facilityId) {
        return getFacility(facilityId, true);
    }

    private Facility getFacility(long facilityId, boolean forUpdate) {
        PostgresQuery qry = fromFacility().where(qFacility.id.eq(facilityId));
        if (forUpdate) {
            qry.forUpdate();
        }
        Facility facility = qry.singleResult(facilityMapping);
        if (facility == null) {
            throw new FacilityNotFoundException(facilityId);
        }
        ImmutableMap<Long, Facility> facilityMap = ImmutableMap.of(facilityId, facility);
        fetchAliases(facilityMap);
        fetchCapacities(facilityMap);
        return facility;
    }

    @TransactionalRead
    @Override
    public SearchResults<Facility> findFacilities(PageableSpatialSearch search) {
        PostgresQuery qry = fromFacility();
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        buildWhere(search, qry);

        Map<Long, Facility> facilities = qry.map(qFacility.id, facilityMapping);
        fetchAliases(facilities);
        fetchCapacities(facilities);

        return SearchResults.of(new ArrayList<>(facilities.values()), search.limit);
    }

    @TransactionalRead
    @Override
    public FacilitySummary summarizeFacilities(SpatialSearch search) {
        PostgresQuery qry = fromFacility();

        buildWhere(search, qry);

        long count = qry.singleResult(SQLExpressions.countAll);

        qry.leftJoin(qFacility._capacityFacilityIdFk, qCapacity);
        qry.groupBy(qCapacity.capacityType);

        Map<CapacityType, Capacity> capacities = qry.map(qCapacity.capacityType, capacitySummaryMapping);

        return new FacilitySummary(count, capacities);
    }

    private void buildWhere(SpatialSearch search, PostgresQuery qry) {
        if (search.intersecting != null) {
            qry.where(qFacility.border.intersects(search.intersecting));
        }

        if (search.ids != null && !search.ids.isEmpty()) {
            qry.where(qFacility.id.in(search.ids));
        }
    }

    private void insertAliases(long facilityId, Collection<String> aliases) {
        if (aliases != null && !aliases.isEmpty()) {
            SQLInsertClause insertBatch = queryFactory.insert(qAlias);
            for (String alias : aliases) {
                insertBatch.set(qAlias.facilityId, facilityId);
                insertBatch.set(qAlias.alias, alias);
                insertBatch.addBatch();
            }
            insertBatch.execute();
        }
    }

    private void insertCapacities(long facilityId, Map<CapacityType, Capacity> capacities) {
        if (capacities != null && !capacities.isEmpty()) {
            SQLInsertClause insertBatch = queryFactory.insert(qCapacity);
            for (Map.Entry<CapacityType, Capacity> entry : capacities.entrySet()) {
                Capacity capacity = entry.getValue();
                insertBatch.set(qCapacity.facilityId, facilityId);
                insertBatch.set(qCapacity.capacityType, entry.getKey());
                insertBatch.set(qCapacity.built, capacity.built);
                insertBatch.set(qCapacity.unavailable, capacity.unavailable);
                insertBatch.addBatch();
            }
            insertBatch.execute();
        }
    }

    private Map<Long, Facility> fetchAliases(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Set<String>> aliasesByFacilityId = findAliases(facilitiesById.keySet());

            for (Map.Entry<Long, Set<String>> entry : aliasesByFacilityId.entrySet()) {
                facilitiesById.get(entry.getKey()).aliases = new TreeSet<>(entry.getValue());
            }
        }
        return facilitiesById;
    }

    private Map<Long, Facility> fetchCapacities(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Map<CapacityType, Capacity>> capacities = findCapacities(facilitiesById.keySet());

            for (Map.Entry<Long, Map<CapacityType, Capacity>> entry : capacities.entrySet()) {
                facilitiesById.get(entry.getKey()).capacities = entry.getValue();
            }
        }
        return facilitiesById;
    }

    private Map<Long, Set<String>> findAliases(Set<Long> facilitiesById) {
        return queryFactory.from(qAlias)
                .where(qAlias.facilityId.in(facilitiesById))
                .transform(aliasesByFacilityIdMapping);
    }

    private Map<Long, Map<CapacityType, Capacity>> findCapacities(Set<Long> facilitiesById) {
        return queryFactory.from(qCapacity)
                .where(qCapacity.facilityId.in(facilitiesById))
                .transform(capacitiesByFacilityIdMapping);
    }

    private void populate(Facility facility, StoreClause store) {
        store.set(qFacility.name, facility.name);
        store.set(qFacility.border, facility.border);
    }

    private SQLInsertClause insertFacility() {
        return queryFactory.insert(qFacility);
    }

    private SQLUpdateClause updateFacility() {
        return queryFactory.update(qFacility);
    }

    private PostgresQuery fromFacility() {
        return queryFactory.from(qFacility);
    }

}
