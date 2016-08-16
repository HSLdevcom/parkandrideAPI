// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.StatementOptions;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.back.sql.QFacilityUtilization;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
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
            u.capacity = row.get(qUtilization.capacity);
            return u;
        }
    };
    private static final RowMapper<Utilization> utilizationRowMapper = (rs, rowNum) -> {
        Utilization u = new Utilization();
        u.facilityId = rs.getLong("facility_id");
        u.capacityType = CapacityType.valueOf(rs.getString("capacity_type"));
        u.usage = Usage.valueOf(rs.getString("usage"));
        u.timestamp = new DateTime(rs.getTimestamp("ts").toInstant().toEpochMilli());
        u.spacesAvailable = rs.getInt("spaces_available");
        u.capacity = rs.getInt("capacity");
        return u;
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
            insertBatch.set(qUtilization.ts, u.timestamp);
            insertBatch.set(qUtilization.spacesAvailable, u.spacesAvailable);
            insertBatch.set(qUtilization.capacity, u.capacity);
            insertBatch.addBatch();
        });
        insertBatch.execute();
    }

    @TransactionalRead
    @Override
    public Set<Utilization> findLatestUtilization(long facilityId) {
        Connection connection = queryFactory.getConnection();
        if (isPostgreSQL(connection)) {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(
                    new SingleConnectionDataSource(connection, true));
            return new HashSet<>(jdbcTemplate.query("" +
                            "SELECT latest.* " +
                            "FROM capacity_type c, usage u " +
                            "JOIN LATERAL ( " +
                            "  SELECT * " +
                            "  FROM facility_utilization " +
                            "  WHERE facility_id = :facility_id AND capacity_type = c.name AND usage = u.name " +
                            "  ORDER BY ts DESC " +
                            "  LIMIT 1 " +
                            ") latest ON TRUE",
                    new MapSqlParameterSource("facility_id", facilityId),
                    utilizationRowMapper));
        } else {
            List<SubQueryExpression<Utilization>> queries = new ArrayList<>();
            for (CapacityType capacityType : CapacityType.values()) {
                for (Usage usage : Usage.values()) {
                    queries.add(queryFactory.from(qUtilization)
                            .select(utilizationMapping)
                            .where(qUtilization.facilityId.eq(facilityId),
                                    qUtilization.capacityType.eq(capacityType),
                                    qUtilization.usage.eq(usage))
                            .orderBy(qUtilization.ts.desc())
                            .limit(1));
                }
            }
            return new HashSet<>(queryFactory.query().union(queries).fetch());
        }
    }

    private static boolean isPostgreSQL(Connection connection) {
        try {
            return connection.getMetaData().getDatabaseProductName().equals("PostgreSQL");
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Failed to read connection metadata", e);
        }
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
        q.setStatementOptions(StatementOptions.builder().setFetchSize(100).build());
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
