// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PredictionServiceTest extends AbstractDaoTest {

    @Inject
    Dummies dummies;

    @Inject
    FacilityRepository facilityRepository;

    @Inject
    PredictionRepository predictionRepository;

    private final DateTime now = new DateTime();
    private long facilityId;
    private PredictionService predictionService;

    @Before
    public void initTestData() {
        predictionService = new PredictionService(facilityRepository, predictionRepository);
        facilityId = dummies.createFacility();
    }

    @Test
    public void updates_predictions() {
        predictionService.predictor = new SameAsLatestPredictor();
        Utilization u = newUtilization(now, 42);
        facilityRepository.insertUtilization(facilityId, Collections.singletonList(u));

        predictionService.updatePredictions();

        Optional<Prediction> prediction = predictionRepository.getPrediction(facilityId, u.capacityType, u.usage, now.plusHours(1));
        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().spacesAvailable).as("prediction.spacesAvailable").isEqualTo(42);
    }

    @Test
    public void saves_predictor_state_between_updates() {
        List<String> spy = new ArrayList<>();
        predictionService.predictor = new SameAsLatestPredictor() {
            @Override
            public void predict(PredictorState state, PredictionBatch batch, UtilizationHistory history) {
                spy.add(state.internalState + "@" + state.latestProcessed);
                super.predict(state, batch, history);
                state.internalState += "x";
            }
        };

        facilityRepository.insertUtilization(facilityId, Collections.singletonList(newUtilization(now.plusHours(1), 10)));
        predictionService.updatePredictions();
        facilityRepository.insertUtilization(facilityId, Collections.singletonList(newUtilization(now.plusHours(2), 20)));
        predictionService.updatePredictions();
        facilityRepository.insertUtilization(facilityId, Collections.singletonList(newUtilization(now.plusHours(3), 30)));
        predictionService.updatePredictions();

        assertThat(spy).containsExactly(
                "@" + new DateTime(0),
                "x@" + now.plusHours(1),
                "xx@" + now.plusHours(2));
    }

    @Test
    public void does_not_update_predictions_if_there_is_no_utilization_data() {
        Predictor predictor = spy(SameAsLatestPredictor.class);
        predictionService.predictor = predictor;

        predictionService.updatePredictions();
        verifyZeroInteractions(predictor);
    }

    @Test
    public void does_not_update_predictions_if_there_is_no_new_utilization_data_since_last_update() {
        Predictor predictor = spy(SameAsLatestPredictor.class);
        predictionService.predictor = predictor;

        facilityRepository.insertUtilization(facilityId, Collections.singletonList(newUtilization(now, 42)));
        predictionService.updatePredictions();
        predictionService.updatePredictions();

        verify(predictor, times(1)).predict(Matchers.<PredictorState>any(), Matchers.<PredictionBatch>any(), Matchers.<UtilizationHistory>any());
    }

    private static Utilization newUtilization(DateTime now, int spacesAvailable) {
        Utilization u = new Utilization();
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.PARK_AND_RIDE;
        u.timestamp = now;
        u.spacesAvailable = spacesAvailable;
        return u;
    }

    // TODO: multiple prediction sets
}
