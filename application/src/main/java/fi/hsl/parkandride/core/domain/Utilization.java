// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

public class Utilization {

    @NotNull
    public DateTime timestamp;

    @NotNull
    public CapacityType capacityType;

    @NotNull
    public Usage usage;

    @NotNull
    @Min(0)
    public Integer spacesAvailable;

}
