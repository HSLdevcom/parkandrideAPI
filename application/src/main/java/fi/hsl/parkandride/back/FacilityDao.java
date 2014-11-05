package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.group.GroupBy.groupBy;
import static com.mysema.query.group.GroupBy.list;
import static com.mysema.query.group.GroupBy.map;
import static com.mysema.query.group.GroupBy.set;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import java.util.*;

import org.geolatte.geom.Geometry;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mysema.query.ResultTransformer;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QGate;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.back.sql.QCapacity;
import fi.hsl.parkandride.back.sql.QFacility;
import fi.hsl.parkandride.back.sql.QFacilityAlias;

public class FacilityDao implements FacilityRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    private static final QFacility qFacility = QFacility.facility;

    private static final QFacilityAlias qAlias = QFacilityAlias.facilityAlias;

    private static final QCapacity qCapacity = QCapacity.capacity;

    private static final QGate qGate = QGate.gate;

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

    private static final MappingProjection<Gate> gateMapping = new MappingProjection<Gate>(Gate.class, qGate.all()) {
        @Override
        protected Gate map(Tuple row) {
            Boolean entry = row.get(qGate.entry);
            if (entry == null) {
                return null;
            }
            Geometry location = row.get(qGate.location);
            boolean exit = row.get(qGate.exit);
            boolean pedestrian = row.get(qGate.pedestrian);
            return new Gate(location, entry, exit, pedestrian);
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

    public static final ResultTransformer<Map<Long, List<Gate>>> gatesByFacilityIdMapping =
            groupBy(qGate.facilityId).as(list(gateMapping));

    private static final MappingProjection<Facility> facilityMapping = new MappingProjection<Facility>(Facility.class, qFacility.all()) {
        private final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qFacility.nameFi, qFacility.nameSv, qFacility.nameEn);
        @Override
        protected Facility map(Tuple row) {
            Long id = row.get(qFacility.id);
            if (id == null) {
                return null;
            }
            Facility facility = new Facility();
            facility.id = id;
            facility.location = row.get(qFacility.location);
            facility.name = nameMapping.map(row);
            return facility;
        }
    };

    public static final String FACILITY_ID_SEQ = "facility_id_seq";

    private static final SimpleExpression<Long> nextFacilityId = SQLExpressions.nextval(FACILITY_ID_SEQ);

    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertFacility(Facility facility) {
        return insertFacility(facility, queryFactory.query().singleResult(nextFacilityId));
    }

    @TransactionalWrite
    public long insertFacility(Facility facility, long facilityId) {
        SQLInsertClause insert = insertFacility();
        insert.set(qFacility.id, facilityId);
        populate(facility, insert);
        insert.execute();

        insertAliases(facilityId, facility.aliases);
        insertCapacities(facilityId, facility.capacities);
        insertGates(facilityId, facility.gates);

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
        updateGates(facilityId, newFacility.gates, oldFacility.gates);
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
        fetchGates(facilityMap);
        return facility;
    }

    @TransactionalRead
    @Override
    public SearchResults<Facility> findFacilities(PageableSpatialSearch search) {
        PostgresQuery qry = fromFacility();
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        buildWhere(search, qry);
        orderBy(search.sort, qry);

        Map<Long, Facility> facilities = qry.map(qFacility.id, facilityMapping);
        fetchAliases(facilities);
        fetchCapacities(facilities);
        fetchGates(facilities);

        return SearchResults.of(new ArrayList<>(facilities.values()), search.limit);
    }

    @TransactionalRead
    @Override
    public FacilitySummary summarizeFacilities(SpatialSearch search) {
        PostgresQuery qry = fromFacility();

        buildWhere(search, qry);

        long count = qry.singleResult(SQLExpressions.countAll);

        qry.innerJoin(qFacility._capacityFacilityIdFk, qCapacity);
        qry.groupBy(qCapacity.capacityType);

        Map<CapacityType, Capacity> capacities = qry.map(qCapacity.capacityType, capacitySummaryMapping);

        return new FacilitySummary(count, capacities);
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
        if (!updatedCapacities.isEmpty()) {
            SQLUpdateClause update = queryFactory.update(qCapacity);
            for (Map.Entry<CapacityType, Capacity> entry : updatedCapacities.entrySet()) {
                Capacity capacity = entry.getValue();
                update.where(qCapacity.facilityId.eq(facilityId), qCapacity.capacityType.eq(entry.getKey()));
                update.set(qCapacity.built, capacity.built);
                update.set(qCapacity.unavailable, capacity.unavailable);
                update.addBatch();
            }
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

    private void updateGates(long facilityId, List<Gate> newGates, List<Gate> oldGates) {
        newGates = firstNonNull(newGates, new ArrayList<Gate>());
        oldGates = firstNonNull(oldGates, new ArrayList<Gate>());

        Map<Integer, Gate> addedGates = new HashMap<>();
        Map<Integer, Gate> updatedGates = new HashMap<>();

        for (int i=0; i < newGates.size(); i++) {
            Gate newGate = newGates.get(i);
            Gate oldGate = i < oldGates.size() ? oldGates.get(i) : null;
            if (oldGate == null) {
                addedGates.put(i, newGate);
            } else if (!newGate.equals(oldGate)) {
                updatedGates.put(i, newGate);
            }
        }

        insertGates(facilityId, addedGates);
        updateGates(facilityId, updatedGates);
        if (oldGates.size() > newGates.size()) {
            deleteGates(facilityId, newGates.size());
        }
    }

    private void updateGates(long facilityId, Map<Integer, Gate> updatedGates) {
        if (updatedGates != null && !updatedGates.isEmpty()) {
            SQLUpdateClause update = queryFactory.update(qGate);
            for (Map.Entry<Integer, Gate> entry : updatedGates.entrySet()) {
                Integer gateIndex = entry.getKey();
                populate(facilityId, gateIndex, entry.getValue(), update);
                update.where(qGate.facilityId.eq(facilityId), qGate.gateIndex.eq(gateIndex));
                update.addBatch();
            }
            update.execute();
        }
    }

    private void insertGates(long facilityId, List<Gate> gates) {
        if (gates != null && !gates.isEmpty()) {
            Map<Integer, Gate> addedGates = new HashMap<>();
            for (int i = 0; i < gates.size(); i++) {
                addedGates.put(i, gates.get(i));
            }
            insertGates(facilityId, addedGates);
        }
    }

    private void insertGates(long facilityId, Map<Integer, Gate> addedGates) {
        if (addedGates != null && !addedGates.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qGate);
            for (Map.Entry<Integer, Gate> entry : addedGates.entrySet()) {
                populate(facilityId, entry.getKey(), entry.getValue(), insert);
                insert.addBatch();
            }
            insert.execute();
        }
    }

    private void populate(long facilityId, Integer index, Gate gate, StoreClause update) {
        update.set(qGate.facilityId, facilityId)
                .set(qGate.gateIndex, index)
                .set(qGate.location, gate.location)
                .set(qGate.entry, gate.entry)
                .set(qGate.exit, gate.exit)
                .set(qGate.pedestrian, gate.pedestrian);
    }

    private void deleteGates(long facilityId, int fromIndex) {
        queryFactory.delete(qGate).where(qGate.facilityId.eq(facilityId), qGate.gateIndex.goe(fromIndex)).execute();
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qFacility.nameFi.toUpperCase(); break;
            case "name.sv": sortField = qFacility.nameSv.toUpperCase(); break;
            case "name.en": sortField = qFacility.nameEn.toUpperCase(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qFacility.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qFacility.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

    private void buildWhere(SpatialSearch search, PostgresQuery qry) {
        if (search.intersecting != null) {
            qry.where(qFacility.location.intersects(search.intersecting));
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

    private Map<Long, Facility> fetchGates(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, List<Gate>> gates = findGates(facilitiesById.keySet());

            for (Map.Entry<Long, List<Gate>> entry : gates.entrySet()) {
                facilitiesById.get(entry.getKey()).gates = entry.getValue();
            }
        }
        return facilitiesById;
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

    private Map<Long, List<Gate>> findGates(Set<Long> facilitiesById) {
        return queryFactory.from(qGate)
                .where(qGate.facilityId.in(facilitiesById))
                .transform(gatesByFacilityIdMapping);
    }

    private void populate(Facility facility, StoreClause store) {
        store.set(qFacility.nameFi, facility.name.fi);
        store.set(qFacility.nameSv, facility.name.sv);
        store.set(qFacility.nameEn, facility.name.en);
        store.set(qFacility.location, facility.location);
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
