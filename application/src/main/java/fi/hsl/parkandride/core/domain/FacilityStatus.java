package fi.hsl.parkandride.core.domain;


import javax.validation.constraints.NotNull;

import org.joda.time.Instant;

@SpacesAvailableOrStatusRequired
public class FacilityStatus {
    @NotNull
    public Instant timestamp;

    @NotNull
    public CapacityType capacityType;

    public Integer spacesAvailable;
    public FacilityStatusEnum status;
}
