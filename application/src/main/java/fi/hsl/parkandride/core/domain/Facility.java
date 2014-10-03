package fi.hsl.parkandride.core.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.NotBlank;

public class Facility {

    public Long id;

    @NotBlank
    public String name;

    @NotNull
    public Geometry border;

    public Set<String> aliases = new HashSet<>();

    @Valid
    public Map<CapacityType, Capacity> capacities = new HashMap<>();

}
