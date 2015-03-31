// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.Usage;

public interface PredictorRepository {

    PredictorState enablePrediction(String predictorType, Long facilityId, CapacityType capacityType, Usage usage);
}
