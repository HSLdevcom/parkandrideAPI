// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.back.sql.QFacilityCapacityHistory;
import fi.hsl.parkandride.back.sql.QFacilityStatusHistory;
import fi.hsl.parkandride.back.sql.QUnavailableCapacityHistory;
import fi.hsl.parkandride.core.back.FacilityHistoryRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class FacilityHistoryDao implements FacilityHistoryRepository {

    public static final String STATUS_HISTORY_ID_SEQ = "facility_status_history_seq";
    public static final String CAPACITY_HISTORY_ID_SEQ = "facility_capacity_history_seq";

    private static final QFacilityStatusHistory qFacilityStatusHistory = QFacilityStatusHistory.facilityStatusHistory;
    private static final QFacilityCapacityHistory qFacilityCapacityHistory = QFacilityCapacityHistory.facilityCapacityHistory;
    private static final QUnavailableCapacityHistory qUnavailableCapacityHistory = QUnavailableCapacityHistory.unavailableCapacityHistory;
    private static final MappingProjection<UnavailableCapacity> unavailableCapacityHistoryMapping = new MappingProjection<UnavailableCapacity>(UnavailableCapacity.class, qUnavailableCapacityHistory.all()) {
        @Override
        protected UnavailableCapacity map(Tuple row) {
            final UnavailableCapacity uc = new UnavailableCapacity();
            uc.capacityType = row.get(qUnavailableCapacityHistory.capacityType);
            uc.usage = row.get(qUnavailableCapacityHistory.usage);
            uc.capacity = row.get(qUnavailableCapacityHistory.capacity);
            return uc;
        }
    };

    private static final Expression<Long> nextStatusHistoryId = SQLExpressions.nextval(STATUS_HISTORY_ID_SEQ);
    private static final Expression<Long> nextCapacityHistoryId = SQLExpressions.nextval(CAPACITY_HISTORY_ID_SEQ);
    private static final MultilingualStringMapping statusHistoryDescriptionMapping = new MultilingualStringMapping(
            qFacilityStatusHistory.statusDescriptionFi,
            qFacilityStatusHistory.statusDescriptionSv,
            qFacilityStatusHistory.statusDescriptionEn
    );

    private final PostgreSQLQueryFactory queryFactory;

    public FacilityHistoryDao(PostgreSQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    private static void populateCapacity(NumberPath<Integer> path, Integer value, StoreClause store) {
        if (value == null || value < 1) {
            store.setNull(path);
        } else {
            store.set(path, value);
        }
    }

    private static void mapCapacity(Map<CapacityType, Integer> capacities, CapacityType type, Integer capacity) {
        if (capacity != null && capacity > 0) {
            capacities.put(type, capacity);
        }
    }

    @Override
    @TransactionalWrite
    public void updateCapacityHistory(DateTime currentDate, long facilityId, Map<CapacityType, Integer> builtCapacity, List<UnavailableCapacity> unavailableCapacities) {
        setEndDateForPreviousCapacityHistoryEntry(facilityId, currentDate);
        final long historyEntryId = insertNewCapacityHistoryEntry(facilityId, currentDate, builtCapacity);
        insertUnavailableCapacitiesHistory(historyEntryId, unavailableCapacities);
    }

    private void insertUnavailableCapacitiesHistory(long historyEntryId, List<UnavailableCapacity> unavailableCapacities) {
        if (unavailableCapacities.isEmpty()) {
            return;
        }
        final SQLInsertClause insert = queryFactory.insert(qUnavailableCapacityHistory);
        unavailableCapacities.forEach(uc -> {
            insert.set(qUnavailableCapacityHistory.capacityHistoryId, historyEntryId)
                    .set(qUnavailableCapacityHistory.capacityType, uc.capacityType)
                    .set(qUnavailableCapacityHistory.usage, uc.usage)
                    .set(qUnavailableCapacityHistory.capacity, uc.capacity);
            insert.addBatch();
        });
        insert.execute();
    }

    @Override
    @TransactionalWrite
    public void updateStatusHistory(DateTime currentDate, long facilityId, FacilityStatus newStatus, MultilingualString statusDescription) {
        setEndDateForPreviousStateHistoryEntry(facilityId, currentDate);
        insertNewStatusHistoryEntry(facilityId, currentDate, newStatus, statusDescription);
    }

    private long insertNewStatusHistoryEntry(long facilityId, DateTime currentDate, FacilityStatus newStatus, MultilingualString statusDescription) {
        final SQLInsertClause insert = queryFactory.insert(qFacilityStatusHistory);
        statusHistoryDescriptionMapping.populate(statusDescription, insert);
        return insert
                .set(qFacilityStatusHistory.id, select(nextStatusHistoryId))
                .set(qFacilityStatusHistory.facilityId, facilityId)
                .set(qFacilityStatusHistory.status, newStatus)
                .set(qFacilityStatusHistory.startTs, currentDate)
                .execute();
    }

    private long insertNewCapacityHistoryEntry(long facilityId, DateTime currentDate, Map<CapacityType, Integer> builtCapacity) {
        final Long historyEntryId = queryFactory.query().select(nextCapacityHistoryId).fetchOne();
        final SQLInsertClause insert = queryFactory.insert(qFacilityCapacityHistory);
        populateCapacity(qFacilityCapacityHistory.capacityCar, builtCapacity.get(CAR), insert);
        populateCapacity(qFacilityCapacityHistory.capacityDisabled, builtCapacity.get(DISABLED), insert);
        populateCapacity(qFacilityCapacityHistory.capacityElectricCar, builtCapacity.get(ELECTRIC_CAR), insert);
        populateCapacity(qFacilityCapacityHistory.capacityMotorcycle, builtCapacity.get(MOTORCYCLE), insert);
        populateCapacity(qFacilityCapacityHistory.capacityBicycle, builtCapacity.get(BICYCLE), insert);
        populateCapacity(qFacilityCapacityHistory.capacityBicycleSecureSpace, builtCapacity.get(BICYCLE_SECURE_SPACE), insert);
        insert.set(qFacilityCapacityHistory.id, historyEntryId)
                .set(qFacilityCapacityHistory.facilityId, facilityId)
                .set(qFacilityCapacityHistory.startTs, currentDate)
                .execute();
        return historyEntryId;
    }

    private void setEndDateForPreviousStateHistoryEntry(long facilityId, DateTime currentDate) {
        final Long lastHistoryEntryId = queryFactory.query().select(qFacilityStatusHistory.id)
                .from(qFacilityStatusHistory)
                .where(qFacilityStatusHistory.facilityId.eq(facilityId))
                .orderBy(qFacilityStatusHistory.startTs.desc())
                .fetchFirst();

        if (lastHistoryEntryId != null) {
            queryFactory.update(qFacilityStatusHistory)
                    .set(qFacilityStatusHistory.endTs, currentDate)
                    .where(qFacilityStatusHistory.id.eq(lastHistoryEntryId))
                    .execute();
        }
    }

    private void setEndDateForPreviousCapacityHistoryEntry(long facilityId, DateTime currentDate) {
        final Long lastHistoryEntryId = queryFactory.query().select(qFacilityCapacityHistory.id)
                .from(qFacilityCapacityHistory)
                .where(qFacilityCapacityHistory.facilityId.eq(facilityId))
                .orderBy(qFacilityCapacityHistory.startTs.desc())
                .fetchFirst();

        if (lastHistoryEntryId != null) {
            queryFactory.update(qFacilityCapacityHistory)
                    .set(qFacilityCapacityHistory.endTs, currentDate)
                    .where(qFacilityCapacityHistory.id.eq(lastHistoryEntryId))
                    .execute();
        }
    }

    @Override
    @TransactionalRead
    public List<FacilityStatusHistory> getStatusHistory(long facilityId) {
        return queryFactory.query()
                .select(constructor(
                        FacilityStatusHistory.class,
                        qFacilityStatusHistory.facilityId,
                        qFacilityStatusHistory.startTs,
                        qFacilityStatusHistory.endTs,
                        qFacilityStatusHistory.status,
                        statusHistoryDescriptionMapping
                ))
                .from(qFacilityStatusHistory)
                .where(qFacilityStatusHistory.facilityId.eq(facilityId))
                .orderBy(qFacilityStatusHistory.startTs.asc())
                .fetch();
    }

    @Override
    @TransactionalRead
    public List<FacilityCapacityHistory> getCapacityHistory(long facilityId) {
        final List<ExtendedCapacityHistory> capacityHistory = getCapacityHistoryWithoutUnavailableCapacities(facilityId);

        final Set<Long> historyEntryIds = capacityHistory.stream().map(c -> c.id).collect(toSet());
        final Map<Long, List<UnavailableCapacity>> unavailableCapacities = queryFactory
                .from(qUnavailableCapacityHistory)
                .where(qUnavailableCapacityHistory.capacityHistoryId.in(historyEntryIds))
                .transform(groupBy(qUnavailableCapacityHistory.capacityHistoryId).as(list(unavailableCapacityHistoryMapping)));

        return capacityHistory.stream()
                .map(entry -> {
                    entry.unavailableCapacities = unavailableCapacities.get(entry.id);
                    return entry.strip();
                })
                .collect(toList());
    }

    private List<ExtendedCapacityHistory> getCapacityHistoryWithoutUnavailableCapacities(long facilityId) {
        return queryFactory.query()
                .select(constructor(
                        ExtendedCapacityHistory.class,
                        qFacilityCapacityHistory.id,
                        qFacilityCapacityHistory.facilityId,
                        qFacilityCapacityHistory.startTs,
                        qFacilityCapacityHistory.endTs,
                        new MappingProjection<Map<CapacityType, Integer>>(Map.class, qFacilityCapacityHistory.all()) {
                            @Override
                            protected Map<CapacityType, Integer> map(Tuple row) {
                                final Map<CapacityType, Integer> map = new HashMap<>();
                                mapCapacity(map, CAR, row.get(qFacilityCapacityHistory.capacityCar));
                                mapCapacity(map, DISABLED, row.get(qFacilityCapacityHistory.capacityDisabled));
                                mapCapacity(map, ELECTRIC_CAR, row.get(qFacilityCapacityHistory.capacityElectricCar));
                                mapCapacity(map, MOTORCYCLE, row.get(qFacilityCapacityHistory.capacityMotorcycle));
                                mapCapacity(map, BICYCLE, row.get(qFacilityCapacityHistory.capacityBicycle));
                                mapCapacity(map, BICYCLE_SECURE_SPACE, row.get(qFacilityCapacityHistory.capacityBicycleSecureSpace));
                                return map;
                            }
                        }
                ))
                .from(qFacilityCapacityHistory)
                .where(qFacilityCapacityHistory.facilityId.eq(facilityId))
                .orderBy(qFacilityCapacityHistory.startTs.asc())
                .fetch();
    }

    public static class ExtendedCapacityHistory extends FacilityCapacityHistory {
        public Long id;

        public ExtendedCapacityHistory(Long id, Long facilityId, DateTime startDate, DateTime endDate, Map<CapacityType, Integer> builtCapacity) {
            super(facilityId, startDate, endDate, builtCapacity);
            this.id = id;
        }

        public FacilityCapacityHistory strip() {
            return new FacilityCapacityHistory(
                    facilityId,
                    startDate,
                    endDate,
                    builtCapacity,
                    unavailableCapacities
            );
        }
    }
}
