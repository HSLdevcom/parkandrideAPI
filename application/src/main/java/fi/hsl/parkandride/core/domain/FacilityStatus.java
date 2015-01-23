package fi.hsl.parkandride.core.domain;


import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import fi.hsl.parkandride.core.domain.validation.SpacesAvailableOrStatusRequired;

@SpacesAvailableOrStatusRequired
public class FacilityStatus {
    @NotNull
    public DateTime timestamp;

    @NotNull
    public CapacityType capacityType;

    public Integer spacesAvailable;
    public FacilityStatusEnum status;
}
