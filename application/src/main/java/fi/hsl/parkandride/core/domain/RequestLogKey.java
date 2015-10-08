// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class RequestLogKey implements Comparable<RequestLogKey> {

    private static final Comparator<RequestLogKey> KEY_COMPARATOR = Comparator.comparing((RequestLogKey rlk) -> rlk.timestamp)
            .thenComparing(rlk -> rlk.source)
            .thenComparing(rlk -> rlk.urlPattern);
    public final String urlPattern;
    public final String source;
    public final DateTime timestamp;

    public RequestLogKey(@Nonnull String urlPattern, @Nonnull String source, @Nonnull DateTime timestamp) {
        this.urlPattern = Objects.requireNonNull(urlPattern);
        this.source = Objects.requireNonNull(source);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    /**
     * Rounds the timestamp to the nearest hour
     * @return a copy of the key with timestamp rounded down to the latest full hour
     */
    public RequestLogKey roundTimestampDown() {
        return new RequestLogKey(
                urlPattern,
                source,
                timestamp.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLogKey that = (RequestLogKey) o;
        return Objects.equals(this.urlPattern, that.urlPattern) &&
                Objects.equals(this.source, that.source) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        int result = urlPattern != null ? urlPattern.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("source", source)
                .add("url", urlPattern)
                .add("ts", timestamp)
                .toString();
    }

    @Override
    public int compareTo(RequestLogKey o) {
        return KEY_COMPARATOR.compare(this, o);
    }
}
