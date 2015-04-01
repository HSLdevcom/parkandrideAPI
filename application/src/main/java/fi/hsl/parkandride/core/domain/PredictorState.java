// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PredictorState {

    @NotNull public final Long predictorId;
    @NotNull public final String predictorType;
    @NotNull @Valid public final UtilizationKey utilizationKey;

    @NotNull public DateTime latestUtilization = new DateTime(0);
    public boolean moreUtilizations = true;
    @NotNull public String internalState = "";

    public PredictorState(Long predictorId, String predictorType, UtilizationKey utilizationKey) {
        this.predictorId = predictorId;
        this.predictorType = predictorType;
        this.utilizationKey = utilizationKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PredictorState)) {
            return false;
        }
        PredictorState that = (PredictorState) obj;
        return Objects.equals(this.predictorId, that.predictorId)
                && Objects.equals(this.predictorType, that.predictorType)
                && Objects.equals(this.utilizationKey, that.utilizationKey)
                && Objects.equals(this.latestUtilization, that.latestUtilization)
                && Objects.equals(this.moreUtilizations, that.moreUtilizations)
                && Objects.equals(this.internalState, that.internalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictorId, predictorType, utilizationKey, latestUtilization, moreUtilizations, internalState);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("predictorId", predictorId)
                .add("predictorType", predictorType)
                .add("utilizationKey", utilizationKey)
                .add("latestUtilization", latestUtilization)
                .add("moreUtilizations", moreUtilizations)
                .add("internalState", internalState)
                .toString();
    }
}
