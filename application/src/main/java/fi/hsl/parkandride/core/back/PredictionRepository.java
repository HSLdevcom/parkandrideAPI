// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.domain.prediction.PredictionBatch;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository {

    void updatePredictions(PredictionBatch predictions);

    Optional<PredictionBatch> getPrediction(UtilizationKey utilizationKey, DateTime time);

    List<PredictionBatch> getPredictionsByFacility(Long facilityId, DateTime time);
}
