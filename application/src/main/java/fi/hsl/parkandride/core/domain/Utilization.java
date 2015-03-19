// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

public class Utilization {

    @NotNull
    @ApiModelProperty(required = true)
    public DateTime timestamp;

    @NotNull
    @ApiModelProperty(required = true)
    public CapacityType capacityType;

    @NotNull
    @ApiModelProperty(required = true)
    public Usage usage;

    @NotNull
    @Min(0)
    @ApiModelProperty(required = true)
    public Integer spacesAvailable;

}
