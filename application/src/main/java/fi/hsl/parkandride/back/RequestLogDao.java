// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.google.common.collect.Maps;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.hsl.parkandride.back.sql.QRequestLog;
import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

import static com.mysema.query.types.ConstructorExpression.create;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.concat;

public class RequestLogDao implements RequestLogRepository {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogDao.class);
    private static final QRequestLog qRequestLog = QRequestLog.requestLog;

    private final PostgresQueryFactory queryFactory;

    public RequestLogDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalRead
    @Override
    public List<RequestLogEntry> getLogEntriesBetween(DateTime startInclusive, DateTime endInclusive) {
        return queryFactory.from(qRequestLog)
                .where(qRequestLog.ts.between(startInclusive, endInclusive))
                .orderBy(qRequestLog.ts.asc(), qRequestLog.url.asc(), qRequestLog.source.asc())
                .list(create(RequestLogEntry.class, qRequestLog.url, qRequestLog.source, qRequestLog.ts, qRequestLog.count));
    }

    @Override
    @TransactionalWrite
    public void batchIncrement(Map<RequestLogKey, Long> nonNormalizedRequestLogCounts) {
        if (nonNormalizedRequestLogCounts.isEmpty()) {
            return;
        }
        // Normalize timestamps to hour
        final Map<RequestLogKey, Long> requestLogCounts = normalizeTimestamps(nonNormalizedRequestLogCounts);

        // Get rows to update
        final Set<DateTime> timestamps = requestLogCounts.keySet().stream().map(key -> key.timestamp).collect(toSet());
        final Map<RequestLogKey, Long> previousCountsForUpdate = getPreviousCountsForUpdate(timestamps);

        // Partition for insert/update
        final Map<Boolean, List<Map.Entry<RequestLogKey, Long>>> partitioned = requestLogCounts.entrySet().stream().collect(
                partitioningBy(entry -> previousCountsForUpdate.containsKey(entry.getKey()))
        );

        insertNew(partitioned.get(Boolean.FALSE));

        // Calculate sum for the new ones
        final Map<RequestLogKey, Long> summedCounts = concat(
                partitioned.get(Boolean.TRUE).stream(),
                previousCountsForUpdate.entrySet().stream()
        ).collect(toMapSummingCounts());
        updateExisting(summedCounts);
    }

    private void updateExisting(Map<RequestLogKey, Long> entries) {
        if (entries.isEmpty()) {
            return;
        }
        final SQLUpdateClause update = queryFactory.update(qRequestLog);
        entries.forEach((key, val) -> {
            update.set(qRequestLog.count, val);
            update.where(qRequestLog.ts.eq(key.timestamp)
                            .and(qRequestLog.source.eq(key.source))
                            .and(qRequestLog.url.eq(key.urlPattern))
            );
            update.addBatch();
        });
        update.execute();
    }

    private void insertNew(List<Map.Entry<RequestLogKey, Long>> requestLogCounts) {
        if (requestLogCounts.isEmpty()) {
            return;
        }
        final SQLInsertClause insert = queryFactory.insert(qRequestLog);
        requestLogCounts.forEach(entry -> {
            final RequestLogKey key = entry.getKey().roundTimestampDown();
            insert.set(qRequestLog.ts, key.timestamp);
            insert.set(qRequestLog.source, key.source);
            insert.set(qRequestLog.url, key.urlPattern);
            insert.set(qRequestLog.count, entry.getValue());
            insert.addBatch();
        });
        insert.execute();
    }

    private static Map<RequestLogKey, Long> normalizeTimestamps(Map<RequestLogKey, Long> logCounts) {
        final Map<RequestLogKey, Long> normalized = logCounts.entrySet().stream()
                .map(entry -> Maps.immutableEntry(entry.getKey().roundTimestampDown(), entry.getValue()))
                .collect(toMapSummingCounts());
        if (logCounts.size() != normalized.size()) {
            final List<DateTime> duplicatedTimestamps = collectDuplicateTimestamps(logCounts);
            logger.warn("Encountered entries with duplicated keys during timestamp normalization. The duplicated timestamps were summed. Duplicated timestamps: {}", duplicatedTimestamps);
        }
        return normalized;
    }

    private static List<DateTime> collectDuplicateTimestamps(Map<RequestLogKey, Long> logCounts) {
        return logCounts.keySet().stream()
                .map(key -> key.roundTimestampDown().timestamp)
                .collect(groupingBy(identity(), counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .collect(toList());
    }

    private static Collector<Map.Entry<RequestLogKey, Long>, ?, Map<RequestLogKey, Long>> toMapSummingCounts() {
        return toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue(),
                Long::sum
        );
    }

    private Map<RequestLogKey, Long> getPreviousCountsForUpdate(Set<DateTime> timestamps) {
        return queryFactory.from(qRequestLog)
                .forUpdate()
                .where(qRequestLog.ts.in(timestamps))
                .map(
                        create(RequestLogKey.class, qRequestLog.url, qRequestLog.source, qRequestLog.ts),
                        qRequestLog.count
                );
    }
}
