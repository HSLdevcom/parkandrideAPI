// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.DefaultTimeZoneDateTimeSerializer;
import fi.hsl.parkandride.core.domain.Usage;
import org.joda.time.DateTime;

public abstract class BasePredictionResult {
    public CapacityType capacityType;
    public Usage usage;
    @JsonSerialize(using = DefaultTimeZoneDateTimeSerializer.class)
    public DateTime timestamp;
    public int spacesAvailable;
}
