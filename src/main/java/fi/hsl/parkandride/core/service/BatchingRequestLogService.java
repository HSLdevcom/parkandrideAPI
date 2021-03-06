// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.RequestLogRepository;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

import static java.util.stream.Collectors.toMap;

public class BatchingRequestLogService {

    private static final Logger logger = LoggerFactory.getLogger(BatchingRequestLogService.class);
    private Map<RequestLogKey, LongAdder> requestLogBatch = new ConcurrentHashMap<>();
    public Duration staggeredUpdateMaxDelay = Duration.ofMinutes(1);

    private final RequestLogRepository requestLogRepository;

    public BatchingRequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    public void increment(RequestLogKey key) {
        final RequestLogKey roundedTimestamp = key.roundTimestampDown();
        requestLogBatch.computeIfAbsent(roundedTimestamp, k -> new LongAdder()).increment();
    }

    @Scheduled(cron = "${requestlog.cron:0 */5 * * * *}")
    public void updateRequestLogs() {
        try {
            // When multiple instances in the cluster do this update
            // in parallel, it quite often causes transaction conflicts
            // and starvation. A staggered start using a random delay
            // improves the probability of them not conflicting.
            long delayMillis = ThreadLocalRandom.current().nextLong(staggeredUpdateMaxDelay.toMillis() + 1);
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        logger.info("Update request logs");

        final Map<RequestLogKey, LongAdder> previousMap = this.requestLogBatch;
        clearLogStash();

        final Map<RequestLogKey, Long> requestCounts = previousMap.keySet().stream()
                .collect(toMap(
                        key -> key,
                        key -> previousMap.get(key).sum()
                ));
        requestLogRepository.batchIncrement(requestCounts);
    }

    public void clearLogStash() {
        requestLogBatch = new ConcurrentHashMap<>();
    }
}
