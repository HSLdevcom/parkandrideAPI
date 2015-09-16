// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.PredictorState;

import java.util.List;

public interface PredictorRepository {

    Long enablePredictor(String predictorType, UtilizationKey utilizationKey);

    void save(PredictorState state);

    PredictorState getById(Long predictorId);

    PredictorState getForUpdate(Long predictorId);

    List<PredictorState> findAllPredictors();

    List<Long> findPredictorsNeedingUpdate();

    void markPredictorsNeedAnUpdate(UtilizationKey utilizationKey);
}
