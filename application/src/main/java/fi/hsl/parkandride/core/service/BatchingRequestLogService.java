// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toMap;

public class BatchingRequestLogService {

    private static final Logger logger = LoggerFactory.getLogger(BatchingRequestLogService.class);
    private final ConcurrentHashMap<RequestLogKey, AtomicInteger> requestLogBatch = new ConcurrentHashMap<>();

    private final RequestLogRepository requestLogRepository;

    public BatchingRequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    public void increment(RequestLogKey key) {
        final RequestLogKey roundedTimestamp = key.roundTimestampDown();
        requestLogBatch.putIfAbsent(roundedTimestamp, new AtomicInteger(0));
        requestLogBatch.get(roundedTimestamp).incrementAndGet();
    }

    @Scheduled(cron = "${requestlog.cron:0 */5 * * * *}")
    @TransactionalWrite
    public void updateRequestLogs() {
        logger.info("Update request logs");
        final Map<RequestLogKey, Long> requestCounts = requestLogBatch.keySet().stream()
                .collect(toMap(
                        key -> key,
                        key -> requestLogBatch.remove(key).longValue()
                ));
        requestLogRepository.batchIncrement(requestCounts);
    }

    public void clearLogStash() {
        requestLogBatch.clear();
    }
}
