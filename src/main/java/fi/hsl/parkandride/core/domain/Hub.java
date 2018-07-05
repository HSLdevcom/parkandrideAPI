// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.collect.Sets;
import fi.hsl.parkandride.core.domain.validation.Coordinates;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import org.geolatte.geom.Point;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class Hub {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    @NotNull
    @Coordinates
    public Point location;

    @NotNull
    @NotNullElement
    public Set<Long> facilityIds = Sets.newHashSet();

    @Valid
    public Address address;

}
