package fi.hsl.parkandride.core.domain;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public int hashCode() {
        int hashCode = (capacityType == null ? 0 : capacityType.hashCode());
        hashCode = 31*hashCode + (usage == null ? 0 : usage.hashCode());
        hashCode = 31*hashCode + capacity;
        return hashCode;
    }

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
