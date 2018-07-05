// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
