// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpressionBase;
import fi.hsl.parkandride.back.sql.QFacilityUtilization;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.UtilizationSearch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final PostgresQueryFactory queryFactory;

    public UtilizationDao(PostgresQueryFactory queryFactory) {
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
                .distinct()
                .list(qUtilization.capacityType, qUtilization.usage);
        return utilizationKeyCombinations.stream()
                .map(utilizationKey -> queryFactory.from(qUtilization)
                        .where(qUtilization.facilityId.eq(facilityId),
                               qUtilization.capacityType.eq(utilizationKey.get(qUtilization.capacityType)),
                               qUtilization.usage.eq(utilizationKey.get(qUtilization.usage)))
                        .orderBy(qUtilization.ts.desc())
                        .singleResult(utilizationMapping))
                .collect(Collectors.toSet());
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
    public CloseableIterator<Utilization> findUtilizations(UtilizationSearch search) {
        // TODO: add support for JDBC setFetchSize to QueryDSL, without it PostgreSQL will not stream results, but instead reads all results to memory
        PostgresQuery q = queryFactory.from(qUtilization).where(qUtilization.ts.between(search.start, search.end));
        q = addCriteria(q, search.facilityIds, qUtilization.facilityId);
        q = addCriteria(q, search.capacityTypes, qUtilization.capacityType);
        q = addCriteria(q, search.usages, qUtilization.usage);
        return q.orderBy(qUtilization.ts.asc()).iterate(utilizationMapping);
    }

    private static <T extends Comparable<T>> PostgresQuery addCriteria(PostgresQuery q, Collection<T> collection, ComparableExpressionBase<T> path) {
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
