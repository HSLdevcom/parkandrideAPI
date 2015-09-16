// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class Utilization implements Cloneable {

    @NotNull
    @JsonInclude(NON_NULL)
    public Long facilityId;

    @NotNull
    public CapacityType capacityType;

    @NotNull
    public Usage usage;

    @NotNull
    @JsonSerialize(using = DefaultTimeZoneDateTimeSerializer.class)
    @JsonDeserialize(using = StrictIsoDateTimeDeserializer.class)
    public DateTime timestamp;

    @NotNull
    @Min(0)
    public Integer spacesAvailable;

    @JsonIgnore
    public UtilizationKey getUtilizationKey() {
        return new UtilizationKey(facilityId, capacityType, usage);
    }

    public Utilization copy() {
        try {
            return (Utilization) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
