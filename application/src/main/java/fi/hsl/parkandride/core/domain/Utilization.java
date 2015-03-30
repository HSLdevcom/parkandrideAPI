// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Utilization {

    @NotNull
    @ApiModelProperty(required = true)
    @JsonSerialize(using = DefaultTimeZoneDateTimeSerializer.class)
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(timestamp)
                .addValue(capacityType)
                .addValue(usage)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
