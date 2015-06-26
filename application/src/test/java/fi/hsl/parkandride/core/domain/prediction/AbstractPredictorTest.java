// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import fi.hsl.parkandride.back.AbstractDaoTest;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.UtilizationRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.Utilization;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;
import org.junit.Before;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPredictorTest extends AbstractDaoTest {

    @Inject private Dummies dummies;
    @Inject private UtilizationRepository utilizationRepository;
    private final Predictor predictor;

    private UtilizationKey utilizationKey;
    private UtilizationHistory utilizationHistory;
    protected PredictorState predictorState;
    protected final DateTime now = new DateTime();

    protected AbstractPredictorTest(Predictor predictor) {
        this.predictor = predictor;
    }

    @Before
    public void init() {
        long facilityId = dummies.createFacility();
        utilizationKey = new UtilizationKey(facilityId, CapacityType.CAR, Usage.PARK_AND_RIDE);
        utilizationHistory = new UtilizationHistoryImpl(utilizationRepository, utilizationKey);
        predictorState = new PredictorState(1L, predictor.getType(), utilizationKey);
    }

    public void insertUtilization(DateTime timestamp, int spacesAvailable) {
        Utilization u = new Utilization();
        u.facilityId = utilizationKey.facilityId;
        u.capacityType = utilizationKey.capacityType;
        u.usage = utilizationKey.usage;
        u.timestamp = timestamp;
        u.spacesAvailable = spacesAvailable;
        utilizationRepository.insertUtilizations(Collections.singletonList(u));
    }

    public List<Prediction> predict() {
        return predictor.predict(predictorState, utilizationHistory);
    }
}
