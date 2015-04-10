// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

public class PredictionResult {

    public Long facilityId;
    public CapacityType capacityType;
    public Usage usage;
    @JsonSerialize(using = DefaultTimeZoneDateTimeSerializer.class)
    public DateTime timestamp;
    public int spacesAvailable;

    public static PredictionResult from(UtilizationKey utilizationKey, Prediction prediction) {
        PredictionResult result = new PredictionResult();
        result.facilityId = utilizationKey.facilityId;
        result.capacityType = utilizationKey.capacityType;
        result.usage = utilizationKey.usage;
        result.timestamp = prediction.timestamp;
        result.spacesAvailable = prediction.spacesAvailable;
        return result;
    }
}
