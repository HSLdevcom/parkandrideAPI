// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Objects;

import static java.util.Comparator.*;

public class UnavailableCapacity {

    public static Comparator<UnavailableCapacity> COMPARATOR =
            comparing(UnavailableCapacity::getCapacityType, nullsLast(naturalOrder()))
            .thenComparing(UnavailableCapacity::getUsage, nullsLast(naturalOrder()));

    @NotNull
    public CapacityType capacityType;

    @NotNull
    public Usage usage;

    @Min(0)
    public int capacity;

    public UnavailableCapacity() {}

    public UnavailableCapacity(CapacityType capacityType, Usage usage, int capacity) {
        this.capacityType = capacityType;
        this.usage = usage;
        this.capacity = capacity;
    }

    @Override
    public int hashCode() {
        int hashCode = (capacityType == null ? 0 : capacityType.hashCode());
        hashCode = 31*hashCode + (usage == null ? 0 : usage.hashCode());
        hashCode = 31*hashCode + capacity;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof UnavailableCapacity) {
            UnavailableCapacity other = (UnavailableCapacity) obj;
            return Objects.equals(this.capacityType, other.capacityType) &&
                    Objects.equals(this.usage, other.usage) &&
                    this.capacity == other.capacity;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .add("capacity", capacity)
                .toString();
    }


    public CapacityType getCapacityType() {
        return capacityType;
    }

    public Usage getUsage() {
        return usage;
    }

    public int getCapacity() {
        return capacity;
    }
}
