// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.google.common.collect.Maps;
import fi.hsl.parkandride.core.domain.RequestLogEntry;
import fi.hsl.parkandride.core.domain.RequestLogKey;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestLogDaoTest extends AbstractDaoTest {

    private static final DateTime TEST_DATE = DateTime.now();
    private static final DateTime TEST_DATE_ROUNDED = roundDown(DateTime.now());

    private static DateTime roundDown(DateTime dateTime) {
        return dateTime
                .withMillisOfSecond(0)
                .withSecondOfMinute(0)
                .withMinuteOfHour(0);
    }

    private static final String URL = "/api/v1/facilities/{}";
    private static final String SOURCE = "#webkäli";

    @Inject
    RequestLogDao requestLogDao;

    @Test
    public void testDao_pureBatchInsert() {
        final List<RequestLogKey> keys = getKeysForTimes(TEST_DATE, TEST_DATE.plusHours(1), TEST_DATE.plusHours(2));

        requestLogDao.batchIncrement(Maps.toMap(keys, key -> 1230l));

        final List<RequestLogEntry> logEntries = getRelevantLogEntries();

        assertThat(logEntries).hasSize(3);
        assertThat(logEntries).extracting("key").containsExactlyElementsOf(
                keys.stream().map(key -> key.roundTimestampDown()).collect(toList()));

        final RequestLogEntry entry = logEntries.get(0);
        assertThat(entry.key).isEqualTo(new RequestLogKey(URL, SOURCE, TEST_DATE_ROUNDED));
        assertThat(entry.count).isEqualTo(1230l);
    }

    @Test
    public void testDao_increment() {
        final DateTime otherDate = TEST_DATE.millisOfSecond().withMaximumValue();

        // These should be summed
        requestLogDao.batchIncrement(singletonMap(new RequestLogKey(URL, SOURCE, TEST_DATE), 777l));
        requestLogDao.batchIncrement(singletonMap(new RequestLogKey(URL, SOURCE, otherDate), 123l));

        // These should not be summed to the same entry
        requestLogDao.batchIncrement(singletonMap(new RequestLogKey(URL, SOURCE + "foo", TEST_DATE), 123l));
        requestLogDao.batchIncrement(singletonMap(new RequestLogKey(URL + "foo", SOURCE, TEST_DATE), 123l));

        final List<RequestLogEntry> logEntries = getRelevantLogEntries();

        assertThat(logEntries).hasSize(3);
        final RequestLogEntry entry = logEntries.get(0);
        assertThat(entry.key).isEqualTo(new RequestLogKey(URL, SOURCE, TEST_DATE_ROUNDED));
        assertThat(entry.count).isEqualTo(900l);
    }

    @Test
    public void testDao_overlappingTimestamps() {
        final List<RequestLogKey> keys = getKeysForTimes(
                TEST_DATE.withSecondOfMinute(1),
                TEST_DATE.withSecondOfMinute(2)
        );
        // Insert
        requestLogDao.batchIncrement(Maps.toMap(keys, key -> 666l));
        // Update
        requestLogDao.batchIncrement(Maps.toMap(keys, key -> 666l));

        final List<RequestLogEntry> logEntries = getRelevantLogEntries();

        // Only one entry is stored with summed results
        assertThat(logEntries).hasSize(1);
        assertThat(logEntries.get(0).count).isEqualTo(666l * 4);
    }

    @Test
    public void testDao_emptySet() {
        requestLogDao.batchIncrement(emptyMap());
        final List<RequestLogEntry> logEntries = getRelevantLogEntries();
        assertThat(logEntries).isEmpty();
    }

    private List<RequestLogEntry> getRelevantLogEntries() {
        return requestLogDao.getLogEntriesBetween(
                TEST_DATE.withTimeAtStartOfDay(),
                TEST_DATE.withTimeAtStartOfDay().plusDays(1)
        );
    }

    private List<RequestLogKey> getKeysForTimes(DateTime... times) {
        final List<DateTime> dates = asList(times);
        return dates.stream()
                .map(d -> new RequestLogKey(URL, SOURCE, d))
                .collect(toList());
    }

}