// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back.prediction;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.Prediction;
import fi.hsl.parkandride.core.domain.prediction.PredictionBatch;
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
import java.util.stream.IntStream;

import static fi.hsl.parkandride.core.back.PredictionRepository.PREDICTION_RESOLUTION;
import static fi.hsl.parkandride.core.back.PredictionRepository.PREDICTION_WINDOW;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.Usage.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class PredictionDaoTest extends AbstractDaoTest {

    private static final CapacityType CAPACITY_TYPE = CAR;
    private static final Usage USAGE = PARK_AND_RIDE;
    private static final String DUMMY_PREDICTOR_TYPE = "dummy";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Inject Dummies dummies;
    @Inject PredictionRepository predictionDao;
    @Inject PredictorRepository predictorDao;

    private final DateTime now = new DateTime();
    private long facilityId;
    private long predictorId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
        predictorId = predictorDao.enablePredictor(DUMMY_PREDICTOR_TYPE, new UtilizationKey(facilityId, CAPACITY_TYPE, USAGE));
    }

    // basics

    @Test
    public void predict_now() {
        PredictionBatch pb = newPredictionBatch(now, new Prediction(now, 123));
        predictionDao.updatePredictions(pb, predictorId);

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
        predictionDao.updatePredictions(pb, predictorId);
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
        predictionDao.updatePredictions(pb, predictorId);

        assertPredictionEquals(new Prediction(now, 12), pb);
    }

    @Test
    public void does_linear_interpolation_between_too_coarse_grained_predictions() {
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(now, 10),
                new Prediction(now.plus(PREDICTION_RESOLUTION.multipliedBy(3)), 40));
        Collections.shuffle(pb.predictions); // input order does not matter
        predictionDao.updatePredictions(pb, predictorId);

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
        predictionDao.updatePredictions(pb, predictorId);

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
        predictionDao.updatePredictions(pb, predictorId);

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
        predictionDao.updatePredictions(pb, predictorId);

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
        predictionDao.updatePredictions(pb, predictorId);

        assertPredictionEquals(new Prediction(now, 123), pb);
    }


    // uniqueness

    @Test
    public void predictions_are_facility_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.facilityId = dummies.createFacility();
        predictionDao.updatePredictions(pb1, newPredictorId(pb1));

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.facilityId = dummies.createFacility();
        predictionDao.updatePredictions(pb2, newPredictorId(pb2));

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }

    @Test
    public void predictions_are_capacity_type_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.capacityType = ELECTRIC_CAR;
        predictionDao.updatePredictions(pb1, newPredictorId(pb1));

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.capacityType = MOTORCYCLE;
        predictionDao.updatePredictions(pb2, newPredictorId(pb2));

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }

    @Test
    public void predictions_are_usage_specific() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 111));
        pb1.utilizationKey.usage = HSL_TRAVEL_CARD;
        predictionDao.updatePredictions(pb1, newPredictorId(pb1));

        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 222));
        pb2.utilizationKey.usage = COMMERCIAL;
        predictionDao.updatePredictions(pb2, newPredictorId(pb2));

        assertPredictionsSavedAsIs(pb1);
        assertPredictionsSavedAsIs(pb2);
    }


    // aggregate accessors

    @Test
    public void finds_all_predictions_for_a_facility() {
        PredictionBatch pb1 = newPredictionBatch(now, new Prediction(now, 10));
        PredictionBatch pb2 = newPredictionBatch(now, new Prediction(now, 20));
        pb2.utilizationKey.capacityType = MOTORCYCLE;               // included: all capacity types
        PredictionBatch pb3 = newPredictionBatch(now, new Prediction(now, 30));
        pb3.utilizationKey.usage = COMMERCIAL;                      // included: all usages
        PredictionBatch pb4 = newPredictionBatch(now, new Prediction(now, 40));
        pb4.utilizationKey.facilityId = dummies.createFacility();   // excluded: other facilities

        predictionDao.updatePredictions(pb1, newPredictorId(pb1));
        predictionDao.updatePredictions(pb2, newPredictorId(pb2));
        predictionDao.updatePredictions(pb3, newPredictorId(pb3));
        predictionDao.updatePredictions(pb4, newPredictorId(pb4));

        List<PredictionBatch> results = predictionDao.getPredictionsByFacility(pb1.utilizationKey.facilityId, now);

        assertThat(results).containsOnly(
                toPredictionResolution(pb1),
                toPredictionResolution(pb2),
                toPredictionResolution(pb3));
    }


    // history

    @Test
    public void history_of_all_predictions_is_saved_per_forecast_distance() {
        DateTime t1 = now;
        PredictionBatch pb1 = newPredictionBatch(t1,
                new Prediction(t1, 666),
                new Prediction(t1.plusHours(1), 666)
        );
        predictionDao.updatePredictions(pb1, predictorId);

        DateTime t2 = now.plusMinutes(5);
        PredictionBatch pb2 = newPredictionBatch(t2,
                new Prediction(t2, 777),
                new Prediction(t2.plusHours(1), 777)
        );
        predictionDao.updatePredictions(pb2, predictorId);

        int forecastDistanceInMinutes = 15;
        // TODO: this method should be parameterized by predictor instead of utilization key, because there can be multiple predictors working on the same facility
        List<Prediction> predictionHistory = predictionDao.getPredictionHistoryByPredictor(
                predictorId, now, now.plusHours(1), forecastDistanceInMinutes);
        assertThat(predictionHistory).containsOnly(
                new Prediction(toPredictionResolution(t1.plusMinutes(forecastDistanceInMinutes)), 666),
                new Prediction(toPredictionResolution(t2.plusMinutes(forecastDistanceInMinutes)), 777)
        );
    }

    @Test
    public void prediction_history_is_generated_for_all_prediction_distances() {
        PredictionBatch pb1 = newPredictionBatch(now,
                new Prediction(now, 666),
                new Prediction(now.plus(PREDICTION_WINDOW), 666)
        );
        predictionDao.updatePredictions(pb1, predictorId);

        IntStream.range(1, PREDICTION_WINDOW.toStandardMinutes().getMinutes() / PREDICTION_RESOLUTION.getMinutes())
                .mapToObj(counter -> counter * PREDICTION_RESOLUTION.getMinutes())
                .forEach(predictionDistanceInMinutes -> {
                    List<Prediction> predictionHistory = predictionDao.getPredictionHistoryByPredictor(predictorId, now, now.plus(PREDICTION_WINDOW), predictionDistanceInMinutes);
                    assertThat(predictionHistory).as("Prediction history for prediction distance %d minutes", predictionDistanceInMinutes)
                            .containsOnly(new Prediction(toPredictionResolution(now.plusMinutes(predictionDistanceInMinutes)), 666));
                });
    }

    // helpers

    public Long newPredictorId(PredictionBatch pb) {
        return predictorDao.enablePredictor(DUMMY_PREDICTOR_TYPE, pb.utilizationKey);
    }

    private PredictionBatch newPredictionBatch(DateTime sourceTimestamp, Prediction... predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.utilizationKey.facilityId = facilityId;
        batch.utilizationKey.capacityType = CAPACITY_TYPE;
        batch.utilizationKey.usage = USAGE;
        batch.sourceTimestamp = sourceTimestamp;
        Collections.addAll(batch.predictions, predictions);
        return batch;
    }

    private static PredictionBatch toPredictionResolution(PredictionBatch pb) {
        PredictionBatch copy = new PredictionBatch();
        copy.utilizationKey = pb.utilizationKey;
        copy.sourceTimestamp = toPredictionResolution(pb.sourceTimestamp);
        copy.predictions = pb.predictions.stream()
                .map(PredictionDaoTest::toPredictionResolution)
                .collect(toList());
        return copy;
    }

    private static Prediction toPredictionResolution(Prediction p) {
        return new Prediction(toPredictionResolution(p.timestamp), p.spacesAvailable);
    }

    private static DateTime toPredictionResolution(DateTime time) {
        return PredictionDao.toPredictionResolution(time);
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
        Optional<PredictionBatch> actual = predictionDao.getPrediction(pb.utilizationKey, expected.timestamp);
        assertThat(actual).as(message).isNotEqualTo(Optional.empty());
        assertThat(actual.get().predictions).as(message).containsExactly(toPredictionResolution(expected));
    }

    private void assertPredictionDoesNotExist(DateTime time, PredictionBatch pb) {
        assertPredictionDoesNotExist("prediction", time, pb);
    }

    private void assertPredictionDoesNotExist(String message, DateTime time, PredictionBatch pb) {
        Optional<PredictionBatch> outsideRange = predictionDao.getPrediction(pb.utilizationKey, time);
        assertThat(outsideRange).as(message).isEqualTo(Optional.empty());
    }
}
