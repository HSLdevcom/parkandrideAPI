// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;


import com.google.common.base.MoreObjects;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PredictionBatch {

    @NotNull @Valid public UtilizationKey utilizationKey = new UtilizationKey();
    @NotNull public DateTime sourceTimestamp;
    @NotNull @Valid public List<Prediction> predictions = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PredictionBatch)) {
            return false;
        }
        PredictionBatch that = (PredictionBatch) obj;
        return Objects.equals(this.utilizationKey, that.utilizationKey)
                && Objects.equals(this.sourceTimestamp, that.sourceTimestamp)
                && Objects.equals(this.predictions, that.predictions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(utilizationKey, sourceTimestamp, predictions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(utilizationKey)
                .add("sourceTimestamp", sourceTimestamp)
                .add("predictions", predictions)
                .toString();
    }
}
