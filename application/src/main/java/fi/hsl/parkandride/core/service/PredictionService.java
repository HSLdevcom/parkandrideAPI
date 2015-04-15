// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final UtilizationRepository utilizationRepository;
    private final PredictionRepository predictionRepository;
    private final PredictorRepository predictorRepository;
    @Inject PlatformTransactionManager transactionManager;

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
                .forEach(this::signalUpdateNeeded);
    }

    private void signalUpdateNeeded(UtilizationKey utilizationKey) {
        // TODO: should we cache that which predictors are already enabled? it would require resetting the cache when DevHelper.deleteAll() is called
        predictorsByType.keySet().stream()
                .forEach(predictorType -> predictorRepository.enablePredictor(predictorType, utilizationKey));
        predictorRepository.markPredictorsNeedAnUpdate(utilizationKey);
    }

    public void registerPredictor(Predictor predictor) {
        predictorsByType.put(predictor.getType(), predictor);
    }

    private Predictor getPredictor(String predictorType) {
        return predictorsByType.computeIfAbsent(predictorType, k -> {
            throw new IllegalArgumentException("Predictor not found: " + predictorType);
        });
    }

    public Optional<PredictionBatch> getPrediction(UtilizationKey utilizationKey, DateTime time) {
        return predictionRepository.getPrediction(utilizationKey, time);
    }

    public List<PredictionBatch> getPredictionsByFacility(Long facilityId, DateTime time) {
        return predictionRepository.getPredictionsByFacility(facilityId, time);
    }

    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes to match PredictionDao.PREDICTION_RESOLUTION
    public void updatePredictions() {
        log.info("updatePredictions");
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED); // TODO: set in Core/JdbcConfiguration
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (Long predictorId : findPredictorsNeedingUpdate()) {
            txTemplate.execute(tx -> {
                updatePredictor(predictorId);
                return null;
            });
        }
    }

    private List<Long> findPredictorsNeedingUpdate() {
        List<Long> predictorIds = predictorRepository.findPredictorsNeedingUpdate();
        Collections.shuffle(predictorIds); // distribute the load over all servers
        return predictorIds;
    }

    private void updatePredictor(Long predictorId) {
        PredictorState state = predictorRepository.getForUpdate(predictorId);
        state.moreUtilizations = false; // by default mark everything as processed, but allow the predictor to override it
        Predictor predictor = getPredictor(state.predictorType);
        // TODO: consider the update interval of prediction types? or leave that up to the predictor?
        List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryImpl(state.utilizationKey));
        // TODO: save to prediction log
        predictionRepository.updatePredictions(toPredictionBatch(state, predictions));
        predictorRepository.save(state);
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
