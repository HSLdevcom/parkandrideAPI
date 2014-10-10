package fi.hsl.parkandride.core.domain;

import java.util.Map;

public class FacilitySummary {

    public final long facilityCount;

    public final Map<CapacityType, Capacity> capacities;

    public FacilitySummary(long facilityCount, Map<CapacityType, Capacity> capacities) {
        this.facilityCount = facilityCount;
        this.capacities = capacities;
    }

    public long getFacilityCount() {
        return facilityCount;
    }

    public Map<CapacityType, Capacity> getCapacities() {
        return capacities;
    }

}
