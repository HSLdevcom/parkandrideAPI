// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

public class PredictorState {

    public final String predictorType;
    public final long facilityId;
    public final CapacityType capacityType;
    public final Usage usage;

    public DateTime latestUtilization = new DateTime(0);
    public String internalState = "";

    public PredictorState(String predictorType, long facilityId, CapacityType capacityType, Usage usage) {
        this.predictorType = predictorType;
        this.facilityId = facilityId;
        this.capacityType = capacityType;
        this.usage = usage;
    }
}
