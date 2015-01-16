package fi.hsl.parkandride.core.domain;

import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UnavailableCapacity {

    @NotNull
    public CapacityType capacityType;

    @NotNull
    public Usage usage;

    @Min(0)
    public int capacity;

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

}
