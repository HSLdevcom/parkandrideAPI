// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.prediction.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PredictionServiceTest extends AbstractDaoTest {

    @Inject Dummies dummies;
    @Inject UtilizationRepository utilizationRepository;
    @Inject PredictionRepository predictionRepository;
    @Inject PredictorRepository predictorRepository;
    @Inject FacilityRepository facilityRepository;
    @Inject PlatformTransactionManager transactionManager;
    private PredictionService predictionService;

    private final DateTime now = new DateTime();
    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
    }


    @Test
    public void enables_all_registered_predictors_when_signaling_that_update_is_needed() {
        usePredictor(new SameAsLatestPredictor());
        assertThat(predictorRepository.findAllPredictors()).as("all predictors, before").isEmpty();

        predictionService.signalUpdateNeeded(Collections.singletonList(newUtilization(facilityId, now, 0)));

        assertThat(predictorRepository.findAllPredictors()).as("all predictors, after").isNotEmpty();
    }

    @Test
    public void updates_predictions() {
        usePredictor(new SameAsLatestPredictor());
        Utilization u = newUtilization(facilityId, now, 42);
        registerUtilizations(u);

        predictionService.updatePredictions();

        Optional<PredictionBatch> prediction = predictionService.getPrediction(u.getUtilizationKey(), now.plusHours(1));
        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().predictions.get(0).spacesAvailable).as("prediction.spacesAvailable").isEqualTo(42);
    }

    @Test
    public void saves_predictor_state_between_updates() {
        List<String> spy = new ArrayList<>();
        usePredictor(new SameAsLatestPredictor() {
            @Override
            public List<Prediction> predict(PredictorState state, UtilizationHistory history, int maxCapacity) {
                spy.add(state.internalState + "@" + state.latestUtilization);
                super.predict(state, history, maxCapacity);
                state.internalState += "x";
                return Collections.emptyList();
            }
        });

        registerUtilizations(newUtilization(facilityId, now.plusHours(1), 10));
        predictionService.updatePredictions();
        registerUtilizations(newUtilization(facilityId, now.plusHours(2), 20));
        predictionService.updatePredictions();
        registerUtilizations(newUtilization(facilityId, now.plusHours(3), 30));
        predictionService.updatePredictions();

        assertThat(spy).containsExactly(
                "@" + new DateTime(0),
                "x@" + now.plusHours(1),
                "xx@" + now.plusHours(2));
    }

    @Test
    public void does_not_update_predictions_if_there_is_no_new_utilization_data_since_last_update() {
        Predictor predictor = spy(SameAsLatestPredictor.class);
        usePredictor(predictor);

        registerUtilizations(newUtilization(facilityId, now, 42));
        predictionService.updatePredictions();
        predictionService.updatePredictions();

        verify(predictor, times(1)).predict(Matchers.<PredictorState>any(), Matchers.<UtilizationHistory>any(), Matchers.anyInt());
    }

    @Test
    public void prevents_updating_the_same_predictor_concurrently() throws InterruptedException {
        ConcurrentPredictorsSpy spy = new ConcurrentPredictorsSpy();
        usePredictor(spy);
        registerUtilizations(newUtilization(facilityId, now, 42));

        inParallel(
                predictionService::updatePredictions,
                predictionService::updatePredictions);

        assertThat(spy.getMaxConcurrentPredictors()).as("max concurrent predictors").isEqualTo(1);
    }

    @Test
    public void allows_updating_different_predictors_concurrently() throws InterruptedException {
        ConcurrentPredictorsSpy spy = new ConcurrentPredictorsSpy();
        usePredictor(spy);
        registerUtilizations(Stream.generate(() -> newUtilization(dummies.createFacility(), now, 42))
                .limit(10)
                .toArray(Utilization[]::new));

        inParallel(
                predictionService::updatePredictions,
                predictionService::updatePredictions);

        assertThat(spy.getMaxConcurrentPredictors()).as("max concurrent predictors").isEqualTo(2);
    }


    // helpers

    private void usePredictor(Predictor predictor) {
        predictionService = new PredictionService(utilizationRepository, predictionRepository,
                predictorRepository, facilityRepository, transactionManager, predictor);
    }

    private void registerUtilizations(Utilization... utilizations) {
        List<Utilization> us = Arrays.asList(utilizations);
        utilizationRepository.insertUtilizations(us);
        predictionService.signalUpdateNeeded(us);
    }

    private static Utilization newUtilization(long facilityId, DateTime now, int spacesAvailable) {
        Utilization u = new Utilization();
        u.facilityId = facilityId;
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.PARK_AND_RIDE;
        u.timestamp = now;
        u.spacesAvailable = spacesAvailable;
        return u;
    }

    private static void inParallel(Runnable... commands) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(commands.length);
        List<Future<?>> futures = Stream.of(commands)
                .map(executor::submit)
                .collect(toList());
        List<Exception> exceptions = futures.stream()
                .flatMap(future -> {
                    try {
                        future.get();
                        return Stream.empty();
                    } catch (Exception e) {
                        return Stream.of(e);
                    }
                })
                .collect(toList());
        if (!exceptions.isEmpty()) {
            AssertionError e = new AssertionError("There were " + exceptions.size() + " uncaught exceptions in the background threads");
            exceptions.forEach(e::addSuppressed);
            throw e;
        }
    }

    private static class ConcurrentPredictorsSpy extends SameAsLatestPredictor {
        private final AtomicInteger concurrentPredictors = new AtomicInteger(0);
        private final AtomicInteger maxConcurrentPredictors = new AtomicInteger(0);

        @Override
        public List<Prediction> predict(PredictorState state, UtilizationHistory history, int maxCapacity) {
            int current = concurrentPredictors.incrementAndGet();
            maxConcurrentPredictors.updateAndGet(max -> Math.max(max, current));
            try {
                increaseProbabilityOfConcurrency();
                return super.predict(state, history, maxCapacity);
            } finally {
                concurrentPredictors.decrementAndGet();
            }
        }

        private static void increaseProbabilityOfConcurrency() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public int getMaxConcurrentPredictors() {
            return maxConcurrentPredictors.get();
        }
    }
}
