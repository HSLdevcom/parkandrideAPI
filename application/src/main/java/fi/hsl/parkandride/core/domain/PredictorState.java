// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class PredictorState {

    @NotNull public Long predictorId;
    @NotNull public final String predictorType;
    @NotNull public final Long facilityId;
    @NotNull public final CapacityType capacityType;
    @NotNull public final Usage usage;

    @NotNull public DateTime latestUtilization = new DateTime(0);
    public boolean moreUtilizations = true;
    @NotNull public String internalState = "";

    public PredictorState(String predictorType, Long facilityId, CapacityType capacityType, Usage usage) {
        this.predictorType = predictorType;
        this.facilityId = facilityId;
        this.capacityType = capacityType;
        this.usage = usage;
    }
}
