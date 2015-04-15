// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

public class HubNotFoundException extends NotFoundException {

    public HubNotFoundException(long hubId) {
        super("Hub#%s not found", hubId);
    }
}
