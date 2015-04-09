// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.UtilizationKey;

import java.util.List;

public interface PredictorRepository {

    PredictorState enablePrediction(String predictorType, UtilizationKey utilizationKey);

    void save(PredictorState state);

    PredictorState getById(Long predictorId);

    List<PredictorState> findPredictorsNeedingUpdate();

    void markPredictorsNeedAnUpdate(UtilizationKey utilizationKey);
}
