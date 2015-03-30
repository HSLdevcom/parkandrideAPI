// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Stream;

public class PredictionService {

    private final FacilityRepository facilityRepository;
    private final PredictionRepository predictionRepository;

    // TODO: inject predictors, lookup by id
    public Predictor predictor = new SameAsLatestPredictor();

    // TODO: save to database, lookup by id
    private PredictorState state = new PredictorState();

    public PredictionService(FacilityRepository facilityRepository, PredictionRepository predictionRepository) {
        this.facilityRepository = facilityRepository;
        this.predictionRepository = predictionRepository;
    }

    public void updatePredictions() {
        // TODO
        // - choose which sets of [predictor type, facility id, capacity type, usage] need to have their predictions updated
        //      - update interval of prediction type?
        //      - are there new utilization updates?
        // - block other servers from processing the same work set
        //      - use a message queue?
        //      - choose one at random and lock it?
        // - get predictor state
        // - run predictor
        // - save predictions
        //      - full log
        //      - lookup table
        // - save predictor state

        UtilizationHistory history = new UtilizationHistory() {
            @Override
            public boolean hasUpdatesSince(DateTime startExclusive) {
                return getUpdatesSince(state.latestUtilization).findAny().isPresent();
            }

            @Override
            public Stream<Utilization> getUpdatesSince(DateTime startExclusive) {
                return facilityRepository.getStatuses(state.facilityId).stream()
                        .filter(u -> u.timestamp.isAfter(startExclusive));
            }
        };

        if (history.hasUpdatesSince(state.latestUtilization)) {
            List<Prediction> predictions = predictor.predict(state, history);
            predictionRepository.updatePredictions(toPredictionBatch(state, predictions));
        }
    }

    private static PredictionBatch toPredictionBatch(PredictorState state, List<Prediction> predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.facilityId = state.facilityId;
        batch.capacityType = state.capacityType;
        batch.usage = state.usage;
        batch.sourceTimestamp = state.latestUtilization;
        batch.predictions = predictions;
        return batch;
    }
}
