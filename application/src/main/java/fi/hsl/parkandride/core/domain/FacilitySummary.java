// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import java.util.Map;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class FacilitySummary {

    @ApiModelProperty(required = true, value="Count of matching facilities")
    public final long facilityCount;

    @ApiModelProperty(required = true, value="Sum of matching facilities' built capacity by CapacityType")
    public final Map<CapacityType, Integer> capacities;

    public FacilitySummary(long facilityCount, Map<CapacityType, Integer> capacities) {
        this.facilityCount = facilityCount;
        this.capacities = capacities;
    }

}
