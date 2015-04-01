// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.inject.Inject;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PredictionServiceTest extends AbstractDaoTest {

    @Inject Dummies dummies;
    @Inject UtilizationRepository utilizationRepository;
    @Inject PredictionService predictionService;

    private final DateTime now = new DateTime();
    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
        Utilization u = newUtilization(facilityId, now, 0);
        predictionService.enablePrediction(SameAsLatestPredictor.TYPE, u.getUtilizationKey());
    }

    @After
    public void resetRegisteredPredictors() {
        predictionService.registerPredictor(new SameAsLatestPredictor());
    }

    @Test
    public void updates_predictions() {
        predictionService.registerPredictor(new SameAsLatestPredictor());
        Utilization u = newUtilization(facilityId, now, 42);
        registerUtilizations(u);

        predictionService.updatePredictions();

        Optional<Prediction> prediction = predictionService.getPrediction(u.getUtilizationKey(), now.plusHours(1));
        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().spacesAvailable).as("prediction.spacesAvailable").isEqualTo(42);
    }

    @Test
    public void saves_predictor_state_between_updates() {
        List<String> spy = new ArrayList<>();
        predictionService.registerPredictor(new SameAsLatestPredictor() {
            @Override
            public List<Prediction> predict(PredictorState state, UtilizationHistory history) {
                spy.add(state.internalState + "@" + state.latestUtilization);
                super.predict(state, history);
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
        predictionService.registerPredictor(predictor);

        registerUtilizations(newUtilization(facilityId, now, 42));
        predictionService.updatePredictions();
        predictionService.updatePredictions();

        verify(predictor, times(1)).predict(Matchers.<PredictorState>any(), Matchers.<UtilizationHistory>any());
    }


    // helpers

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
}
