// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class FacilityCapacity {
    public final Map<CapacityType, Integer> builtCapacity;
    public final List<UnavailableCapacity> unavailableCapacities;

    public FacilityCapacity(Map<CapacityType, Integer> builtCapacity, List<UnavailableCapacity> unavailableCapacities) {
        this.builtCapacity = Optional.ofNullable(builtCapacity).orElse(emptyMap());
        this.unavailableCapacities = Optional.ofNullable(unavailableCapacities).orElse(emptyList());
    }

    public FacilityCapacity(FacilityCapacityHistory entry) {
        this(entry.builtCapacity, entry.unavailableCapacities);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("builtCapacity", builtCapacity)
                .add("unavailableCapacities", unavailableCapacities)
                .toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityCapacity)) {
            return false;
        }
        FacilityCapacity that = (FacilityCapacity) o;
        return Objects.equal(builtCapacity, that.builtCapacity) &&
                Objects.equal(unavailableCapacities, that.unavailableCapacities);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(builtCapacity, unavailableCapacities);
    }
}
