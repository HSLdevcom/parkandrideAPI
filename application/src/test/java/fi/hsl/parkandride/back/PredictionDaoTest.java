// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import fi.hsl.parkandride.core.service.ValidationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fi.hsl.parkandride.back.PredictionDao.*;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.Usage.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PredictionDaoTest extends AbstractDaoTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Inject Dummies dummies;
    @Inject PredictionRepository predictionDao;

    private final DateTime now = new DateTime();
    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
    }


    // basics

    @Test
    public void predict_now() {
        PredictionBatch pb = newPredictionBatch(now, new Prediction(now, 123));
        predictionDao.updatePredictions(pb);

        assertPredictionsSavedAsIs(pb);
    }

    @Test
    public void rejects_invalid_prediction_batches() {
        PredictionBatch pb = newPredictionBatch(now, new Prediction(now, -1));
        pb.sourceTimestamp = null;
        pb.utilizationKey.capacityType = null;

        thrown.expect(ValidationException.class);
        thrown.expectMessage("sourceTimestamp (NotNull)");              // validate fields of PredictionBatch
        thrown.expectMessage("utilizationKey.capacityType (NotNull)");  // validate fields of UtilizationKey
        thrown.expectMessage("predictions[0].spacesAvailable (Min)");   // validate fields of every Prediction
        predictionDao.updatePredictions(pb);
    }


    // interpolation

    @Test
    public void keeps_newest_of_too_fine_grained_predictions() {
        DateTime now = toPredictionResolution(this.now); // ensure that all predictions are rounded to the same value

        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(now, 10),
                new Prediction(now.plusSeconds(1), 11),
                new Prediction(now.plusSeconds(2), 12));
        Collections.shuffle(pb.predictions); // input order does not matter
        predictionDao.updatePredictions(pb);

        assertPredictionEquals(new Prediction(now, 12), pb);
    }

    @Test
    public void does_linear_interpolation_between_too_coarse_grained_predictions() {
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(now, 10),
                new Prediction(now.plus(PREDICTION_RESOLUTION.multipliedBy(3)), 40));
        Collections.shuffle(pb.predictions); // input order does not matter
        predictionDao.updatePredictions(pb);

        pb.predictions = Arrays.asList(
                new Prediction(now, 10),
                new Prediction(now.plus(PREDICTION_RESOLUTION.multipliedBy(1)), 20),
                new Prediction(now.plus(PREDICTION_RESOLUTION.multipliedBy(2)), 30),
                new Prediction(now.plus(PREDICTION_RESOLUTION.multipliedBy(3)), 40));
        assertPredictionsSavedAsIs(pb);
    }

    /**
     * This makes it easier to implement coarse grained prediction algorithms,
     * so that the prediction window will be completely filled, without having to be
     * precise about where the prediction window ends.
     */
    @Test
    public void does_linear_interpolation_also_between_values_outside_the_prediction_window() {
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(now.minusHours(1), 123),
                new Prediction(now.plus(PREDICTION_WINDOW).plusHours(1), 123));
        Collections.shuffle(pb.predictions); // input order does not matter
        predictionDao.updatePredictions(pb);

        assertPredictionEquals("windowStart", new Prediction(now, 123), pb);
        assertPredictionEquals("windowEnd", new Prediction(now.plus(PREDICTION_WINDOW).minus(PREDICTION_RESOLUTION), 123), pb);
    }


    // prediction window

    @Test
    public void cannot_predict_when_there_are_no_predictions() {
        PredictionBatch pb = newPredictionBatch(now, new Prediction(now, 123));

        assertPredictionDoesNotExist(now, pb);
    }

    @Test
    public void cannot_predict_beyond_the_prediction_window() {
        DateTime withinWindow = now.plus(PREDICTION_WINDOW).minus(PREDICTION_RESOLUTION);
        DateTime outsideWindow = now.plus(PREDICTION_WINDOW);
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(now, 5),
                new Prediction(withinWindow, 10),
                new Prediction(outsideWindow, 20));
        predictionDao.updatePredictions(pb);

        assertPredictionEquals("inRange", new Prediction(withinWindow, 10), pb);
        assertPredictionDoesNotExist("outsideRange", outsideWindow, pb);
        assertPredictionEquals("overflowCheck", new Prediction(outsideWindow.minus(PREDICTION_WINDOW), 5), pb);
    }

    @Test
    public void cannot_predict_into_past() {
        DateTime past = now.minus(PREDICTION_RESOLUTION);
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(past, 20),
                new Prediction(now, 10),
                new Prediction(past.plus(PREDICTION_WINDOW), 5));
        predictionDao.updatePredictions(pb);

        assertPredictionEquals("inRange", new Prediction(now, 10), pb);
        assertPredictionDoesNotExist("outsideRange", past, pb);
        assertPredictionEquals("overflowCheck", new Prediction(past.plus(PREDICTION_WINDOW), 5), pb);
    }

    @Test
    public void predictions_are_timezone_independent() { // i.e. everything in database is in UTC
        PredictionBatch pb = newPredictionBatch(
                now.withZone(DateTimeZone.forOffsetHours(7)),
                new Prediction(now.withZone(DateTimeZone.forOffsetHours(8)), 123)
        );
        predictionDao.updatePredictions(pb);

        assertPredictionEquals(new Prediction(now, 123), pb);
    }


    // uniqueness

    @Test
    public void predictions_are_facility_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.facilityId = dummies.createFacility();
        predictionDao.updatePredictions(pb1);

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.facilityId = dummies.createFacility();
        predictionDao.updatePredictions(pb2);

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }

    @Test
    public void predictions_are_capacity_type_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.capacityType = ELECTRIC_CAR;
        predictionDao.updatePredictions(pb1);

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.capacityType = MOTORCYCLE;
        predictionDao.updatePredictions(pb2);

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }

    @Test
    public void predictions_are_usage_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.usage = HSL_TRAVEL_CARD;
        predictionDao.updatePredictions(pb1);

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.usage = COMMERCIAL;
        predictionDao.updatePredictions(pb2);

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }


    // helpers

    private PredictionBatch newPredictionBatch(DateTime sourceTimestamp, Prediction... predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.utilizationKey.facilityId = facilityId;
        batch.utilizationKey.capacityType = CAR;
        batch.utilizationKey.usage = PARK_AND_RIDE;
        batch.sourceTimestamp = sourceTimestamp;
        Collections.addAll(batch.predictions, predictions);
        return batch;
    }

    private void assertPredictionsSavedAsIs(PredictionBatch pb) {
        List<Prediction> predictions = pb.predictions;
        for (int i = 0; i < predictions.size(); i++) {
            assertPredictionEquals("prediction[" + i + "]", predictions.get(i), pb);
        }
    }

    private void assertPredictionEquals(Prediction expected, PredictionBatch pb) {
        assertPredictionEquals("prediction", expected, pb);
    }

    private void assertPredictionEquals(String message, Prediction expected, PredictionBatch pb) {
        Optional<Prediction> actual = predictionDao.getPrediction(pb.utilizationKey, expected.timestamp);
        assertThat(actual).as(message).isNotEqualTo(Optional.empty());
        assertThat(actual.get().timestamp).as(message + ".timestamp").isEqualTo(toPredictionResolution(expected.timestamp));
        assertThat(actual.get().spacesAvailable).as(message + ".spacesAvailable").isEqualTo(expected.spacesAvailable);
    }

    private void assertPredictionDoesNotExist(DateTime time, PredictionBatch pb) {
        assertPredictionDoesNotExist("prediction", time, pb);
    }

    private void assertPredictionDoesNotExist(String message, DateTime time, PredictionBatch pb) {
        Optional<Prediction> outsideRange = predictionDao.getPrediction(pb.utilizationKey, time);
        assertThat(outsideRange).as(message).isEqualTo(Optional.empty());
    }
}
