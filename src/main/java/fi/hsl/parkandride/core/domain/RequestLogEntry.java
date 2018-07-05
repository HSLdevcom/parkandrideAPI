// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

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

    public RequestLogEntry sum(RequestLogEntry other) {
        if (!this.key.equals(requireNonNull(other.key))) {
            throw new IllegalArgumentException(String.format("Can't sum request log entries with different keys: %s %s", this.key, other.key));
        }
        return new RequestLogEntry(key, this.count + other.count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLogEntry)) {
            return false;
        }
        RequestLogEntry that = (RequestLogEntry) o;
        return Objects.equal(key, that.key) &&
                Objects.equal(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, count);
    }
}
