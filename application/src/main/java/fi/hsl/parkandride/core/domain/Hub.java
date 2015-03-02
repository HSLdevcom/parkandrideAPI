package fi.hsl.parkandride.core.domain;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Point;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import fi.hsl.parkandride.core.domain.validation.WGS84Coordinates;

public class Hub {

    public Long id;

    @ApiModelProperty(required = true)
    @NotNull
    @Valid
    public MultilingualString name;

    @ApiModelProperty(required = true)
    @NotNull
    @WGS84Coordinates
    public Point location;

    @NotNull
    @NotNullElement
    public Set<Long> facilityIds = Sets.newHashSet();

    @Valid
    public Address address;

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public Set<Long> getFacilityIds() {
        return facilityIds;
    }

    public Address getAddress() {
        return address;
    }
}
