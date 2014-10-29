package fi.hsl.parkandride.core.domain;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Hub {

    public Long id;

    @NotNull
    @Valid
    public MultilingualString name;

    @NotNull
    public Geometry location;

    public Set<Long> facilityIds;

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }

    public Geometry getLocation() {
        return location;
    }

    public Set<Long> getFacilityIds() {
        return facilityIds;
    }
}
