// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.stream.Stream;

public class PredictionService {

    @Inject
    FacilityRepository facilityRepository;

    @Inject
    PredictionRepository predictionRepository;

    public void updatePredictions() {
        Predictor predictor = new SameAsLatestPredictor();

        PredictorState state = new PredictorState();

        PredictionBatch batch = new PredictionBatch();
        batch.facilityId = 1L;
        batch.capacityType = CapacityType.CAR;
        batch.usage = Usage.PARK_AND_RIDE;

        UtilizationHistory history = new UtilizationHistory() {
            @Override
            public Stream<Utilization> getUpdatesSince(DateTime startExclusive) {
                return facilityRepository.getStatuses(batch.facilityId).stream();
            }
        };

        predictor.predict(state, batch, history);

        predictionRepository.updatePredictions(batch);
    }
}
