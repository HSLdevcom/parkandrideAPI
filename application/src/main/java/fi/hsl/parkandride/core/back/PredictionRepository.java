// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import fi.hsl.parkandride.core.domain.Usage;
import org.joda.time.DateTime;

import java.util.Optional;

public interface PredictionRepository {

    void updatePredictions(PredictionBatch predictions);

    Optional<Prediction> getPrediction(long facilityId, CapacityType capacityType, Usage usage, DateTime time);
}
