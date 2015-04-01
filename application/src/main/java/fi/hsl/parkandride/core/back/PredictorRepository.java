// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.UtilizationKey;

public interface PredictorRepository {

    PredictorState enablePrediction(String predictorType, UtilizationKey utilizationKey);
}
