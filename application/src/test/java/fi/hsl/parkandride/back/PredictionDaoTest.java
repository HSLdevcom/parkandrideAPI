// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Optional;

import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

public class PredictionDaoTest extends AbstractDaoTest {

    @Inject
    Dummies dummies;

    @Inject
    PredictionRepository predictionDao;

    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
    }

    @Test
    public void predict_now() {
        DateTime now = new DateTime();
        PredictionBatch p = new PredictionBatch();
        p.facilityId = facilityId;
        p.capacityType = CAR;
        p.usage = PARK_AND_RIDE;
        p.sourceTimestamp = now;
        p.predictions.add(new Prediction(now, 123));
        predictionDao.updatePredictions(p);

        Optional<Prediction> prediction = predictionDao.getPrediction(p.facilityId, p.capacityType, p.usage, now);

        assertThat(prediction.isPresent()).as("isPresent").isTrue();
        assertThat(prediction.get().timestamp).as("timestamp").isEqualTo(TimeUtil.roundMinutes(5, now));
        assertThat(prediction.get().spacesAvailable).as("spacesAvailable").isEqualTo(123);
    }

    @Test
    public void cannot_predict_when_no_predictions_are_saved() {
        Optional<Prediction> prediction = predictionDao.getPrediction(facilityId, CAR, PARK_AND_RIDE, new DateTime());

        assertThat(prediction.isPresent()).as("isPresent").isFalse();
    }

    // TODO: cannot_predict_further_than_24h
    // TODO: cannot_predict_into_past
    // TODO: predictions_are_capacity_type_specific
    // TODO: predictions_are_usage_specific
    // TODO: predictions_are_facility_specific
    // TODO: predictions_are_timezone_independed (saved internally as UTC)
}
