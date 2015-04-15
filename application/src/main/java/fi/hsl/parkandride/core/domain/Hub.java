// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Point;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.Coordinates;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;

public class Hub {

    public Long id;

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public MultilingualString name;

    @ApiModelProperty(required = true)
    @NotNull
    @Coordinates
    public Point location;

    @NotNull
    @NotNullElement
    public Set<Long> facilityIds = Sets.newHashSet();

    @Valid
    public Address address;

}
