// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import fi.hsl.parkandride.back.sql.QFacilityUtilization;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.UtilizationSearch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

public class UtilizationDao implements UtilizationRepository {

    private static final QFacilityUtilization qUtilization = QFacilityUtilization.facilityUtilization;
    private static final MappingProjection<Utilization> utilizationMapping = new MappingProjection<Utilization>(Utilization.class, qUtilization.all()) {
        @Override
        protected Utilization map(Tuple row) {
            Utilization u = new Utilization();
            u.facilityId = row.get(qUtilization.facilityId);
            u.capacityType = row.get(qUtilization.capacityType);
            u.usage = row.get(qUtilization.usage);
            u.timestamp = row.get(qUtilization.ts);
            u.spacesAvailable = row.get(qUtilization.spacesAvailable);
            return u;
        }
    };

    private final PostgreSQLQueryFactory queryFactory;

    public UtilizationDao(PostgreSQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public void insertUtilizations(List<Utilization> utilizations) {
        if (utilizations.isEmpty()) {
            return;
        }
        SQLInsertClause insertBatch = queryFactory.insert(qUtilization);
        utilizations.forEach(u -> {
            insertBatch.set(qUtilization.facilityId, u.facilityId);
            insertBatch.set(qUtilization.capacityType, u.capacityType);
            insertBatch.set(qUtilization.usage, u.usage);
            insertBatch.set(qUtilization.spacesAvailable, u.spacesAvailable);
            insertBatch.set(qUtilization.ts, u.timestamp);
            insertBatch.addBatch();
        });
        insertBatch.execute();
    }

    @TransactionalRead
    @Override
    public Set<Utilization> findLatestUtilization(long facilityId) {
        // TODO: do with a single query
        List<Tuple> utilizationKeyCombinations = queryFactory.from(qUtilization)
                .where(qUtilization.facilityId.eq(facilityId))
                .select(qUtilization.capacityType, qUtilization.usage)
                .distinct()
                .fetch();
        return utilizationKeyCombinations.stream()
                .map(utilizationKey -> queryFactory.from(qUtilization)
                        .select(utilizationMapping)
                        .where(qUtilization.facilityId.eq(facilityId),
                                qUtilization.capacityType.eq(utilizationKey.get(qUtilization.capacityType)),
                                qUtilization.usage.eq(utilizationKey.get(qUtilization.usage)))
                        .orderBy(qUtilization.ts.desc())
                        .fetchFirst())
                .collect(Collectors.toSet());
    }

    @TransactionalRead
    @Override
    public Optional<Utilization> findUtilizationAtInstant(UtilizationKey utilizationKey, DateTime instant) {
        return Optional.ofNullable(queryFactory.from(qUtilization)
                .select(utilizationMapping)
                .where(qUtilization.facilityId.eq(utilizationKey.facilityId),
                        qUtilization.capacityType.eq(utilizationKey.capacityType),
                        qUtilization.usage.eq(utilizationKey.usage),
                        qUtilization.ts.eq(instant).or(qUtilization.ts.before(instant)))
                .orderBy(qUtilization.ts.desc())
                .fetchFirst())
                .map(u -> {
                    u.timestamp = instant;
                    return u;
                });
    }

    @Transactional(readOnly = true, isolation = READ_COMMITTED, propagation = MANDATORY)
    @Override
    public CloseableIterator<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end) {
        UtilizationSearch search = new UtilizationSearch();
        search.facilityIds.add(utilizationKey.facilityId);
        search.usages.add(utilizationKey.usage);
        search.capacityTypes.add(utilizationKey.capacityType);
        search.start = start;
        search.end = end;
        return findUtilizations(search);
    }

    @Transactional(readOnly = true, isolation = READ_COMMITTED, propagation = MANDATORY)
    @Override
    public List<Utilization> findUtilizationsWithResolution(UtilizationKey utilizationKey, DateTime start, DateTime end, Minutes resolution) {
        ArrayList<Utilization> results = new ArrayList<>();
        Optional<Utilization> first = findUtilizationAtInstant(utilizationKey, start);
        try (CloseableIterator<Utilization> rest = findUtilizationsBetween(utilizationKey, start, end)) {
            LinkedList<Utilization> utilizations = Stream.concat(
                    StreamUtil.asStream(first),
                    StreamUtil.asStream(rest))
                    .collect(Collectors.toCollection(LinkedList::new));

            Utilization current = null;
            for (DateTime instant = start; !instant.isAfter(end); instant = instant.plus(resolution)) {
                while (!utilizations.isEmpty() && !utilizations.getFirst().timestamp.isAfter(instant)) {
                    current = utilizations.removeFirst();
                }
                if (current != null) {
                    current.timestamp = instant;
                    results.add(current.copy());
                }
            }
        }
        return results;
    }

    @Transactional(readOnly = true, isolation = READ_COMMITTED, propagation = MANDATORY)
    @Override
    public CloseableIterator<Utilization> findUtilizations(UtilizationSearch search) {
        // TODO: add support for JDBC setFetchSize to QueryDSL, without it PostgreSQL will not stream results, but instead reads all results to memory
        final PostgreSQLQuery<Utilization> q = queryFactory.from(qUtilization).select(utilizationMapping);
        q.where(qUtilization.ts.between(search.start, search.end));
        addCriteria(q, search.facilityIds, qUtilization.facilityId);
        addCriteria(q, search.capacityTypes, qUtilization.capacityType);
        addCriteria(q, search.usages, qUtilization.usage);
        return q.orderBy(qUtilization.ts.asc()).iterate();
    }

    private static <S, T extends Comparable<T>> PostgreSQLQuery<S> addCriteria(PostgreSQLQuery<S> q, Collection<T> collection, ComparableExpressionBase<T> path) {
        switch (collection.size()) {
            case 0:
                return q;
            case 1:
                return q.where(path.eq(collection.iterator().next()));
            default:
                return q.where(path.in(collection));
        }
    }
}
