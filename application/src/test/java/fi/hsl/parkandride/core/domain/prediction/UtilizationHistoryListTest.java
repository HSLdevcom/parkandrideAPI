// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import fi.hsl.parkandride.core.domain.Utilization;
import org.assertj.core.api.Condition;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.HSL_TRAVEL_CARD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class UtilizationHistoryListTest {

    static final DateTime LATEST_DATETIME = new DateTime(2015, 10, 23, 14, 0);
    static final Long FACILITY_ID = 123L;
    static final DateTime RANGE_START_DATETIME = LATEST_DATETIME.minusMinutes(20);
    static final DateTime RANGE_END_DATETIME = LATEST_DATETIME.minusMinutes(10);

    public static abstract class Base {
        protected List<Utilization> utilizationList;
        protected UtilizationHistoryList historyList;

        @Before
        public void setUp() throws Exception {
            utilizationList = Arrays.asList(
                    newUtilization(LATEST_DATETIME.minusMinutes(25), 50),
                    newUtilization(LATEST_DATETIME.minusMinutes(20), 40),
                    newUtilization(LATEST_DATETIME.minusMinutes(15), 30),
                    newUtilization(LATEST_DATETIME.minusMinutes(10), 20),
                    newUtilization(LATEST_DATETIME.minusMinutes(5), 15),
                    newUtilization(LATEST_DATETIME, 10));
            historyList = new UtilizationHistoryList(utilizationList);
        }

        @After
        public void tearDown() throws Exception {
        }
    }

    public static class GetLatest extends Base {
        @Test
        public void latest_utilization_is_returned() throws Exception {
            assertThat(historyList.getLatest()).isPresent();
            assertThat(historyList.getLatest().get().timestamp).isEqualTo(LATEST_DATETIME);
        }
    }

    public static class GetRange extends Base {
        @Test
        public void when_start_and_end_timestamps_match_entries_then_boundary_entries_must_be_included() throws Exception {
            assertThat(historyList.getRange(RANGE_START_DATETIME, RANGE_END_DATETIME))
                    .areNot(new TimestampBefore(RANGE_START_DATETIME))
                    .areNot(new TimestampAfter(RANGE_END_DATETIME))
                    .hasSize(3);
        }
    }

    public static class GetUpdatesSince extends Base {
        @Test
        public void getUpdatesSince_is_inclusive() throws Exception {
            assertThat(historyList.getUpdatesSince(RANGE_START_DATETIME))
                    .areNot(new TimestampBefore(RANGE_START_DATETIME))
                    .hasSize(5);
        }
    }

    public static class  GetAt extends Base {
        @Test
        public void when_getAt_is_called_with_matching_timestamp_then_Utilization_with_matching_timestamp_is_returned() {
            assertThat(historyList.getAt(LATEST_DATETIME.minusMinutes(5)))
                    .isPresent()
                    .contains(newUtilization(LATEST_DATETIME.minusMinutes(5), 15));
        }

        @Test
        public void when_getAt_is_called_with_non_matching_timestamp_then_Utilization_with_closest_previous_timestamp_is_returned() {
            assertThat(historyList.getAt(LATEST_DATETIME.minusMinutes(1)))
                    .isPresent()
                    .contains(newUtilization(LATEST_DATETIME.minusMinutes(5), 15));
        }
    }

    private static Utilization newUtilization(DateTime time, int spacesAvailable) {
        Utilization u = new Utilization();
        u.facilityId = FACILITY_ID;
        u.capacityType = CAR;
        u.usage = HSL_TRAVEL_CARD;
        u.timestamp = time;
        u.spacesAvailable = spacesAvailable;
        return u;
    }

    private static class TimestampAfter extends Condition<Utilization> {
        private final DateTime lowerBound;

        public TimestampAfter(DateTime lowerBound) {
            super("After " + lowerBound);
            this.lowerBound = lowerBound;
        }

        @Override
        public boolean matches(Utilization utilization) {
            return utilization.timestamp.isAfter(lowerBound);
        }
    }

    private static class TimestampBefore extends Condition<Utilization> {
        private final DateTime upperBound;

        public TimestampBefore(DateTime upperBound) {
            super("Before " + upperBound);
            this.upperBound = upperBound;
        }

        @Override
        public boolean matches(Utilization utilization) {
            return utilization.timestamp.isBefore(upperBound);
        }
    }
}
