// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.*;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.domain.prediction.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final UtilizationRepository utilizationRepository;
    private final PredictionRepository predictionRepository;
    private final PredictorRepository predictorRepository;
    private final PlatformTransactionManager transactionManager;
    private final Map<String, Predictor> predictorsByType;
    private final FacilityRepository facilityRepository;
    private final LockRepository lockRepository;

    public PredictionService(UtilizationRepository utilizationRepository,
                             PredictionRepository predictionRepository,
                             PredictorRepository predictorRepository,
                             FacilityRepository facilityRepository,
                             PlatformTransactionManager transactionManager,
                             LockRepository lockRepository,
                             Predictor... predictors) {
        this.utilizationRepository = utilizationRepository;
        this.predictionRepository = predictionRepository;
        this.predictorRepository = predictorRepository;
        this.transactionManager = transactionManager;
        this.facilityRepository = facilityRepository;
        this.lockRepository = lockRepository;
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

    /**
     * Get the predictions by facility. Predictions that don't match the current built capacity
     * or usage of the facility are left out.
     *
     * @param facilityId the id of the facility
     * @param time the timestamp for the predictions
     * @return prediction results
     */
    public List<PredictionResult> getPredictionResultByFacility(long facilityId, DateTime time) {
        final Facility facility = facilityRepository.getFacility(facilityId);

        Map<CapacityType, Set<Usage>> usagesByCapacityType = FacilityUtil.usagesByCapacityType(facility);

        return getPredictionsByFacility(facilityId, time)
                .stream()
                .flatMap(pb -> PredictionResult.from(pb).stream())
                .filter(pr -> usagesByCapacityType.getOrDefault(pr.capacityType, emptySet()).contains(pr.usage))
                .filter(pr -> facility.builtCapacity.getOrDefault(pr.capacityType, 0) > 0)
                .collect(toList());
    }

    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes to match PredictionDao.PREDICTION_RESOLUTION
    public void updatePredictions() {
        log.info("updatePredictions");
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED); // TODO: set in Core/JdbcConfiguration
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (Long predictorId : findPredictorsNeedingUpdate()) {
            Optional<Lock> lock = Optional.empty();
            try {
                lock = Optional.of(lockRepository.acquireLock("predictor-" + predictorId, Duration.standardMinutes(1)));
                txTemplate.execute(tx -> {
                    updatePredictor(predictorId);
                    log.debug("Updating predictor {} done", predictorId);
                    return null;
                });
            } catch (LockException e) {
                log.debug("Failed to get lock for updating predictorId {} - another node updates this predictor.", predictorId);
            } catch (Exception e) {
                log.error("Failed to update predictor {}", predictorId, e);
            } finally {
                lock.ifPresent(l -> lockRepository.releaseLock(l));
            }
        }
    }

    private List<Long> findPredictorsNeedingUpdate() {
        List<Long> predictorIds = predictorRepository.findPredictorsNeedingUpdate();
        Collections.shuffle(predictorIds); // distribute the load over all servers
        return predictorIds;
    }

    private void updatePredictor(Long predictorId) {
        final PredictorState state = predictorRepository.getById(predictorId);
        if (state.moreUtilizations == false) {
            log.debug("Another cluster node already updated predictor ID {} (type {} for {}), skipping...", state.predictorId, state.predictorType, state.utilizationKey);
            return;
        }
        log.debug("Going to update predictor: {}", state);
        state.moreUtilizations = false; // by default mark everything as processed, but allow the predictor to override it (and uninstalled predictors get disabled)
        getPredictor(state.predictorType).ifPresent(predictor -> {
            // TODO: consider the update interval of prediction types? or leave that up to the predictor?
            List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryImpl(utilizationRepository, state.utilizationKey), getAvailableMaxCapacity(state));
            // TODO: should we set state.latestUtilization here so that all predictors don't need to remember do it? or will some predictors use different logic for it, for example if they process only part of the updates?
            // TODO: save to prediction log
            log.debug("Got {} predictions. state = {}", predictions.size(), state);
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
            List<Prediction> predictions = predictor.predict(state, new UtilizationHistoryList(utilizationList), getAvailableMaxCapacity(state));
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

    private int getAvailableMaxCapacity(PredictorState state) {
        final UtilizationKey uKey = state.utilizationKey;
        final Facility facility = facilityRepository.getFacility(uKey.facilityId);
        final int builtCapacity = facility.builtCapacity.get(uKey.capacityType);
        final int unavailable = facility.unavailableCapacities.stream()
                .filter(uc -> uc.capacityType == uKey.capacityType && uc.usage == uKey.usage)
                .mapToInt(uc -> uc.capacity).max().orElse(0);
        return builtCapacity - unavailable;
    }
}
