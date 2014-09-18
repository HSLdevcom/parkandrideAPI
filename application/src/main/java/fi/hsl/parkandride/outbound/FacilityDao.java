package fi.hsl.parkandride.outbound;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.group.GroupBy.groupBy;
import static com.mysema.query.group.GroupBy.map;
import static com.mysema.query.group.GroupBy.set;
import static java.lang.String.format;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mysema.query.ResultTransformer;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.QBean;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.core.domain.Capacity;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.outbound.FacilitySearch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.outbound.sql.QCapacity;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;

public class FacilityDao implements FacilityRepository {

    private static final QFacility qFacility = QFacility.facility;

    private static final QFacilityAlias qAlias = QFacilityAlias.facilityAlias;

    private static final QCapacity qCapacity = QCapacity.capacity;

    private static MappingProjection<Capacity> capacityMapping = new MappingProjection<Capacity>(Capacity.class, qCapacity.built, qCapacity.unavailable) {
        @Override
        protected Capacity map(Tuple row) {
            Integer built = row.get(qCapacity.built);
            if (built == null) {
                return null;
            }
            return new Capacity(built, row.get(qCapacity.unavailable));
        }
    };

    public static final ResultTransformer<Map<Long, Set<String>>> aliasesByFacilityIdMapping =
            groupBy(qAlias.facilityId).as(set(qAlias.alias));

    public static final ResultTransformer<Map<Long, Map<String, Capacity>>> capacitiesByFacilityIdMapping =
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
        SQLInsertClause insert = insertFacility();
        insert.set(qFacility.id, nextFacilityId);
        populate(facility, insert);
        long id = insert.executeWithKey(qFacility.id);
        facility.id = id;
        insertAliases(id, facility.aliases);
        insertCapacities(id, facility.capacities);
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

        updateAliases(facility.id, facility.aliases);
    }

    private void updateAliases(Long facilityId, Set<String> newAliases) {
        Set<String> oldAliases = findAliases(facilityId);
        Set<String> addedAliases = Sets.newHashSet();
        for (String newAlias : newAliases) {
            if (!oldAliases.remove(newAlias)) {
                addedAliases.add(newAlias);
            }
        }
        insertAliases(facilityId, addedAliases);
        deleteAliases(facilityId, oldAliases);
    }

    private void deleteAliases(Long facilityId, Set<String> aliases) {
        if (!aliases.isEmpty()) {
            queryFactory.delete(qAlias)
                    .where(qAlias.facilityId.eq(facilityId), qAlias.alias.in(aliases))
                    .execute();
        }
    }

    @TransactionalRead
    @Override
    public Facility getFacility(long id) {
        Facility facility = query().where(qFacility.id.eq(id)).singleResult(facilityMapping);
        if (facility == null) {
            throw new IllegalArgumentException(format("Facility#%s not found", id));
        }
        ImmutableMap<Long, Facility> facilityMap = ImmutableMap.of(id, facility);
        fetchAliases(facilityMap);
        fetchCapacities(facilityMap);
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

        Map<Long, Facility> facilities = qry.map(qFacility.id, facilityMapping);
        fetchAliases(facilities);
        fetchCapacities(facilities);

        return new ArrayList<>(facilities.values());
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

    private void insertCapacities(long id, Map<String, Capacity> capacities) {
        if (capacities != null && !capacities.isEmpty()) {
            SQLInsertClause insertBatch = queryFactory.insert(qCapacity);
            for (Map.Entry<String, Capacity> entry : capacities.entrySet()) {
                Capacity capacity = entry.getValue();
                insertBatch.set(qCapacity.facilityId, id);
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
            Map<Long, Map<String, Capacity>> capacities = findCapacities(facilitiesById.keySet());

            for (Map.Entry<Long, Map<String, Capacity>> entry : capacities.entrySet()) {
                facilitiesById.get(entry.getKey()).capacities = entry.getValue();
            }
        }
        return facilitiesById;
    }

    private Set<String> findAliases(Long facilityId) {
        Set<String> aliases = findAliases(ImmutableSet.of(facilityId)).get(facilityId);
        return aliases != null ? aliases : Sets.newHashSet();
    }

    private Map<Long, Set<String>> findAliases(Set<Long> facilitiesById) {
        return queryFactory.from(qAlias)
                .where(qAlias.facilityId.in(facilitiesById))
                .transform(aliasesByFacilityIdMapping);
    }

    private Map<Long, Map<String, Capacity>> findCapacities(Set<Long> facilitiesById) {
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

    private SQLUpdateClause update() {
        return queryFactory.update(qFacility);
    }

    private PostgresQuery query() {
        return queryFactory.from(qFacility);
    }

}
