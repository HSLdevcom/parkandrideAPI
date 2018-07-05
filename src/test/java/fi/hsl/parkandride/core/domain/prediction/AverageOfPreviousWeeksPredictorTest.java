// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AverageOfPreviousWeeksPredictorTest extends AbstractPredictorTest {

    public AverageOfPreviousWeeksPredictorTest() {
        super(new AverageOfPreviousWeeksPredictor());
    }

    @Before
    public void defineCurrentTime() {
        // the predictor is independent of system time, so the latest utilization defines "now"
        insertUtilization(now, 0);
    }

    @After
    public void checkUpdatesLatestUtilization() {
        assertThat(predictorState.latestUtilization).isEqualTo(now);
    }

    @Test
    public void when_less_than_6_days_of_history_then_no_predictions() {
        insertUtilization(now.minusDays(6).plusSeconds(1), 666);

        List<Prediction> predictions = predict();

        assertThat(predictions).isEmpty();
    }

    @Test
    public void when_1_week_of_history_then_predicts_same_as_one_week_ago() {
        insertUtilization(now.minusDays(7), 10);
        insertUtilization(now.minusDays(6), 20);

        List<Prediction> predictions = predict();

        assertThat(predictions).containsSubsequence(
                new Prediction(now, 10),
                new Prediction(now.plusDays(1), 20));
    }

    @Test
    public void when_3_weeks_of_history_then_predicts_the_mean_of_all_the_weeks() {
        insertUtilization(now.minusWeeks(3).plusDays(0), 10);
        insertUtilization(now.minusWeeks(3).plusDays(1), 20);
        insertUtilization(now.minusWeeks(2).plusDays(0), 17);
        insertUtilization(now.minusWeeks(2).plusDays(1), 27);
        insertUtilization(now.minusWeeks(1).plusDays(0), 18);
        insertUtilization(now.minusWeeks(1).plusDays(1), 28);

        List<Prediction> predictions = predict();

        assertThat(predictions).containsSubsequence(
                new Prediction(now, 15),
                new Prediction(now.plusDays(1), 25));
    }

    @Test
    public void when_more_than_3_weeks_of_history_then_uses_only_the_last_3_weeks() {
        insertUtilization(now.minusWeeks(4).plusDays(0), 666);
        insertUtilization(now.minusWeeks(3).plusDays(0), 10);
        insertUtilization(now.minusWeeks(3).plusDays(1), 20);
        insertUtilization(now.minusWeeks(2).plusDays(0), 10);
        insertUtilization(now.minusWeeks(2).plusDays(1), 20);
        insertUtilization(now.minusWeeks(1).plusDays(0), 10);
        insertUtilization(now.minusWeeks(1).plusDays(1), 20);

        List<Prediction> predictions = predict();

        assertThat(predictions).containsSubsequence(
                new Prediction(now, 10),
                new Prediction(now.plusDays(1), 20));
    }

    // TODO: shift prediction up/down to start from current utilization baseline
    // TODO: 5 weeks of history, discard 2 outliers, average
    // TODO: more than 5 weeks of history, use only the last 5 weeks
}
