// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PredictorState {

    @NotNull public Long predictorId;
    @NotNull public final String predictorType;
    @NotNull @Valid public final UtilizationKey utilizationKey;

    @NotNull public DateTime latestUtilization = new DateTime(0);
    public boolean moreUtilizations = true;
    @NotNull public String internalState = "";

    public PredictorState(String predictorType, UtilizationKey utilizationKey) {
        this.predictorType = predictorType;
        this.utilizationKey = utilizationKey;
    }
}
