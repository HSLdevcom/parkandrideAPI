// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import fi.hsl.parkandride.back.sql.QFacilityUtilization;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;

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

    @TransactionalRead
    @Override
    public List<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end) {
        // TODO: limit the amount of results per query or return a lazy iterator (must also update UtilizationHistory)
        return queryFactory.from(qUtilization)
                .where(qUtilization.facilityId.eq(utilizationKey.facilityId),
                        qUtilization.capacityType.eq(utilizationKey.capacityType),
                        qUtilization.usage.eq(utilizationKey.usage),
                        qUtilization.ts.between(start, end))
                .orderBy(qUtilization.ts.asc())
                .list(utilizationMapping);
    }
}
