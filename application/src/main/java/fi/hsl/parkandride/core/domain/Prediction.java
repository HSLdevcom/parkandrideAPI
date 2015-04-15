// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;


import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Prediction {

    @NotNull public final DateTime timestamp;
    @Min(0) public final int spacesAvailable;

    public Prediction(DateTime timestamp, int spacesAvailable) {
        this.timestamp = timestamp;
        this.spacesAvailable = spacesAvailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Prediction)) {
            return false;
        }
        Prediction that = (Prediction) obj;
        return Objects.equals(this.timestamp, that.timestamp)
                && Objects.equals(this.spacesAvailable, that.spacesAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, spacesAvailable);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .addValue(timestamp)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
