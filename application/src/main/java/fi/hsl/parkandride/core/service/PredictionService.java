// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final UtilizationRepository utilizationRepository;
    private final PredictionRepository predictionRepository;
    private final PredictorRepository predictorRepository;
    private final PlatformTransactionManager transactionManager;
    private final Map<String, Predictor> predictorsByType;

    public PredictionService(UtilizationRepository utilizationRepository,
                             PredictionRepository predictionRepository,
                             PredictorRepository predictorRepository,
                             PlatformTransactionManager transactionManager,
                             Predictor... predictors) {
        this.utilizationRepository = utilizationRepository;
        this.predictionRepository = predictionRepository;
        this.predictorRepository = predictorRepository;
        this.transactionManager = transactionManager;
        Map<String, Predictor> predictorsByType = new HashMap<>();
        for (Predictor predictor : predictors) {
            predictorsByType.put(predictor.getType(), predictor);
        }
        this.predictorsByType = Collections.unmodifiableMap(predictorsByType);
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

    private Optional<Predictor> getPredictor(String predictorType) {
        Optional<Predictor> predictor = Optional.ofNullable(predictorsByType.get(predictorType));
        if (!predictor.isPresent()) {
            log.warn("Predictor {} not installed", predictorType);
        }
        return predictor;
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
        state.moreUtilizations = false; // by default mark everything as processed, but allow the predictor to override it (and uninstalled predictors get disabled)
        getPredictor(state.predictorType).ifPresent(predictor -> {
            // TODO: consider the update interval of prediction types? or leave that up to the predictor?
            List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryImpl(utilizationRepository, state.utilizationKey));
            // TODO: should we set state.latestUtilization here so that all predictors don't need to remember do it? or will some predictors use different logic for it, for example if they process only part of the updates?
            // TODO: save to prediction log
            predictionRepository.updatePredictions(toPredictionBatch(state, predictions), predictorId);
        });
        predictorRepository.save(state); // save state even if predictor is not present: this disables uninstalled predictors
    }

    @Transactional(readOnly = false, isolation = READ_COMMITTED, propagation = REQUIRES_NEW)
    public void updatePredictionsHistoryForFacility(List<Utilization> utilizationList) {
        utilizationList.stream()
                .map(utilization -> utilization.getUtilizationKey())
                .distinct()
                .flatMap(utilizationKey ->
                        predictorsByType.keySet().stream()
                                .map(predictorType -> predictorRepository.enablePredictor(predictorType, utilizationKey)))
                .forEach(predictorId -> updatePredictionHistoryForPredictor(predictorId, utilizationList));
    }

    private void updatePredictionHistoryForPredictor(Long predictorId, List<Utilization> utilizationList) {
        PredictorState state = predictorRepository.getById(predictorId);
        getPredictor(state.predictorType).ifPresent(predictor -> {
            List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryList(utilizationList));
            predictionRepository.updateOnlyPredictionHistory(toPredictionBatch(state, predictions), predictorId);
        });
    }

    private static PredictionBatch toPredictionBatch(PredictorState state, List<Prediction> predictions) {
        PredictionBatch batch = new PredictionBatch();
        batch.utilizationKey = state.utilizationKey;
        batch.sourceTimestamp = state.latestUtilization;
        batch.predictions = predictions;
        return batch;
    }

}
