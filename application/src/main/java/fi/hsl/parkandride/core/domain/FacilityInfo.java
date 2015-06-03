// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import fi.hsl.parkandride.core.domain.validation.Coordinates;
import fi.hsl.parkandride.core.domain.validation.MinElement;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import org.geolatte.geom.Polygon;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class FacilityInfo implements OperatorEntity {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    @NotNull
    @Coordinates
    public Polygon location;

    @NotNull
    public Long operatorId;

    @NotNull
    public FacilityStatus status;

    @NotNull
    public PricingMethod pricingMethod;

    @Valid
    public MultilingualString statusDescription;

    @NotNullElement
    @MinElement(1)
    @NotNull
    public Map<CapacityType, Integer> builtCapacity = newHashMap();

    /**
     * Summary of unique( pricing[*].usage )
     */
    public NullSafeSortedSet<Usage> usages = new NullSafeSortedSet<>();

    @Override
    public Long operatorId() {
        return operatorId;
    }

}
