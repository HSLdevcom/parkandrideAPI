// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Utilization {

    @NotNull
    @ApiModelProperty(required = false, notes = "defaults to facilityId from URI")
    public Long facilityId;

    @NotNull
    @ApiModelProperty(required = true)
    public CapacityType capacityType;

    @NotNull
    @ApiModelProperty(required = true)
    public Usage usage;

    @NotNull
    @ApiModelProperty(required = true)
    @JsonSerialize(using = DefaultTimeZoneDateTimeSerializer.class)
    public DateTime timestamp;

    @NotNull
    @Min(0)
    @ApiModelProperty(required = true)
    public Integer spacesAvailable;

    @JsonIgnore
    public UtilizationKey getUtilizationKey() {
        return new UtilizationKey(facilityId, capacityType, usage);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Utilization)) {
            return false;
        }
        Utilization that = (Utilization) obj;
        return Objects.equals(this.facilityId, that.facilityId)
                && Objects.equals(this.capacityType, that.capacityType)
                && Objects.equals(this.usage, that.usage)
                && Objects.equals(this.timestamp, that.timestamp)
                && Objects.equals(this.spacesAvailable, that.spacesAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId, capacityType, usage, timestamp, spacesAvailable);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .add("timestamp", timestamp)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
