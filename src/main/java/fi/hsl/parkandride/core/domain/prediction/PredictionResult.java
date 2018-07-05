// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.google.common.base.MoreObjects;
import fi.hsl.parkandride.core.domain.UtilizationKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class PredictionResult extends BasePredictionResult {

    public Long facilityId;

    public static List<PredictionResult> from(Optional<PredictionBatch> batch) {
        List<PredictionResult> results = new ArrayList<>();
        batch.ifPresent(pb -> results.addAll(from(pb)));
        return results;
    }

    public static List<PredictionResult> from(PredictionBatch batch) {
        return batch.predictions.stream()
                .map(p -> from(batch.utilizationKey, p))
                .collect(toList());
    }

    public static PredictionResult from(UtilizationKey utilizationKey, Prediction prediction) {
        PredictionResult result = new PredictionResult();
        result.facilityId = utilizationKey.facilityId;
        result.capacityType = utilizationKey.capacityType;
        result.usage = utilizationKey.usage;
        result.timestamp = prediction.timestamp;
        result.spacesAvailable = prediction.spacesAvailable;
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .add("timestamp", timestamp)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
