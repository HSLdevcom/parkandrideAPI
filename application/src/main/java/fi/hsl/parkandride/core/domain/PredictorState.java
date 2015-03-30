// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import org.joda.time.DateTime;

public class PredictorState {

    // TODO: parameterize
    public final String predictorType = SameAsLatestPredictor.TYPE;
    public final long facilityId = 1;
    public final CapacityType capacityType = CapacityType.CAR;
    public final Usage usage = Usage.PARK_AND_RIDE;

    public DateTime latestUtilization = new DateTime(0);
    public String internalState = "";
}
