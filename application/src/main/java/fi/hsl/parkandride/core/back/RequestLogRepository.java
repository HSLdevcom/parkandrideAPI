// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface RequestLogRepository {

    /**
     * Increment all given request logs with the amount specified.
     * <p/>
     * Should a log entry with the same key already exist, it is updated with the previous value incremented by the
     * count. If no such key exists, a new row will be created..
     *
     * @param requestLogCounts the request counts associated with the keys
     */
    void batchIncrement(Map<RequestLogKey, Long> requestLogCounts);


    /**
     * Get the request log entries between the two given dates ordered by (timestamp, source, url)
     *
     * @param startInclusive the start time (inclusive)
     * @param endInclusive the end time (inclusive)
     * @return a list of log entries or empty list if none
     */
    List<RequestLogEntry> getLogEntriesBetween(DateTime startInclusive, DateTime endInclusive);
}
