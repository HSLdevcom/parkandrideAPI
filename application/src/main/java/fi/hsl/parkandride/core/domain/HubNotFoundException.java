// Copyright © 2015 HSL

package fi.hsl.parkandride.core.domain;

public class HubNotFoundException extends NotFoundException {

    public HubNotFoundException(long hubId) {
        super("Hub#%s not found", hubId);
    }
}
