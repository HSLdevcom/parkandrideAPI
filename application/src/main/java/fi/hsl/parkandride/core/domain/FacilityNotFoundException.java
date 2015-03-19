// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public class FacilityNotFoundException extends NotFoundException {

    public FacilityNotFoundException(long facilityId) {
        super("Facility#%s not found", facilityId);
    }
}
