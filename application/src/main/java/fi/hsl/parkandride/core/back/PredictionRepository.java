// Copyright © 2015 HSL

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Prediction;
import fi.hsl.parkandride.core.domain.PredictionBatch;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

import java.util.Optional;

public interface PredictionRepository {

    void updatePredictions(PredictionBatch predictions);

    Optional<Prediction> getPrediction(UtilizationKey utilizationKey, DateTime time);
}
