// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class UtilizationKey {

    @NotNull public Long facilityId;
    @NotNull public CapacityType capacityType;
    @NotNull public Usage usage;

    public UtilizationKey(Long facilityId, CapacityType capacityType, Usage usage) {
        this.facilityId = facilityId;
        this.capacityType = capacityType;
        this.usage = usage;
    }

    public UtilizationKey() {
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UtilizationKey)) {
            return false;
        }
        UtilizationKey that = (UtilizationKey) obj;
        return Objects.equals(this.facilityId, that.facilityId)
                && Objects.equals(this.capacityType, that.capacityType)
                && Objects.equals(this.usage, that.usage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId, capacityType, usage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityId)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .toString();
    }
}
