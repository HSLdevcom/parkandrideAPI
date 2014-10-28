package fi.hsl.parkandride.core.domain;

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Hub {

    public Long id;

    @NotBlank
    @Length(max=255)
    public String name;

    @NotNull
    public Geometry location;

    public Set<Long> facilityIds;

}
