// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import fi.hsl.parkandride.core.domain.validation.NotNullElement;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class RegionWithHubs extends Region {
    @Nonnull
    @NotNullElement
    public Set<Long> hubIds = new HashSet<>();

    public RegionWithHubs() {}
    public RegionWithHubs(Region region, Set<Long> hubIds) {
        this.id = region.id;
        this.name = region.name;
        this.hubIds = hubIds;
    }
}
