// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.querydsl.core.Tuple;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.SimpleExpression;
import fi.hsl.parkandride.back.sql.QRequestLog;
import fi.hsl.parkandride.back.sql.QRequestLogSource;
import fi.hsl.parkandride.back.sql.QRequestLogUrl;
import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.util.MapUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.querydsl.core.types.ConstructorExpression.create;
import static fi.hsl.parkandride.util.MapUtils.extractFromKeys;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.concat;

public class RequestLogDao implements RequestLogRepository {

    public static final String SOURCE_ID_SEQ = "request_log_source_id_seq";
    public static final String URL_ID_SEQ = "request_log_url_id_seq";
    private static final SimpleExpression<Long> nextSourceId = SQLExpressions.nextval(SOURCE_ID_SEQ);
    private static final SimpleExpression<Long> nextUrlId = SQLExpressions.nextval(URL_ID_SEQ);

    private static final Logger logger = LoggerFactory.getLogger(RequestLogDao.class);
    private static final QRequestLog qRequestLog = QRequestLog.requestLog;
    private static final QRequestLogSource qRequestLogSource = QRequestLogSource.requestLogSource;
    private static final QRequestLogUrl qRequestLogUrl = QRequestLogUrl.requestLogUrl;

    private final PostgreSQLQueryFactory queryFactory;

