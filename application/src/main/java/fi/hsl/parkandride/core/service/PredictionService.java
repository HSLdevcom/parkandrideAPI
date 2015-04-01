// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class PredictionService {

    private final UtilizationRepository utilizationRepository;
    private final PredictionRepository predictionRepository;
    private final PredictorRepository predictorRepository;

    private final ConcurrentMap<String, Predictor> predictorsByType = new ConcurrentHashMap<>();

    public PredictionService(UtilizationRepository utilizationRepository, PredictionRepository predictionRepository, PredictorRepository predictorRepository) {
        this.utilizationRepository = utilizationRepository;
        this.predictionRepository = predictionRepository;
        this.predictorRepository = predictorRepository;
    }

    @TransactionalWrite
    public void signalUpdateNeeded(List<Utilization> utilizations) {
        utilizations.stream()
                .map(Utilization::getUtilizationKey)
                .distinct()
                .forEach(predictorRepository::markPredictorsNeedAnUpdate);
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
        predictorRepository.enablePrediction(predictorType, utilizationKey);
    }

    public Optional<Prediction> getPrediction(UtilizationKey utilizationKey, DateTime time) {
        return predictionRepository.getPrediction(utilizationKey, time);
    }

    @TransactionalWrite
    public void updatePredictions() {
        // TODO: block other servers from processing the same work set (use a message queue? choose one at random and lock it?)
        // TODO: consider the update interval of prediction types? or leave that up to the predictor?

        for (PredictorState state : predictorRepository.findPredictorsNeedingUpdate()) {
            state.moreUtilizations = false; // by default mark everything as processed, but allow the predictor to override it
            Predictor predictor = getPredictor(state.predictorType);
            List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryImpl(state.utilizationKey));
            // TODO: save to prediction log
            predictionRepository.updatePredictions(toPredictionBatch(state, predictions));
            predictorRepository.save(state);
        }
    }

    private static PredictionBatch toPredictionBatch(PredictorState state, List<Prediction> predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.utilizationKey = state.utilizationKey;
        batch.sourceTimestamp = state.latestUtilization;
        batch.predictions = predictions;
        return batch;
    }

    private class UtilizationHistoryImpl implements UtilizationHistory {
        private final UtilizationKey utilizationKey;

        public UtilizationHistoryImpl(UtilizationKey utilizationKey) {
            this.utilizationKey = utilizationKey;
        }

        @Override
        public Stream<Utilization> getUpdatesSince(DateTime startExclusive) {
            DateTime start = startExclusive.plusMillis(1);
            DateTime end = new DateTime().plusYears(1);
            return utilizationRepository.findUtilizationsBetween(utilizationKey, start, end).stream();
        }
    }
}
