// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import static com.google.common.base.MoreObjects.toStringHelper;

public class RequestLogEntry {

    public final RequestLogKey key;
    public final Long count;

    public RequestLogEntry(RequestLogKey key, Long count) {
        this.key = key;
        this.count = count;
    }

    /** Used by {@link fi.hsl.parkandride.back.RequestLogDao#getLogEntriesBetween(DateTime, DateTime)} */
    @SuppressWarnings("unused")
    public RequestLogEntry(String urlPattern, String source, DateTime timestamp, Long count) {
        this(new RequestLogKey(urlPattern, source, timestamp), count);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("key", key)
                .add("count", count)
                .toString();
    }
}
