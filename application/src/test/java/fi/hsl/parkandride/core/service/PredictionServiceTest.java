// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.Utilization;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PredictionServiceTest extends AbstractDaoTest {

    @Inject
    Dummies dummies;

    @Inject
    FacilityRepository facilityRepository;

    @Inject
    PredictionRepository predictionRepository;

    @Inject
    PredictionService predictionService;

    private final DateTime now = new DateTime();

    @Test
    public void updates_predictions() {
        long facilityId = dummies.createFacility();
        Utilization u = new Utilization();
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.PARK_AND_RIDE;
        u.timestamp = now;
        u.spacesAvailable = 42;
        facilityRepository.insertUtilization(facilityId, Collections.singletonList(u));

        predictionService.updatePredictions();

        Optional<Prediction> prediction = predictionRepository.getPrediction(facilityId, u.capacityType, u.usage, now.plusHours(1));
        assertThat(prediction).as("prediction").isNotEqualTo(Optional.empty());
        assertThat(prediction.get().spacesAvailable).as("prediction.spacesAvailable").isEqualTo(42);
    }
}
