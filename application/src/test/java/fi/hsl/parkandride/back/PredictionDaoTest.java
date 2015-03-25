// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

import static fi.hsl.parkandride.back.PredictionDao.*;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

public class PredictionDaoTest extends AbstractDaoTest {

    @Inject
    Dummies dummies;

    @Inject
    PredictionRepository predictionDao;

    private final DateTime now = new DateTime();
    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
    }

    @Test
    public void predict_now() {
        PredictionBatch pb = newPredictionBatch(now, new Prediction(now, 123));
        predictionDao.updatePredictions(pb);

        Optional<Prediction> prediction = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, now);

        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().timestamp).as("prediction.timestamp").isEqualTo(toPredictionResolution(now));
        assertThat(prediction.get().spacesAvailable).as("prediction.spacesAvailable").isEqualTo(123);
    }

    @Test
    public void cannot_predict_when_there_are_no_predictions() {
        Optional<Prediction> prediction = predictionDao.getPrediction(facilityId, CAR, PARK_AND_RIDE, now);

        assertThat(prediction).as("prediction").isEqualTo(Optional.empty());
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

        Optional<Prediction> inRange = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, withinWindow);
        assertThat(inRange).as("inRange").isNotEqualTo(Optional.empty());
        assertThat(inRange.get().timestamp).as("inRange.timestamp").isEqualTo(toPredictionResolution(withinWindow));
        assertThat(inRange.get().spacesAvailable).as("inRange.spacesAvailable").isEqualTo(10);

        Optional<Prediction> outsideRange = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, outsideWindow);
        assertThat(outsideRange).as("outsideRange").isEqualTo(Optional.empty());

        Optional<Prediction> overflowCheck = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, outsideWindow.minus(PREDICTION_WINDOW));
        assertThat(overflowCheck).as("overflowCheck").isNotEqualTo(Optional.empty());
        assertThat(overflowCheck.get().spacesAvailable).as("overflowCheck.spacesAvailable").isEqualTo(5);
    }

    @Test
    public void cannot_predict_into_past() {
        DateTime past = now.minus(PREDICTION_RESOLUTION);
        PredictionBatch pb = newPredictionBatch(now,
                new Prediction(past, 20),
                new Prediction(now, 10),
                new Prediction(past.plus(PREDICTION_WINDOW), 5));
        predictionDao.updatePredictions(pb);

        Optional<Prediction> inRange = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, now);
        assertThat(inRange).as("inRange").isNotEqualTo(Optional.empty());
        assertThat(inRange.get().timestamp).as("inRange.timestamp").isEqualTo(toPredictionResolution(now));
        assertThat(inRange.get().spacesAvailable).as("inRange.spacesAvailable").isEqualTo(10);

        Optional<Prediction> outsideRange = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, past);
        assertThat(outsideRange).as("outsideRange").isEqualTo(Optional.empty());

        Optional<Prediction> overflowCheck = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, past.plus(PREDICTION_WINDOW));
        assertThat(overflowCheck).as("overflowCheck").isNotEqualTo(Optional.empty());
        assertThat(overflowCheck.get().spacesAvailable).as("overflowCheck.spacesAvailable").isEqualTo(5);
    }

    @Test
    public void predictions_are_timezone_independent() { // i.e. everything in database is in UTC
        PredictionBatch pb = newPredictionBatch(
                now.withZone(DateTimeZone.forOffsetHours(7)),
                new Prediction(now.withZone(DateTimeZone.forOffsetHours(8)), 123)
        );
        predictionDao.updatePredictions(pb);

        Optional<Prediction> prediction = predictionDao.getPrediction(pb.facilityId, pb.capacityType, pb.usage, now);

        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().timestamp).as("prediction.timestamp").isEqualTo(toPredictionResolution(now));
        assertThat(prediction.get().spacesAvailable).as("prediction.spacesAvailable").isEqualTo(123);
    }

    // TODO: predictions_are_facility_specific
    // TODO: predictions_are_usage_specific
    // TODO: predictions_are_capacity_type_specific
    // TODO: linear interpolation between predictions
    // TODO: linear interpolation from sourceTimestamp

    private PredictionBatch newPredictionBatch(DateTime sourceTimestamp, Prediction... predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.facilityId = facilityId;
        batch.capacityType = CAR;
        batch.usage = PARK_AND_RIDE;
        batch.sourceTimestamp = sourceTimestamp;
        Collections.addAll(batch.predictions, predictions);
        return batch;
    }
}
