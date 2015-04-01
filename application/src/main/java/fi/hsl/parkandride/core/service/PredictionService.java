// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class PredictionService {

    private final FacilityRepository facilityRepository;
    private final PredictionRepository predictionRepository;
    private final PredictorRepository predictorRepository;

    private final ConcurrentMap<String, Predictor> predictorsByType = new ConcurrentHashMap<>();

    public PredictionService(FacilityRepository facilityRepository, PredictionRepository predictionRepository, PredictorRepository predictorRepository) {
        this.facilityRepository = facilityRepository;
        this.predictionRepository = predictionRepository;
        this.predictorRepository = predictorRepository;
    }

    @TransactionalWrite
    public void registerUtilizations(long facilityId, List<Utilization> utilizations) {
        // TODO: move the authorization and validation from FacilityService into here and remove the method from FacilityService?
        facilityRepository.insertUtilization(facilityId, utilizations);
        utilizations.forEach(u -> predictorRepository.markPredictorsNeedAnUpdate(u.getUtilizationKey(facilityId)));
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
            // TODO: more effective search
            return facilityRepository.getStatuses(utilizationKey.facilityId).stream()
                    .filter(u -> u.timestamp.isAfter(startExclusive));
        }
    }
}
