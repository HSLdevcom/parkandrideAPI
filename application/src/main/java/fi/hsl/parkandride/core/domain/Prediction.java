// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Prediction {

    @NotNull public final DateTime timestamp;
    @Min(0) public final int spacesAvailable;

    public Prediction(DateTime timestamp, int spacesAvailable) {
        this.timestamp = timestamp;
        this.spacesAvailable = spacesAvailable;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .addValue(timestamp)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
