// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredictionService {

    private final FacilityRepository facilityRepository;
    private final PredictionRepository predictionRepository;

    private final ConcurrentMap<String, Predictor> predictorsByType = new ConcurrentHashMap<>();
    private final List<PredictorState> predictorStates = new CopyOnWriteArrayList<>(); // TODO: remove me

    public PredictionService(FacilityRepository facilityRepository, PredictionRepository predictionRepository) {
        this.facilityRepository = facilityRepository;
        this.predictionRepository = predictionRepository;
    }

    public void registerPredictor(Predictor predictor) {
        predictorsByType.put(predictor.getType(), predictor);
    }

    private Predictor getPredictor(String predictorType) {
        return predictorsByType.computeIfAbsent(predictorType, k -> {
            throw new IllegalArgumentException("Predictor not found: " + predictorType);
        });
    }

    public void enablePrediction(String predictorType, UtilizationKey utilizationKey) {
        // TODO: save to database
        predictorStates.add(new PredictorState(123L, predictorType, utilizationKey));
    }

    @TransactionalWrite
    public void updatePredictions() {
        // TODO: block other servers from processing the same work set (use a message queue? choose one at random and lock it?)
        // TODO: consider the update interval of prediction types? or leave that that to the predictor?

        for (PredictorState state : getPredictionsNeedingUpdate()) {
            Predictor predictor = getPredictor(state.predictorType);
            List<Prediction> predictions = predictor.predict(state, new PredictorUtilizationHistory(state));
            // TODO: save to prediction log
            predictionRepository.updatePredictions(toPredictionBatch(state, predictions));
            savePredictorState(state);
        }
    }

    private List<PredictorState> getPredictionsNeedingUpdate() {
        // TODO: search from database
        return predictorStates.stream()
                .filter(state -> new PredictorUtilizationHistory(state).hasUpdatesSince(state.latestUtilization))
                .collect(Collectors.toList());
    }

    private void savePredictorState(PredictorState state) {
        // TODO: save to database
    }

    private static PredictionBatch toPredictionBatch(PredictorState state, List<Prediction> predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.utilizationKey = state.utilizationKey;
        batch.sourceTimestamp = state.latestUtilization;
        batch.predictions = predictions;
        return batch;
    }

    private class PredictorUtilizationHistory implements UtilizationHistory {
        private final PredictorState state;

        public PredictorUtilizationHistory(PredictorState state) {
            this.state = state;
        }

        @Override
        public boolean hasUpdatesSince(DateTime startExclusive) {
            return getUpdatesSince(state.latestUtilization).findAny().isPresent();
        }

        @Override
        public Stream<Utilization> getUpdatesSince(DateTime startExclusive) {
            // TODO: more effective search
            return facilityRepository.getStatuses(state.utilizationKey.facilityId).stream()
                    .filter(u -> u.timestamp.isAfter(startExclusive));
        }
    }
}
