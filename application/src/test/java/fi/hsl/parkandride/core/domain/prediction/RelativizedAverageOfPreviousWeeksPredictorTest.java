// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RelativizedAverageOfPreviousWeeksPredictorTest extends AbstractPredictorTest {

    public RelativizedAverageOfPreviousWeeksPredictorTest() {
        super(new RelativizedAverageOfPreviousWeeksPredictor());
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
    public void when_1_week_old_history_exists_but_no_recent_data_then_predicts_same_as_one_week_ago() {
        insertUtilization(now.minusDays(7), 10);
        insertUtilization(now.minusDays(7).plusMinutes(5), 11);
        insertUtilization(now.minusDays(7).plusMinutes(10), 12);
        insertUtilization(now.minusDays(7).plusMinutes(15), 13);
        insertUtilization(now.minusDays(7).plusMinutes(20), 14);

        List<Prediction> predictions = predict();

        assertThat(predictions).containsSubsequence(
                new Prediction(now, 10),
                new Prediction(now.plusMinutes(5), 11),
                new Prediction(now.plusMinutes(10), 12),
                new Prediction(now.plusMinutes(15), 13),
                new Prediction(now.plusMinutes(20), 14));
    }
}
