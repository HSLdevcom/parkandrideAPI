// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLogKeyTest {

    private static final DateTime TIMESTAMP = DateTime.now().withMillisOfSecond(123);
    final RequestLogKey key = new RequestLogKey("url", "source", TIMESTAMP);

    @Test
    public void equality() {
        final RequestLogKey similar = new RequestLogKey("url", "source", TIMESTAMP);
        assertThat(key).isEqualTo(similar);
        assertThat(key.hashCode()).isEqualTo(similar.hashCode());

        assertThat(key).isEqualTo(key);
        assertThat(key).isNotEqualTo("foo");
        assertThat(key).isNotEqualTo(null);

        assertThat(key).isNotEqualTo(new RequestLogKey("foo", "source", TIMESTAMP));
        assertThat(key).isNotEqualTo(new RequestLogKey("url", "foo",    TIMESTAMP));
        assertThat(key).isNotEqualTo(new RequestLogKey("url", "source", DateTime.now().plusHours(1)));
    }

    @Test
    public void roundTimestamp_equality() {
        assertThat(key.roundTimestampDown())
                .isEqualTo(key.roundTimestampDown().roundTimestampDown());
        assertThat(key.roundTimestampDown().hashCode())
                .isEqualTo(key.roundTimestampDown().roundTimestampDown().hashCode());
    }

    @Test
    public void roundTimestamp() {
        final DateTime rounded = TIMESTAMP.withMillisOfSecond(0)
                .withSecondOfMinute(0)
                .withMinuteOfHour(0);
        assertThat(key.roundTimestampDown().timestamp).isEqualTo(rounded);
    }

}