package fi.hsl.parkandride.core.domain;

import java.util.Map;

public class FacilitySummary {

    public final long facilityCount;

    public final Map<CapacityType, Integer> capacities;

    public FacilitySummary(long facilityCount, Map<CapacityType, Integer> capacities) {
        this.facilityCount = facilityCount;
        this.capacities = capacities;
    }

}