    public RequestLogDao(PostgreSQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalRead
    @Override
    public List<RequestLogEntry> getLogEntriesBetween(DateTime startInclusive, DateTime endInclusive) {
        final BiMap<Long, String> urls = getAllUrlPatterns();
        final BiMap<Long, String> sources = getAllSources();
        final List<RequestLogEntry> list = queryFactory.from(qRequestLog)
                .where(qRequestLog.ts.between(startInclusive, endInclusive))
                .list(create(RequestLogEntry.class, new RequestLogKeyProjection(sources, urls), qRequestLog.count));
        list.sort(comparing(entry -> entry.key));
        return list;
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
        final Set<DateTime> timestamps = extractFromKeys(requestLogCounts, key -> key.timestamp);
        final Map<RequestLogKey, Long> previousCountsForUpdate = getPreviousCountsForUpdate(timestamps);

        // Insert new sources and urls
        final BiMap<Long, String> allSources = insertNewSources(extractFromKeys(requestLogCounts, key -> key.source));
        final BiMap<Long, String> allUrls = insertNewUrls(extractFromKeys(requestLogCounts, key -> key.urlPattern));

        // Partition for insert/update
        final Map<Boolean, List<Map.Entry<RequestLogKey, Long>>> partitioned = requestLogCounts.entrySet().stream().collect(
                partitioningBy(entry -> previousCountsForUpdate.containsKey(entry.getKey()))
        );

        // Insert non-existing rows
        insertNew(partitioned.get(Boolean.FALSE), allSources.inverse(), allUrls.inverse());

        // Update counts for existing keys
        final Map<RequestLogKey, Long> summedCounts = concat(
                partitioned.get(Boolean.TRUE).stream(),
                previousCountsForUpdate.entrySet().stream()
        ).collect(toMapSummingCounts());
        updateExisting(summedCounts, allSources.inverse(), allUrls.inverse());
    }

    private BiMap<Long, String> insertNewUrls(Set<String> toInsert) {
        return insertMissing(toInsert, this::getAllUrlPatterns, qRequestLogUrl, (insert, url) -> {
            insert.set(qRequestLogUrl.id, nextUrlId);
            insert.set(qRequestLogUrl.url, url);
        });
    }

    private BiMap<Long, String> insertNewSources(Set<String> toInsert) {
        return insertMissing(toInsert, this::getAllSources, qRequestLogSource, (insert, source) -> {
            insert.set(qRequestLogSource.id, nextSourceId);
            insert.set(qRequestLogSource.source, source);
        });
    }

    private BiMap<Long, String> insertMissing(Set<String> toInsert, Supplier<BiMap<Long, String>> persistedGetter, RelationalPath<?> path, BiConsumer<SQLInsertClause, String> processor) {
        final BiMap<Long, String> persisted = persistedGetter.get();
        final Set<String> difference = difference(toInsert, persisted);

        if (difference.isEmpty()) {
            // Nothing to insert, just return the already persisted sources
            return persisted;
        }
        insertBatch(difference, path, processor);
        return persistedGetter.get();
    }

    private <T> void insertBatch(Collection<T> batch, RelationalPath<?> expression, BiConsumer<SQLInsertClause, T> processor) {
        if (batch.isEmpty()) {
            return;
        }
        final SQLInsertClause insert = queryFactory.insert(expression);
        batch.forEach(item -> {
            processor.accept(insert, item);
            insert.addBatch();
        });
        insert.execute();
    }

    private <T> void updateBatch(Set<T> batch, RelationalPath<?> expression, BiConsumer<SQLUpdateClause, T> processor) {
        if (batch.isEmpty()) {
            return;
        }
        final SQLUpdateClause update = queryFactory.update(expression);
        batch.forEach(item -> {
            processor.accept(update, item);
            update.addBatch();
        });
        update.execute();
    }

    private void updateExisting(Map<RequestLogKey, Long> entries, Map<String, Long> sourceIdsBySource, Map<String, Long> urlIdsByUrl) {
        updateBatch(entries.entrySet(), qRequestLog, (update, entry) -> {
            final RequestLogKey key = entry.getKey();
            update.set(qRequestLog.count, entry.getValue());
            update.where(qRequestLog.ts.eq(key.timestamp)
                            .and(qRequestLog.sourceId.eq(sourceIdsBySource.get(key.source)))
                            .and(qRequestLog.urlId.eq(urlIdsByUrl.get(key.urlPattern)))
            );
        });
    }

    private void insertNew(List<Map.Entry<RequestLogKey, Long>> requestLogCounts, Map<String, Long> sourceIdsBySource, Map<String, Long> urlIdsByUrl) {
        insertBatch(requestLogCounts, qRequestLog, (insert, entry) -> {
            final RequestLogKey key = entry.getKey().roundTimestampDown();
            insert.set(qRequestLog.ts, key.timestamp);
            insert.set(qRequestLog.sourceId, sourceIdsBySource.get(key.source));
            insert.set(qRequestLog.urlId, urlIdsByUrl.get(key.urlPattern));
            insert.set(qRequestLog.count, entry.getValue());
        });
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
                .collect(MapUtils.countingOccurrences())
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .collect(toList());
    }

    private static Collector<Map.Entry<RequestLogKey, Long>, ?, Map<RequestLogKey, Long>> toMapSummingCounts() {
        return MapUtils.entriesToMap(Long::sum);
    }

    private Map<RequestLogKey, Long> getPreviousCountsForUpdate(Set<DateTime> timestamps) {
        final Map<Long, String> sources = getAllSources();
        final Map<Long, String> urls = getAllUrlPatterns();

        return queryFactory.from(qRequestLog)
                .forUpdate()
                .where(qRequestLog.ts.in(timestamps))
                .map(new RequestLogKeyProjection(sources, urls), qRequestLog.count);
    }

    private BiMap<Long, String> getAllUrlPatterns() {
        return HashBiMap.create(queryFactory.from(qRequestLogUrl).map(qRequestLogUrl.id, qRequestLogUrl.url));
    }

    private BiMap<Long, String> getAllSources() {
        return HashBiMap.create(queryFactory.from(qRequestLogSource).map(qRequestLogSource.id, qRequestLogSource.source));
    }

    private static Set<String> difference(Set<String> toPersist, BiMap<Long, String> alreadyPersisted) {
        return toPersist.stream()
                .filter(val -> !alreadyPersisted.containsValue(val))
                .collect(toSet());
    }

    private static class RequestLogKeyProjection extends MappingProjection<RequestLogKey> {

        private final Map<Long, String> sources;
        private final Map<Long, String> urls;

        public RequestLogKeyProjection(Map<Long, String> sources, Map<Long, String> urls) {
            super(RequestLogKey.class, QRequestLog.requestLog.all());
            this.sources = sources;
            this.urls = urls;
        }

        @Override
        protected RequestLogKey map(Tuple row) {
            return new RequestLogKey(
                    urls.get(row.get(qRequestLog.urlId)),
                    sources.get(row.get(qRequestLog.sourceId)),
                    row.get(qRequestLog.ts)
            );
        }
    }

}
