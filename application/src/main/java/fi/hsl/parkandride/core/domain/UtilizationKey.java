// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

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
}
