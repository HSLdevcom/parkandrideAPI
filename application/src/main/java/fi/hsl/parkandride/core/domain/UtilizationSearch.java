// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.google.common.base.MoreObjects;

public class UtilizationSearch {

    @NotNull public Set<Long> facilityIds = new HashSet<>();
    @NotNull public Set<CapacityType> capacityTypes = new HashSet<>();
    @NotNull public Set<Usage> usages = new HashSet<>();
    @NotNull public DateTime start;
    @NotNull public DateTime end;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UtilizationSearch)) {
            return false;
        }
        UtilizationSearch that = (UtilizationSearch) obj;
        return Objects.equals(this.facilityIds, that.facilityIds)
                && Objects.equals(this.capacityTypes, that.capacityTypes)
                && Objects.equals(this.usages, that.usages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityIds, capacityTypes, usages);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("facilityId", facilityIds)
                .add("capacityType", capacityTypes)
                .add("usage", usages)
                .toString();
    }
}
