// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;
import org.junit.After;
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

    @Inject Dummies dummies;
    @Inject PredictionService predictionService;

    private final DateTime now = new DateTime();
    private long facilityId;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
        Utilization u = newUtilization(now, 0);
        predictionService.enablePrediction(SameAsLatestPredictor.TYPE, u.getUtilizationKey(facilityId));
    }

    @After
    public void resetRegisteredPredictors() {
        predictionService.registerPredictor(new SameAsLatestPredictor());
    }

    @Test
    public void updates_predictions() {
        predictionService.registerPredictor(new SameAsLatestPredictor());
        Utilization u = newUtilization(now, 42);
        predictionService.registerUtilizations(facilityId, Collections.singletonList(u));

        predictionService.updatePredictions();

        Optional<Prediction> prediction = predictionService.getPrediction(u.getUtilizationKey(facilityId), now.plusHours(1));
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

        predictionService.registerUtilizations(facilityId, Collections.singletonList(newUtilization(now.plusHours(1), 10)));
        predictionService.updatePredictions();
        predictionService.registerUtilizations(facilityId, Collections.singletonList(newUtilization(now.plusHours(2), 20)));
        predictionService.updatePredictions();
        predictionService.registerUtilizations(facilityId, Collections.singletonList(newUtilization(now.plusHours(3), 30)));
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

        predictionService.registerUtilizations(facilityId, Collections.singletonList(newUtilization(now, 42)));
        predictionService.updatePredictions();
        predictionService.updatePredictions();

        verify(predictor, times(1)).predict(Matchers.<PredictorState>any(), Matchers.<UtilizationHistory>any());
    }


    // helpers

    private static Utilization newUtilization(DateTime now, int spacesAvailable) {
        Utilization u = new Utilization();
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.PARK_AND_RIDE;
        u.timestamp = now;
        u.spacesAvailable = spacesAvailable;
        return u;
    }
}
