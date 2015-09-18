// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.Prediction;
import fi.hsl.parkandride.core.domain.prediction.PredictionBatch;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository {

    Hours PREDICTION_WINDOW = Hours.hours(24);
    Minutes PREDICTION_RESOLUTION = Minutes.minutes(5);

    void updatePredictions(PredictionBatch predictions, Long predictorId);

    Optional<PredictionBatch> getPrediction(UtilizationKey utilizationKey, DateTime time);

    List<PredictionBatch> getPredictionsByFacility(Long facilityId, DateTime time);

    List<Prediction> getPredictionHistoryByPredictor(Long predictorId, DateTime start, DateTime end, int forecastDistanceInMinutes);
}
